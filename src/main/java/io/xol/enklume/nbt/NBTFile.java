package io.xol.enklume.nbt;

import java.io.*;
import java.util.zip.GZIPInputStream;
import java.util.zip.InflaterInputStream;

public class NBTFile
{
	private final NBTCompound root;
	
	public enum CompressionScheme {
		NONE, 
		GZIPPED,
		ZLIB,
	}
	
	public NBTFile(File file, CompressionScheme scheme) throws IOException
	{
		if(!file.exists())
			throw new FileNotFoundException(file.getAbsolutePath());
		
		FileInputStream fis = new FileInputStream(file);
		InputStream is;
		
		switch (scheme) {
			case GZIPPED:
				is = new GZIPInputStream(fis);
				break;
			case ZLIB:
				is = new InflaterInputStream(fis);
				break;
			case NONE:
				is = fis;
				break;
			default:
				fis.close();
				throw new RuntimeException("Unknown CompressionScheme: " + scheme);
		}
		
		root = (NBTCompound) NBTag.parseInputStream(is);
		is.close();
	}
	
	public NBTCompound getRoot()
	{
		return root;
	}
}
