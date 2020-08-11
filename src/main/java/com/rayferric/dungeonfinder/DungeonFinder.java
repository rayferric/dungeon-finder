package com.rayferric.dungeonfinder;

import com.conversantmedia.util.collection.geometry.Point3d;
import com.conversantmedia.util.collection.spatial.ConcurrentRTree;
import com.conversantmedia.util.collection.spatial.SpatialSearches;
import com.rayferric.dungeonfinder.task.FilterProximityTask;
import com.rayferric.dungeonfinder.task.FindDungeonsInRegionTask;
import io.xol.enklume.MinecraftWorld;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

@SuppressWarnings("BusyWait")
public class DungeonFinder {
    public interface Callback {
        void execute();
    }

    public interface Callback2<A, B> {
        void execute(A a, B b);
    }

    public interface Callback3<A, B, C> {
        void execute(A a, B b, C c);
    }

    /**
     * Defines a callback to fire at the beginning, after the world directory has been opened<br>
     * This lambda is called from the same thread that invoked run(...)<br>
     *
     * @param startCallback    parameter-less callback
     */
    public void onStart(Callback startCallback) {
        this.startCallback = startCallback;
    }

    /**
     * Defines a callback to fire after the region files are read and before the proximity filtering begins<br>
     * • int – the number of dungeons that have been found<br>
     * • long – time elapsed in milliseconds<br>
     * This lambda is called from the same thread that invoked run(...)
     *
     * @param filterCallback    two-parameter callback
     */
    public void onFilter(Callback2<Integer, Long> filterCallback) {
        this.filterCallback = filterCallback;
    }

    /**
     * Defines a callback to fire every time the object reports its current state of computation<br>
     * • long – items complete<br>
     * • long – items total<br>
     * • long – time elapsed in milliseconds<br>
     * This lambda is called from the same thread that invoked run(...)
     *
     * @param reportCallback    three-parameter callback
     */
    public void onReport(Callback3<Long, Long, Long> reportCallback) {
        this.reportCallback = reportCallback;
    }

    /**
     * Finds multi-dungeon configurations in a Minecraft world save<br>
     * The algorithm first searches the world for dungeons<br>
     * Then the proximity filtering stage begins – all the configurations that do not meet the requirements are discarded<br>
     * Both stages are multithreaded
     *
     * @param worldDirectory    path to the input world's directory
     * @param minX              most negative region's X position
     * @param maxX              most positive region's X position
     * @param minZ              most negative region's Z position
     * @param maxZ              most positive region's Z position
     * @param minConfigSize     minimum number of spawners per dungeon configuration
     * @param numThreads        number of threads used to process regions
     * @param reportDelay       delay between individual progress reports in milliseconds
     *
     * @return    list of dungeon configurations that were found
     */
    public List<DungeonConfiguration> run(String worldDirectory, int minX, int maxX, int minZ, int maxZ, int minConfigSize, int numThreads, int reportDelay) throws IOException {
        ConcurrentRTree<Point3d> dungeonTree = (ConcurrentRTree<Point3d>)SpatialSearches.lockingRTree(new Point3d.Builder());

        ThreadPoolExecutor threadPool = (ThreadPoolExecutor)Executors.newFixedThreadPool(numThreads);

        File worldFolder = new File(worldDirectory);
        MinecraftWorld world = new MinecraftWorld(worldFolder);

        if(startCallback != null)startCallback.execute();

        for(int regionX = minX; regionX <= maxX; regionX++) {
            for(int regionZ = minZ; regionZ <= maxZ; regionZ++) {
                threadPool.execute(new FindDungeonsInRegionTask(regionX, regionZ, world, dungeonTree));
            }
        }

        long time = System.currentTimeMillis();
        monitorThreadPool(threadPool, reportDelay);
        if(filterCallback != null)filterCallback.execute(dungeonTree.getEntryCount(), System.currentTimeMillis() - time);

        List<DungeonConfiguration> dungeonConfigs = new ArrayList<>();
        Semaphore dungeonConfigsSemaphore = new Semaphore(1);
        dungeonTree.forEach(point -> threadPool.execute(new FilterProximityTask(point, dungeonTree, dungeonConfigs, dungeonConfigsSemaphore, minConfigSize)));

        // This process doesn't take that long, so drop monitoring in favor of just waiting
        //monitorThreadPool(threadPool, reportDelay);
        threadPool.shutdown();
        try {
            threadPool.awaitTermination(Long.MAX_VALUE, TimeUnit.DAYS);
        } catch(InterruptedException e) {
            e.printStackTrace();
            System.exit(1);
        }

        return dungeonConfigs;
    }

    private Callback startCallback = null;
    private Callback2<Integer, Long> filterCallback = null;
    private Callback3<Long, Long, Long> reportCallback = null;

    private void monitorThreadPool(ThreadPoolExecutor threadPool, int reportDelay) {
        long numTotal = threadPool.getTaskCount(), numComplete;
        long timeElapsed = reportDelay;

        try {
            Thread.sleep(reportDelay);
        } catch(InterruptedException e) {
            e.printStackTrace();
        }
        while((numComplete = threadPool.getCompletedTaskCount()) != numTotal) {
            if(reportCallback != null)reportCallback.execute(numComplete, numTotal, timeElapsed);

            try {
                Thread.sleep(reportDelay);
            } catch(InterruptedException e) {
                e.printStackTrace();
            }
            timeElapsed += reportDelay;
        }
    }
}