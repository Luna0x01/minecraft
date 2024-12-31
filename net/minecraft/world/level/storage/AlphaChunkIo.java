package net.minecraft.world.level.storage;

import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.LayeredBiomeSource;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.ChunkNibbleArray;

public class AlphaChunkIo {
	public static AlphaChunkIo.AlphaChunk readAlphaChunk(NbtCompound nbt) {
		int i = nbt.getInt("xPos");
		int j = nbt.getInt("zPos");
		AlphaChunkIo.AlphaChunk alphaChunk = new AlphaChunkIo.AlphaChunk(i, j);
		alphaChunk.blocks = nbt.getByteArray("Blocks");
		alphaChunk.data = new AlphaChunkDataArray(nbt.getByteArray("Data"), 7);
		alphaChunk.skyLight = new AlphaChunkDataArray(nbt.getByteArray("SkyLight"), 7);
		alphaChunk.blockLight = new AlphaChunkDataArray(nbt.getByteArray("BlockLight"), 7);
		alphaChunk.heightMap = nbt.getByteArray("HeightMap");
		alphaChunk.terrainPopulated = nbt.getBoolean("TerrainPopulated");
		alphaChunk.entities = nbt.getList("Entities", 10);
		alphaChunk.blockEntities = nbt.getList("TileEntities", 10);
		alphaChunk.blockTicks = nbt.getList("TileTicks", 10);

		try {
			alphaChunk.lastUpdate = nbt.getLong("LastUpdate");
		} catch (ClassCastException var5) {
			alphaChunk.lastUpdate = (long)nbt.getInt("LastUpdate");
		}

		return alphaChunk;
	}

	public static void convertAlphaChunk(AlphaChunkIo.AlphaChunk alphaChunk, NbtCompound nbt, LayeredBiomeSource biomeSource) {
		nbt.putInt("xPos", alphaChunk.x);
		nbt.putInt("zPos", alphaChunk.z);
		nbt.putLong("LastUpdate", alphaChunk.lastUpdate);
		int[] is = new int[alphaChunk.heightMap.length];

		for (int i = 0; i < alphaChunk.heightMap.length; i++) {
			is[i] = alphaChunk.heightMap[i];
		}

		nbt.putIntArray("HeightMap", is);
		nbt.putBoolean("TerrainPopulated", alphaChunk.terrainPopulated);
		NbtList nbtList = new NbtList();

		for (int j = 0; j < 8; j++) {
			boolean bl = true;

			for (int k = 0; k < 16 && bl; k++) {
				for (int l = 0; l < 16 && bl; l++) {
					for (int m = 0; m < 16; m++) {
						int n = k << 11 | m << 7 | l + (j << 4);
						int o = alphaChunk.blocks[n];
						if (o != 0) {
							bl = false;
							break;
						}
					}
				}
			}

			if (!bl) {
				byte[] bs = new byte[4096];
				ChunkNibbleArray chunkNibbleArray = new ChunkNibbleArray();
				ChunkNibbleArray chunkNibbleArray2 = new ChunkNibbleArray();
				ChunkNibbleArray chunkNibbleArray3 = new ChunkNibbleArray();

				for (int p = 0; p < 16; p++) {
					for (int q = 0; q < 16; q++) {
						for (int r = 0; r < 16; r++) {
							int s = p << 11 | r << 7 | q + (j << 4);
							int t = alphaChunk.blocks[s];
							bs[q << 8 | r << 4 | p] = (byte)(t & 0xFF);
							chunkNibbleArray.set(p, q, r, alphaChunk.data.get(p, q + (j << 4), r));
							chunkNibbleArray2.set(p, q, r, alphaChunk.skyLight.get(p, q + (j << 4), r));
							chunkNibbleArray3.set(p, q, r, alphaChunk.blockLight.get(p, q + (j << 4), r));
						}
					}
				}

				NbtCompound nbtCompound = new NbtCompound();
				nbtCompound.putByte("Y", (byte)(j & 0xFF));
				nbtCompound.putByteArray("Blocks", bs);
				nbtCompound.putByteArray("Data", chunkNibbleArray.getValue());
				nbtCompound.putByteArray("SkyLight", chunkNibbleArray2.getValue());
				nbtCompound.putByteArray("BlockLight", chunkNibbleArray3.getValue());
				nbtList.add(nbtCompound);
			}
		}

		nbt.put("Sections", nbtList);
		byte[] cs = new byte[256];
		BlockPos.Mutable mutable = new BlockPos.Mutable();

		for (int u = 0; u < 16; u++) {
			for (int v = 0; v < 16; v++) {
				mutable.setPosition(alphaChunk.x << 4 | u, 0, alphaChunk.z << 4 | v);
				cs[v << 4 | u] = (byte)(biomeSource.getBiomeAt(mutable, Biome.DEFAULT).id & 0xFF);
			}
		}

		nbt.putByteArray("Biomes", cs);
		nbt.put("Entities", alphaChunk.entities);
		nbt.put("TileEntities", alphaChunk.blockEntities);
		if (alphaChunk.blockTicks != null) {
			nbt.put("TileTicks", alphaChunk.blockTicks);
		}
	}

	public static class AlphaChunk {
		public long lastUpdate;
		public boolean terrainPopulated;
		public byte[] heightMap;
		public AlphaChunkDataArray blockLight;
		public AlphaChunkDataArray skyLight;
		public AlphaChunkDataArray data;
		public byte[] blocks;
		public NbtList entities;
		public NbtList blockEntities;
		public NbtList blockTicks;
		public final int x;
		public final int z;

		public AlphaChunk(int i, int j) {
			this.x = i;
			this.z = j;
		}
	}
}
