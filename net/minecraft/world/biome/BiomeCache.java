package net.minecraft.world.biome;

import com.google.common.collect.Lists;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import java.util.List;
import net.minecraft.server.MinecraftServer;

public class BiomeCache {
	private final SingletonBiomeSource field_4665;
	private long field_4666;
	private final Long2ObjectMap<BiomeCache.Entry> field_12460 = new Long2ObjectOpenHashMap(4096);
	private final List<BiomeCache.Entry> slots = Lists.newArrayList();

	public BiomeCache(SingletonBiomeSource singletonBiomeSource) {
		this.field_4665 = singletonBiomeSource;
	}

	public BiomeCache.Entry method_3841(int i, int j) {
		i >>= 4;
		j >>= 4;
		long l = (long)i & 4294967295L | ((long)j & 4294967295L) << 32;
		BiomeCache.Entry entry = (BiomeCache.Entry)this.field_12460.get(l);
		if (entry == null) {
			entry = new BiomeCache.Entry(i, j);
			this.field_12460.put(l, entry);
			this.slots.add(entry);
		}

		entry.field_4674 = MinecraftServer.getTimeMillis();
		return entry;
	}

	public Biome method_3843(int i, int j, Biome biome) {
		Biome biome2 = this.method_3841(i, j).getBiomeAt(i, j);
		return biome2 == null ? biome : biome2;
	}

	public void method_3840() {
		long l = MinecraftServer.getTimeMillis();
		long m = l - this.field_4666;
		if (m > 7500L || m < 0L) {
			this.field_4666 = l;

			for (int i = 0; i < this.slots.size(); i++) {
				BiomeCache.Entry entry = (BiomeCache.Entry)this.slots.get(i);
				long n = l - entry.field_4674;
				if (n > 30000L || n < 0L) {
					this.slots.remove(i--);
					long o = (long)entry.x & 4294967295L | ((long)entry.z & 4294967295L) << 32;
					this.field_12460.remove(o);
				}
			}
		}
	}

	public Biome[] method_3844(int i, int j) {
		return this.method_3841(i, j).biomeArray;
	}

	public class Entry {
		public Biome[] biomeArray = new Biome[256];
		public int x;
		public int z;
		public long field_4674;

		public Entry(int i, int j) {
			this.x = i;
			this.z = j;
			BiomeCache.this.field_4665.method_11538(this.biomeArray, i << 4, j << 4, 16, 16, false);
		}

		public Biome getBiomeAt(int x, int z) {
			return this.biomeArray[x & 15 | (z & 15) << 4];
		}
	}
}
