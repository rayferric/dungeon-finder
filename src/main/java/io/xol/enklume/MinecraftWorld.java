package io.xol.enklume;

import java.io.File;
import java.io.IOException;

import io.xol.enklume.nbt.NBTFile;
import io.xol.enklume.nbt.NBTString;
import io.xol.enklume.nbt.NBTFile.CompressionScheme;

public class MinecraftWorld
{
	private final File folder;
	private final NBTFile nbtFile;
	
	private final String levelName;
	
	public MinecraftWorld(File folder) throws IOException
	{
		this.folder = folder;
		
		File levelDat = new File(this.folder.getAbsolutePath()+"/level.dat");
		if (levelDat.exists()) {
			nbtFile = new NBTFile(levelDat, CompressionScheme.GZIPPED);
			levelName = ((NBTString)nbtFile.getRoot().getTag("Data.LevelName")).getText();
		} else {
			nbtFile = null;
			levelName = "unknown";
		}
	}
	
	public String getName()
	{
		return levelName;
	}
	
	public NBTFile getLevelDotDat()
	{
		return nbtFile;
	}
	
	public static int blockToRegionCoordinates(int blockCoordinates)
	{
		if (blockCoordinates >= 0)
		{
			return (int) Math.floor(blockCoordinates / 512f);
		}
		blockCoordinates = -blockCoordinates;
		return -(int) Math.floor(blockCoordinates / 512f) - 1;
	}
	
	public MinecraftRegion getRegion(int regionCoordinateX, int regionCoordinateZ)
	{
		File regionFile = new File(folder.getAbsolutePath() + "/region/r." + regionCoordinateX + "." + regionCoordinateZ + ".mca");
		
		if (regionFile.exists())
		{
			return new MinecraftRegion(regionFile);
		}
		
		// Try new dimension path structure
		regionFile = new File(folder.getAbsolutePath() + "/dimensions/minecraft/overworld/region/r." + regionCoordinateX + "." + regionCoordinateZ + ".mca");
		if (regionFile.exists())
		{
			return new MinecraftRegion(regionFile);
		}
		
		return null;
	}
}
