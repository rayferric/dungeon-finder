package com.rayferric.dungeonfinder;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class DungeonConfiguration {
    public DungeonConfiguration(List<BlockPos> dungeons) {
        this.dungeons = new ArrayList<BlockPos>(dungeons);
        Collections.sort(this.dungeons); // We must sort the array, so two differently ordered configurations will remain equal

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

    public boolean isValid() {
        for(BlockPos pos : dungeons)
            if(center.distance(pos) > 16.0)
                return false;
        return true;
    }

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

    public DungeonConfiguration getFirstValidSubdivision(int minDungeons) {
        if(isValid())return this;
        if(dungeons.size() <= minDungeons)return null;
        List<DungeonConfiguration> subConfigs = subdivide();
        for(DungeonConfiguration subConfig : subConfigs) {
            DungeonConfiguration config = subConfig.getFirstValidSubdivision(minDungeons);
            if(config != null)return config;
        }
        return null;
    }

    public List<BlockPos> getDungeons() {
        return dungeons;
    }

    public BlockPos getCenter() {
        return center;
    }

    private List<BlockPos> dungeons;
    private BlockPos center;
}