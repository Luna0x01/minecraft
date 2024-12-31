package net.minecraft.world.level.storage;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.biome.source.BiomeSource;
import net.minecraft.world.chunk.ChunkNibbleArray;

public class AlphaChunkIo {
	public static AlphaChunkIo.AlphaChunk readAlphaChunk(CompoundTag compoundTag) {
		int i = compoundTag.getInt("xPos");
		int j = compoundTag.getInt("zPos");
		AlphaChunkIo.AlphaChunk alphaChunk = new AlphaChunkIo.AlphaChunk(i, j);
		alphaChunk.blocks = compoundTag.getByteArray("Blocks");
		alphaChunk.data = new AlphaChunkDataArray(compoundTag.getByteArray("Data"), 7);
		alphaChunk.skyLight = new AlphaChunkDataArray(compoundTag.getByteArray("SkyLight"), 7);
		alphaChunk.blockLight = new AlphaChunkDataArray(compoundTag.getByteArray("BlockLight"), 7);
		alphaChunk.heightMap = compoundTag.getByteArray("HeightMap");
		alphaChunk.terrainPopulated = compoundTag.getBoolean("TerrainPopulated");
		alphaChunk.entities = compoundTag.getList("Entities", 10);
		alphaChunk.blockEntities = compoundTag.getList("TileEntities", 10);
		alphaChunk.blockTicks = compoundTag.getList("TileTicks", 10);

		try {
			alphaChunk.lastUpdate = compoundTag.getLong("LastUpdate");
		} catch (ClassCastException var5) {
			alphaChunk.lastUpdate = (long)compoundTag.getInt("LastUpdate");
		}

		return alphaChunk;
	}

	public static void convertAlphaChunk(AlphaChunkIo.AlphaChunk alphaChunk, CompoundTag compoundTag, BiomeSource biomeSource) {
		compoundTag.putInt("xPos", alphaChunk.x);
		compoundTag.putInt("zPos", alphaChunk.z);
		compoundTag.putLong("LastUpdate", alphaChunk.lastUpdate);
		int[] is = new int[alphaChunk.heightMap.length];

		for (int i = 0; i < alphaChunk.heightMap.length; i++) {
			is[i] = alphaChunk.heightMap[i];
		}

		compoundTag.putIntArray("HeightMap", is);
		compoundTag.putBoolean("TerrainPopulated", alphaChunk.terrainPopulated);
		ListTag listTag = new ListTag();

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

				CompoundTag compoundTag2 = new CompoundTag();
				compoundTag2.putByte("Y", (byte)(j & 0xFF));
				compoundTag2.putByteArray("Blocks", bs);
				compoundTag2.putByteArray("Data", chunkNibbleArray.asByteArray());
				compoundTag2.putByteArray("SkyLight", chunkNibbleArray2.asByteArray());
				compoundTag2.putByteArray("BlockLight", chunkNibbleArray3.asByteArray());
				listTag.add(compoundTag2);
			}
		}

		compoundTag.put("Sections", listTag);
		byte[] cs = new byte[256];
		BlockPos.Mutable mutable = new BlockPos.Mutable();

		for (int u = 0; u < 16; u++) {
			for (int v = 0; v < 16; v++) {
				mutable.set(alphaChunk.x << 4 | u, 0, alphaChunk.z << 4 | v);
				cs[v << 4 | u] = (byte)(Registry.BIOME.getRawId(biomeSource.getBiome(mutable)) & 0xFF);
			}
		}

		compoundTag.putByteArray("Biomes", cs);
		compoundTag.put("Entities", alphaChunk.entities);
		compoundTag.put("TileEntities", alphaChunk.blockEntities);
		if (alphaChunk.blockTicks != null) {
			compoundTag.put("TileTicks", alphaChunk.blockTicks);
		}

		compoundTag.putBoolean("convertedFromAlphaFormat", true);
	}

	public static class AlphaChunk {
		public long lastUpdate;
		public boolean terrainPopulated;
		public byte[] heightMap;
		public AlphaChunkDataArray blockLight;
		public AlphaChunkDataArray skyLight;
		public AlphaChunkDataArray data;
		public byte[] blocks;
		public ListTag entities;
		public ListTag blockEntities;
		public ListTag blockTicks;
		public final int x;
		public final int z;

		public AlphaChunk(int i, int j) {
			this.x = i;
			this.z = j;
		}
	}
}
