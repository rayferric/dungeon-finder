package com.rayferric.dungeonfinder;

import com.conversantmedia.util.collection.geometry.Point3d;
import com.conversantmedia.util.collection.geometry.Rect3d;
import com.conversantmedia.util.collection.spatial.ConcurrentRTree;
import com.conversantmedia.util.collection.spatial.SpatialSearches;
import io.xol.enklume.MinecraftWorld;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

@SuppressWarnings("BusyWait")
public class DungeonFinder {
    public static List<DungeonConfiguration> run(String worldFolderPath, int minX, int maxX, int minZ, int maxZ, int minConfigSize, int numThreads, int reportDelay) throws IOException, InterruptedException {
        ConcurrentRTree<Point3d> dungeonTree = (ConcurrentRTree<Point3d>) SpatialSearches.lockingRTree(new Point3d.Builder());

        ThreadPoolExecutor threadPool = (ThreadPoolExecutor) Executors.newFixedThreadPool(numThreads);

        File worldFolder = new File(worldFolderPath);
        MinecraftWorld world = new MinecraftWorld(worldFolder);

        System.out.println(String.format("Processing %d regions...", (maxX - minX + 1) * (maxZ - minZ + 1)));
        System.out.flush();

        for(int regionX = minX; regionX <= maxX; regionX++) {
            for(int regionZ = minZ; regionZ <= maxZ; regionZ++) {
                threadPool.execute(new FindDungeonsInRegionTask(regionX, regionZ, world, dungeonTree));
            }
        }

        long numTotal = threadPool.getTaskCount(), numComplete;
        long elapsedTime = reportDelay;
        Thread.sleep(reportDelay);
        while((numComplete = threadPool.getCompletedTaskCount()) != numTotal) {
            double remainingTime = 0;
            if(numComplete != 0)
                remainingTime = (double)(elapsedTime * numTotal / numComplete - elapsedTime) / 1000.0;
            String remainingTimeStr = numComplete == 0 ? "?" : Long.toString((long)remainingTime);

            double progress = (double)numComplete / numTotal * 100.0;
            System.out.println(String.format("%d %% - ETA %s s", (long)progress, remainingTimeStr));
            System.out.flush();
            Thread.sleep(reportDelay);
            elapsedTime += reportDelay;
        }
        threadPool.shutdownNow();

        System.out.println(String.format("Found %d dungeons.\nStarted proximity filtering...", dungeonTree.getEntryCount()));
        System.out.flush();

        List<DungeonConfiguration> dungeonConfigs = new ArrayList<>();

        dungeonTree.forEach(point -> {
            double x = point.getCoord(0);
            double y = point.getCoord(1);
            double z = point.getCoord(2);

            double maxDist2 = 32.0; // 2 * 16

            Rect3d rect = new Rect3d(x - maxDist2, y - maxDist2, z - maxDist2, x + maxDist2, y + maxDist2, z + maxDist2);

            List<BlockPos> dungeons = new ArrayList<>();
            dungeons.add(new BlockPos(x, y, z));
            dungeonTree.search(rect, testPoint -> {
                if(testPoint == point)return;
                BlockPos testDungeonPos = new BlockPos(testPoint);
                for(BlockPos dungeonPos : dungeons) {
                    if(dungeonPos.distance(testDungeonPos) > maxDist2)return;
                }
                dungeons.add(testDungeonPos);
            });

            if(dungeons.size() >= minConfigSize) {
                DungeonConfiguration conf = new DungeonConfiguration(dungeons);
                DungeonConfiguration validConf = conf.getFirstValidSubdivision(minConfigSize);
                if(validConf != null && !dungeonConfigs.contains(validConf))dungeonConfigs.add(validConf);
            }
        });

        return dungeonConfigs;
    }
}
