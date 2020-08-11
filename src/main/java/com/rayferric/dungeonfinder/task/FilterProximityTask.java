package com.rayferric.dungeonfinder.task;

import com.conversantmedia.util.collection.geometry.Point3d;
import com.conversantmedia.util.collection.geometry.Rect3d;
import com.conversantmedia.util.collection.spatial.ConcurrentRTree;
import com.rayferric.dungeonfinder.DungeonConfiguration;
import com.rayferric.dungeonfinder.util.BlockPos;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Semaphore;

public class FilterProximityTask implements Runnable {
    public FilterProximityTask(Point3d point, ConcurrentRTree<Point3d> dungeonTree, List<DungeonConfiguration> dungeonConfigs, Semaphore dungeonConfigsSemaphore, int minConfigSize) {
        this.point = point;
        this.dungeonTree = dungeonTree;
        this.dungeonConfigs = dungeonConfigs;
        this.dungeonConfigsSemaphore = dungeonConfigsSemaphore;
        this.minConfigSize = minConfigSize;
    }

    @Override
    public void run() {
        // point doesn't need a semaphore as it's not modified anywhere, nor is it publicly exposed
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
            conf = conf.getOptimalValidSubdivision(minConfigSize);
            if(conf != null) {
                try {
                    dungeonConfigsSemaphore.acquire();
                } catch(InterruptedException e) {
                    e.printStackTrace();
                    System.exit(1);
                }
                if(!dungeonConfigs.contains(conf))
                    dungeonConfigs.add(conf);
                dungeonConfigsSemaphore.release();
            }
        }
    }

    private final Point3d point;
    private final ConcurrentRTree<Point3d> dungeonTree;
    private final List<DungeonConfiguration> dungeonConfigs;
    private final Semaphore dungeonConfigsSemaphore;
    private final int minConfigSize;
}