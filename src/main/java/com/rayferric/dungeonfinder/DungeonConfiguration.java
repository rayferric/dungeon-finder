package com.rayferric.dungeonfinder;

import com.conversantmedia.util.collection.geometry.Point3d;
import com.rayferric.math.common.Vector3f;
import com.rayferric.math.welzl.Sphere;
import com.rayferric.math.welzl.Welzl;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class DungeonConfiguration {
    public DungeonConfiguration(@NotNull List<Spawner> spawners, int maxDist) {
        this.spawners = spawners;
        this.maxDist = maxDist;

        // We must sort the array, so two differently ordered configurations will remain equal
        spawners.sort(Comparator.comparingInt(Spawner::hashCode));

        List<Vector3f> points = new ArrayList<>(spawners.size());
        for(Spawner spawner : spawners) {
            Point3d pos = spawner.getPos();
            points.add(new Vector3f(pos.getCoord(0), pos.getCoord(1), pos.getCoord(2)));
        }
        Sphere boundingSphere = Welzl.run(points);
        Vector3f origin = boundingSphere.getOrigin();

        center = new Point3d(origin.getX(), origin.getY(), origin.getZ());
        valid = (boundingSphere.getRadius() <= maxDist);
    }

    @Override
    public boolean equals(Object o) {
        if(this == o) return true;
        if(o == null || getClass() != o.getClass()) return false;
        DungeonConfiguration other = (DungeonConfiguration)o;
        return Objects.equals(spawners, other.spawners);
    }

    @Override
    public int hashCode() {
        return Objects.hash(spawners);
    }

    /**
     * Subdivides this configuration into two children with one less spawner each.
     *
     * @return list of two children configurations
     */
    public List<DungeonConfiguration> subdivide() {
        List<DungeonConfiguration> subConfigs = new ArrayList<>(2);

        List<Spawner> newSpawners = new ArrayList<>(spawners);
        newSpawners.remove(newSpawners.size() - 1);
        subConfigs.add(new DungeonConfiguration(newSpawners, maxDist));

        newSpawners = new ArrayList<>(spawners);
        newSpawners.remove(0);
        subConfigs.add(new DungeonConfiguration(newSpawners, maxDist));

        return subConfigs;
    }

    /**
     * Subdivides the configuration till a valid one that has at least minDungeons spawners is found.<br>
     * Always returns the configuration with the most spawners.
     *
     * @param minDungeons minimum number of dungeons in a configuration
     * @return a valid configuration with the most spawners possible
     */
    public DungeonConfiguration getOptimalValidSubdivision(int minDungeons) {
        if(valid)
            return this;
        if(spawners.size() <= minDungeons)
            return null;

        List<DungeonConfiguration> subConfigs = subdivide();

        DungeonConfiguration candidate = null;
        for(DungeonConfiguration subConfig : subConfigs) {
            DungeonConfiguration config = subConfig.getOptimalValidSubdivision(minDungeons);
            if(config == null) continue;

            if(candidate == null || config.spawners.size() > candidate.spawners.size())
                candidate = config;
        }
        return candidate;
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
     * Returns the center point of the configuration which is an optimal (if not required) place for the player to stand.
     *
     * @return center of the configuration
     */
    public Point3d getCenter() {
        return center;
    }

    /**
     * Returns whether all the spawners are at most maxDist blocks away from the center.
     *
     * @return whether the configuration is valid
     */
    public boolean isValid() {
        return valid;
    }

    private final List<Spawner> spawners;
    private final int maxDist;
    private final Point3d center;
    private final boolean valid;
}