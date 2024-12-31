package net.minecraft.world.biome;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import java.util.concurrent.TimeUnit;
import net.minecraft.util.math.ChunkPos;

public class BiomeCache {
	private final SingletonBiomeSource field_4665;
	private final LoadingCache<ChunkPos, BiomeCache.Entry> field_17658 = CacheBuilder.newBuilder()
		.expireAfterAccess(30000L, TimeUnit.MILLISECONDS)
		.build(new CacheLoader<ChunkPos, BiomeCache.Entry>() {
			public BiomeCache.Entry load(ChunkPos chunkPos) throws Exception {
				return BiomeCache.this.new Entry(chunkPos.x, chunkPos.z);
			}
		});

	public BiomeCache(SingletonBiomeSource singletonBiomeSource) {
		this.field_4665 = singletonBiomeSource;
	}

	public BiomeCache.Entry method_16472(int i, int j) {
		i >>= 4;
		j >>= 4;
		return (BiomeCache.Entry)this.field_17658.getUnchecked(new ChunkPos(i, j));
	}

	public Biome method_3843(int i, int j, Biome biome) {
		Biome biome2 = this.method_16472(i, j).getBiomeAt(i, j);
		return biome2 == null ? biome : biome2;
	}

	public void method_3840() {
	}

	public Biome[] method_3844(int i, int j) {
		return this.method_16472(i, j).field_17660;
	}

	public class Entry {
		private final Biome[] field_17660;

		public Entry(int i, int j) {
			this.field_17660 = BiomeCache.this.field_4665.method_16477(i << 4, j << 4, 16, 16, false);
		}

		public Biome getBiomeAt(int x, int z) {
			return this.field_17660[x & 15 | (z & 15) << 4];
		}
	}
}
