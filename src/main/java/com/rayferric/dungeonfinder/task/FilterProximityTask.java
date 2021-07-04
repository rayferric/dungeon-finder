package com.rayferric.dungeonfinder.task;

import com.conversantmedia.util.collection.geometry.Point3d;
import com.conversantmedia.util.collection.geometry.Rect3d;
import com.conversantmedia.util.collection.spatial.ConcurrentRTree;
import com.rayferric.dungeonfinder.DungeonConfiguration;
import com.rayferric.dungeonfinder.Spawner;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.Lock;

public class FilterProximityTask implements Runnable {
    public FilterProximityTask(Spawner spawner, ConcurrentRTree<Spawner> spawnerTree,
                               List<DungeonConfiguration> dungeonConfigs, Lock dungeonConfigsLock, int minConfigSize,
                               int maxDist) {
        this.spawner = spawner;
        this.spawnerTree = spawnerTree;
        this.dungeonConfigs = dungeonConfigs;
        this.dungeonConfigsLock = dungeonConfigsLock;
        this.minConfigSize = minConfigSize;
        this.maxDist = maxDist;
    }

    @Override
    public void run() {
        int maxDistOffset = maxDist * 2;

        Point3d pos = spawner.getPos();
        double x = pos.getCoord(0);
        double y = pos.getCoord(1);
        double z = pos.getCoord(2);
        Rect3d searchRect = new Rect3d(x - maxDistOffset, y - maxDistOffset, z - maxDistOffset, x + maxDistOffset,
                y + maxDistOffset, z + maxDistOffset);

        List<Spawner> spawners = new ArrayList<>();
        spawnerTree.search(searchRect, spawners);

        if (spawners.size() >= minConfigSize) { // Drop these here, so we don't have to cope with them later.
            DungeonConfiguration config = new DungeonConfiguration(spawners);
            config = config.getOptimalValidSubdivision(minConfigSize, maxDist);
            if (config == null) return;

            try {
                dungeonConfigsLock.lock();
                if (!dungeonConfigs.contains(config))
                    dungeonConfigs.add(config);
            } finally {
                dungeonConfigsLock.unlock();
            }
        }
    }

    private final Spawner spawner;
    private final ConcurrentRTree<Spawner> spawnerTree;
    private final List<DungeonConfiguration> dungeonConfigs;
    private final Lock dungeonConfigsLock;
    private final int minConfigSize, maxDist;
}
