package com.rayferric.dungeonfinder.task;

import com.conversantmedia.util.collection.geometry.Point3d;
import com.conversantmedia.util.collection.spatial.ConcurrentRTree;
import com.rayferric.dungeonfinder.util.BlockID;
import io.xol.enklume.MinecraftChunk;
import io.xol.enklume.MinecraftRegion;
import io.xol.enklume.MinecraftWorld;

public class FindDungeonsInRegionTask implements Runnable {
    public FindDungeonsInRegionTask(int regionX, int regionZ, MinecraftWorld world,
                                    ConcurrentRTree<Point3d> dungeonTree) {
        this.regionX = regionX;
        this.regionZ = regionZ;
        this.world = world;
        this.dungeonTree = dungeonTree;
    }

    @Override
    public void run() {
        MinecraftRegion region = world.getRegion(regionX, regionZ);
        if(region == null) return;

        for(int chunkX = 0; chunkX < 32; chunkX++) {
            for(int chunkZ = 0; chunkZ < 32; chunkZ++) {
                long globalChunkX = regionX * 32 + chunkX;
                long globalChunkZ = regionZ * 32 + chunkZ;

                MinecraftChunk chunk = region.getChunk(chunkX, chunkZ);

                for(int x = 0; x < 16; x++) {
                    for(int z = 0; z < 16; z++) {
                        for(int y = 0; y < 256; y++) {
                            int id = chunk.getBlockID(x, y, z);
                            if(id == BlockID.MOB_SPAWNER) {
                                long globalX = globalChunkX * 16 + x;
                                long globalZ = globalChunkZ * 16 + z;

                                dungeonTree.add(new Point3d(globalX, y, globalZ));
                            }
                        }
                    }
                }
            }
        }
        region.close();
    }

    private final int regionX, regionZ;
    private final MinecraftWorld world;
    private final ConcurrentRTree<Point3d> dungeonTree;
}