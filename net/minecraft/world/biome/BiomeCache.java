package net.minecraft.world.biome;

import com.google.common.collect.Lists;
import java.util.List;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.collection.LongObjectStorage;
import net.minecraft.world.LayeredBiomeSource;

public class BiomeCache {
	private final LayeredBiomeSource biomeSource;
	private long field_4666;
	private LongObjectStorage<BiomeCache.Entry> field_4667 = new LongObjectStorage<>();
	private List<BiomeCache.Entry> slots = Lists.newArrayList();

	public BiomeCache(LayeredBiomeSource layeredBiomeSource) {
		this.biomeSource = layeredBiomeSource;
	}

	public BiomeCache.Entry method_3841(int i, int j) {
		i >>= 4;
		j >>= 4;
		long l = (long)i & 4294967295L | ((long)j & 4294967295L) << 32;
		BiomeCache.Entry entry = this.field_4667.get(l);
		if (entry == null) {
			entry = new BiomeCache.Entry(i, j);
			this.field_4667.set(l, entry);
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
					this.field_4667.remove(o);
				}
			}
		}
	}

	public Biome[] method_3844(int i, int j) {
		return this.method_3841(i, j).biomeArray;
	}

	public class Entry {
		public float[] field_7227 = new float[256];
		public Biome[] biomeArray = new Biome[256];
		public int x;
		public int z;
		public long field_4674;

		public Entry(int i, int j) {
			this.x = i;
			this.z = j;
			BiomeCache.this.biomeSource.method_3856(this.field_7227, i << 4, j << 4, 16, 16);
			BiomeCache.this.biomeSource.method_3858(this.biomeArray, i << 4, j << 4, 16, 16, false);
		}

		public Biome getBiomeAt(int x, int z) {
			return this.biomeArray[x & 15 | (z & 15) << 4];
		}
	}
}
