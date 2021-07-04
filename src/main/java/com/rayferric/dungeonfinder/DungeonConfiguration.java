package com.rayferric.dungeonfinder;

import com.conversantmedia.util.collection.geometry.Point3d;
import com.rayferric.dungeonfinder.welzl.Sphere;
import com.rayferric.dungeonfinder.welzl.Vector3f;
import com.rayferric.dungeonfinder.welzl.Welzl;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class DungeonConfiguration {
    public DungeonConfiguration(@NotNull List<Spawner> spawners) {
        this.spawners = spawners;

        // We must sort the array, so two differently ordered configurations will remain equal
        spawners.sort(Comparator.comparingInt(Spawner::hashCode));

        List<Vector3f> points = new ArrayList<>(spawners.size());
        for (Spawner spawner : spawners) {
            Point3d pos = spawner.getPos();
            points.add(new Vector3f(pos.getCoord(0), pos.getCoord(1), pos.getCoord(2)));
        }

        boundingSphere = Welzl.run(points);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DungeonConfiguration other = (DungeonConfiguration)o;
        return Objects.equals(spawners, other.spawners);
    }

    @Override
    public int hashCode() {
        return Objects.hash(spawners);
    }

    /**
     * Subdivides this configuration into multiple children with one less spawner each.
     *
     * @return list of children configurations
     */
    public List<DungeonConfiguration> subdivide() {
        if (spawners.size() == 1)
            throw new IllegalStateException("Can't subdivide configuration with just one spawner.");

        List<DungeonConfiguration> subConfigs = new ArrayList<>(spawners.size());

        for (int i = 0; i < spawners.size(); i++) {
            List<Spawner> newSpawners = new ArrayList<>(spawners);
            newSpawners.remove(i);
            subConfigs.add(new DungeonConfiguration(newSpawners));
        }

        return subConfigs;
    }

    /**
     * Subdivides the configuration till a valid one that has at least minDungeons spawners is found.<br>
     * Always returns the configuration with the most spawners.
     *
     * @param minDungeons minimum number of dungeons in a configuration
     * @return a valid configuration with the most spawners possible
     */
    public DungeonConfiguration getOptimalValidSubdivision(int minDungeons, float maxDist) {
        Queue<DungeonConfiguration> queue = new LinkedList<>();
        queue.add(this);

        while (!queue.isEmpty()) {
            DungeonConfiguration config = queue.poll();

            if (config.spawners.size() < minDungeons)
                return null;

            if (config.isValid(maxDist))
                return config;

            queue.addAll(config.subdivide());
        }

        return null;
    }

    /**
     * Returns a list of spawner positions in the configuration.
     *
     * @return list of spawner positions
     */
    public List<Spawner> getSpawners() {
        return spawners;
    }

    /**
     * Returns center of the configuration which is an optimal
     * (if not required) place for the player to stand.
     *
     * @return center position
     */
    public Point3d getCenter() {
        Vector3f vec = boundingSphere.getOrigin();
        return new Point3d(vec.getX(), vec.getY(), vec.getZ());
    }

    public boolean isValid(float maxDist) {
        return boundingSphere.getRadius() <= maxDist;
    }


    private final List<Spawner> spawners;
    private final Sphere boundingSphere;
}