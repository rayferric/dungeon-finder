package com.rayferric.dungeonfinder;

import com.rayferric.dungeonfinder.util.BlockPos;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class DungeonConfiguration {
    public DungeonConfiguration(List<BlockPos> dungeons) {
        this.dungeons = new ArrayList<BlockPos>(dungeons);
        // We must sort the array, so two differently ordered configurations will remain equal
        Collections.sort(this.dungeons);

        center = new BlockPos(0, 0, 0);
        int i = 0;
        for(BlockPos pos : dungeons) {
            center = center.add(pos);
            i++;
        }
        center = center.div(i);
    }

    @Override
    public boolean equals(Object obj) {
        if(this == obj) return true;
        if(obj == null || getClass() != obj.getClass()) return false;
        DungeonConfiguration that = (DungeonConfiguration) obj;
        return Objects.equals(dungeons, that.dungeons) && Objects.equals(center, that.center);
    }

    @Override
    public int hashCode() {
        return Objects.hash(dungeons, center);
    }

    @Override
    public String toString() {
        return String.format("DungeonConfiguration{dungeons=%s, center=%s}", dungeons, center);
    }

    /**
     * Check if all the spawners are at most 16 block away from the center
     *
     * @return    whether is the configuration valid
     */
    public boolean isValid() {
        for(BlockPos pos : dungeons)
            if(center.distance(pos) > 16.0)
                return false;
        return true;
    }

    /**
     * Subdivides this configuration into two children with one less spawner each
     *
     * @return    list of two children configurations
     */
    public List<DungeonConfiguration> subdivide() {
        List<DungeonConfiguration> subConfigs = new ArrayList<>(2);

        List<BlockPos> newDungeons = new ArrayList<>(dungeons);
        newDungeons.remove(newDungeons.size() - 1);
        subConfigs.add(new DungeonConfiguration(newDungeons));

        newDungeons = new ArrayList<>(dungeons);
        newDungeons.remove(0);
        subConfigs.add(new DungeonConfiguration(newDungeons));

        return subConfigs;
    }

    /**
     * Subdivides the configuration till a valid one that has at least minDungeons spawners is found<br>
     * Always returns the configuration with the most spawners
     *
     * @param minDungeons    minimum number of dungeons in a configuration
     *
     * @return    a valid configuration with the most spawners possible
     */
    public DungeonConfiguration getOptimalValidSubdivision(int minDungeons) {
        if(isValid())return this;
        if(dungeons.size() <= minDungeons)return null;
        List<DungeonConfiguration> subConfigs = subdivide();
        DungeonConfiguration candidate = null;
        for(DungeonConfiguration subConfig : subConfigs) {
            DungeonConfiguration config = subConfig.getOptimalValidSubdivision(minDungeons);
            if(config != null)
                if(candidate == null)
                    candidate = config;
                else if(config.dungeons.size() > candidate.dungeons.size())
                    candidate = config;
        }
        return candidate;
    }

    /**
     * Returns a list of spawner positions in the configuration
     *
     * @return    list of spawner positions
     */
    public List<BlockPos> getDungeons() {
        return dungeons;
    }

    /**
     * Returns the center point of the configuration which is an optimal (if not required) place for the player to stand
     *
     * @return    center of the configuration
     */
    public BlockPos getCenter() {
        return center;
    }

    private List<BlockPos> dungeons;
    private BlockPos center;
}