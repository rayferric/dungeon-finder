package com.rayferric.dungeonfinder.task;

import com.conversantmedia.util.collection.geometry.Point3d;
import com.conversantmedia.util.collection.geometry.Rect3d;
import com.conversantmedia.util.collection.spatial.ConcurrentRTree;
import com.rayferric.dungeonfinder.DungeonConfiguration;
import com.rayferric.dungeonfinder.util.BlockPos;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.Lock;

public class FilterProximityTask implements Runnable {
    public FilterProximityTask(Point3d point, ConcurrentRTree<Point3d> dungeonTree,
                               List<DungeonConfiguration> dungeonConfigs, Lock dungeonConfigsLock, int minConfigSize,
                               int maxDist) {
        this.point = point;
        this.dungeonTree = dungeonTree;
        this.dungeonConfigs = dungeonConfigs;
        this.dungeonConfigsLock = dungeonConfigsLock;
        this.minConfigSize = minConfigSize;
        this.maxDist = maxDist;
    }

    @Override
    public void run() {
        // point doesn't need a semaphore as it's not modified anywhere, nor is it publicly exposed
        double x = point.getCoord(0);
        double y = point.getCoord(1);
        double z = point.getCoord(2);

        double maxDist2 = maxDist * 2;

        Rect3d rect = new Rect3d(x - maxDist2, y - maxDist2, z - maxDist2, x + maxDist2, y + maxDist2, z + maxDist2);

        List<BlockPos> dungeons = new ArrayList<>();
        dungeons.add(new BlockPos(x, y, z));
        dungeonTree.search(rect, testPoint -> {
            if(testPoint == point) return;
            BlockPos testDungeonPos = new BlockPos(testPoint);
            for(BlockPos dungeonPos : dungeons) {
                if(dungeonPos.distance(testDungeonPos) > maxDist2) return;
            }
            dungeons.add(testDungeonPos);
        });

        if(dungeons.size() >= minConfigSize) {
            DungeonConfiguration config = new DungeonConfiguration(dungeons, maxDist);
            config = config.getOptimalValidSubdivision(minConfigSize);
            if(config == null) return;

            try {
                dungeonConfigsLock.lock();
                if(!dungeonConfigs.contains(config))
                    dungeonConfigs.add(config);
            } finally {
                dungeonConfigsLock.unlock();
            }
        }
    }

    private final Point3d point;
    private final ConcurrentRTree<Point3d> dungeonTree;
    private final List<DungeonConfiguration> dungeonConfigs;
    private final Lock dungeonConfigsLock;
    private final int minConfigSize, maxDist;
}
