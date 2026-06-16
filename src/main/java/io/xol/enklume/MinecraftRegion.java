package io.xol.enklume;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.zip.Inflater;

public class MinecraftRegion {
	
	int[] locations = new int[1024];
	int[] sizes = new int[1024];
	
	RandomAccessFile is;
	private MinecraftChunk[][] chunks = new MinecraftChunk[32][32];
	
	public MinecraftRegion(File regionFile) {
		try{
			is = new RandomAccessFile(regionFile,"r");
			for(int i = 0; i < 1024; i++)
			{
				locations[i] += is.read() << 16;
				locations[i] += is.read() << 8;
				locations[i] += is.read();
				
				sizes[i] += is.read();
			}
			byte[] osef = new byte[4];
			for(int i = 0; i < 1024; i++)
			{
				is.read(osef);
			}
			
			for(int x = 0; x < 32; x++)
				for(int z = 0; z < 32; z ++)
					chunks[x][z] = getChunkInternal(x, z);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}

	int offset(int x, int z)
	{
		return ((x & 31) + (z & 31) * 32);
	}
	
	public MinecraftChunk getChunk(int x, int z) {
		return chunks[x][z];
	}
	
	private MinecraftChunk getChunkInternal(int x, int z)
	{
		int l = offset(x,z);
		if(sizes[l] > 0)
		{
			try{
				is.seek(locations[l]*4096);
				int compressedLength = 0;
				compressedLength += is.read() << 24;
				compressedLength += is.read() << 16;
				compressedLength += is.read() << 8;
				compressedLength += is.read();
				int compression = is.read();
				if(compression != 2)
				{
					return new MinecraftChunk(x,z);
				}
				
				byte[] compressedData = new byte[compressedLength];
				is.read(compressedData);
				
				ByteArrayOutputStream baos = new ByteArrayOutputStream();
				
				Inflater inflater = new Inflater();
				inflater.setInput(compressedData);
				
				byte[] buffer = new byte[4096];
				while(!inflater.finished())
				{
					int c = inflater.inflate(buffer);
					baos.write(buffer, 0, c);
				}
				baos.close();
				
				return new MinecraftChunk(x,z, baos.toByteArray());
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
		}
		return new MinecraftChunk(x,z);
	}
	
	public void close()
	{
		try {
			is.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
