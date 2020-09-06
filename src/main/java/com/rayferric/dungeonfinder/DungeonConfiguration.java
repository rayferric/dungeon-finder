package com.rayferric.dungeonfinder;

import com.rayferric.dungeonfinder.util.BlockPos;
import com.rayferric.math.common.Vector3f;
import com.rayferric.math.welzl.Sphere;
import com.rayferric.math.welzl.Welzl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class DungeonConfiguration {
    public DungeonConfiguration(List<BlockPos> dungeons, int maxDist) {
        this.dungeons = dungeons;
        this.maxDist = maxDist;

        // We must sort the array, so two differently ordered configurations will remain equal
        Collections.sort(this.dungeons);

        List<Vector3f> points = new ArrayList<>(dungeons.size());
        for(BlockPos pos : dungeons) {
            points.add(new Vector3f(pos.getX(), pos.getY(), pos.getZ()));
        }
        Sphere boundingSphere = Welzl.run(points);
        Vector3f origin = boundingSphere.getOrigin();

        center = new BlockPos(origin.getX(), origin.getY(), origin.getZ());
        valid = (boundingSphere.getRadius() <= maxDist);
    }

    @Override
    public boolean equals(Object o) {
        if(this == o) return true;
        if(o == null || getClass() != o.getClass()) return false;
        DungeonConfiguration other = (DungeonConfiguration)o;
        return Objects.equals(dungeons, other.dungeons);
    }

    @Override
    public int hashCode() {
        return Objects.hash(dungeons);
    }

    @Override
    public String toString() {
        return String.format("DungeonConfiguration{dungeons=%s}", dungeons);
    }

    /**
     * Subdivides this configuration into two children with one less spawner each.
     *
     * @return list of two children configurations
     */
    public List<DungeonConfiguration> subdivide() {
        List<DungeonConfiguration> subConfigs = new ArrayList<>(2);

        List<BlockPos> newDungeons = new ArrayList<>(dungeons);
        newDungeons.remove(newDungeons.size() - 1);
        subConfigs.add(new DungeonConfiguration(newDungeons, maxDist));

        newDungeons = new ArrayList<>(dungeons);
        newDungeons.remove(0);
        subConfigs.add(new DungeonConfiguration(newDungeons, maxDist));

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
        if(dungeons.size() <= minDungeons)
            return null;

        List<DungeonConfiguration> subConfigs = subdivide();

        DungeonConfiguration candidate = null;
        for(DungeonConfiguration subConfig : subConfigs) {
            DungeonConfiguration config = subConfig.getOptimalValidSubdivision(minDungeons);
            if(config == null) continue;

            if(candidate == null || config.dungeons.size() > candidate.dungeons.size())
                candidate = config;
        }
        return candidate;
    }

    /**
     * Returns a list of spawner positions in the configuration.
     *
     * @return list of spawner positions
     */
    public List<BlockPos> getDungeons() {
        return dungeons;
    }

    /**
     * Returns the center point of the configuration which is an optimal (if not required) place for the player to stand.
     *
     * @return center of the configuration
     */
    public BlockPos getCenter() {
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

    private final List<BlockPos> dungeons;
    private final int maxDist;
    private final BlockPos center;
    private final boolean valid;
}