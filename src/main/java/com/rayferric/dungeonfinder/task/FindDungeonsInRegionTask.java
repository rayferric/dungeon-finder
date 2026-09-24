package com.rayferric.dungeonfinder.task;

import com.conversantmedia.util.collection.geometry.Point3d;
import com.conversantmedia.util.collection.spatial.ConcurrentRTree;
import com.rayferric.dungeonfinder.Spawner;
import com.rayferric.dungeonfinder.util.MobType;
import io.xol.enklume.MinecraftChunk;
import io.xol.enklume.MinecraftRegion;
import io.xol.enklume.MinecraftWorld;
import io.xol.enklume.nbt.*;
import org.jetbrains.annotations.NotNull;

public class FindDungeonsInRegionTask implements Runnable {
    public FindDungeonsInRegionTask(int regionX, int regionZ, @NotNull MinecraftWorld world,
                                    @NotNull ConcurrentRTree<Spawner> spawnerTree) {
        this.regionX = regionX;
        this.regionZ = regionZ;
        this.world = world;
        this.spawnerTree = spawnerTree;
    }

    @Override
    public void run() {
        MinecraftRegion region = world.getRegion(regionX, regionZ);
        if (region == null) return;

        for (int chunkX = 0; chunkX < 32; chunkX++) {
            for (int chunkZ = 0; chunkZ < 32; chunkZ++) {
                MinecraftChunk chunk = region.getChunk(chunkX, chunkZ);
                NBTCompound root = chunk.getRootTag();
                if (root == null)
                    continue;

                // Try new format (1.18+): block_entities at root
                NBTList tileEntities = (NBTList)root.getTag("block_entities");
                // Fall back to old format (pre-1.18): Level.TileEntities
                if (tileEntities == null)
                    tileEntities = (NBTList)root.getTag("Level.TileEntities");
                if (tileEntities == null)
                    continue;

                for (NBTNamed entity : tileEntities.elements) {
                    NBTCompound compoundEntity = (NBTCompound)entity;

                    String id = ((NBTString)compoundEntity.getTag("id")).getText();
                    if (!id.equals("minecraft:mob_spawner"))
                        continue;

                    int x = ((NBTInt)compoundEntity.getTag("x")).getData();
                    int y = ((NBTInt)compoundEntity.getTag("y")).getData();
                    int z = ((NBTInt)compoundEntity.getTag("z")).getData();

                    // Try new format (1.21+): SpawnData.entity.id
                    NBTString mobIdTag = (NBTString)compoundEntity.getTag("SpawnData.entity.id");
                    // Fall back to old format: SpawnData.id
                    if (mobIdTag == null)
                        mobIdTag = (NBTString)compoundEntity.getTag("SpawnData.id");
                    if (mobIdTag == null)
                        continue;

                    String mobId = mobIdTag.getText();
                    mobId = mobId.replace("minecraft:", "");

                    Point3d pos = new Point3d(x, y, z);
                    MobType type = MobType.findById(mobId);
                    spawnerTree.add(new Spawner(pos, type));
                }
            }
        }
        region.close();
    }

    private final int regionX, regionZ;
    private final MinecraftWorld world;
    private final ConcurrentRTree<Spawner> spawnerTree;
}