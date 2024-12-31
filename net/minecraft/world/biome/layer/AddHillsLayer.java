package net.minecraft.world.biome.layer;

import net.minecraft.util.collection.IntArrayCache;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.Biomes;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class AddHillsLayer extends Layer {
	private static final Logger LOGGER = LogManager.getLogger();
	private final Layer field_7604;

	public AddHillsLayer(long l, Layer layer, Layer layer2) {
		super(l);
		this.field_172 = layer;
		this.field_7604 = layer2;
	}

	@Override
	public int[] method_143(int i, int j, int k, int l) {
		int[] is = this.field_172.method_143(i - 1, j - 1, k + 2, l + 2);
		int[] js = this.field_7604.method_143(i - 1, j - 1, k + 2, l + 2);
		int[] ks = IntArrayCache.get(k * l);

		for (int m = 0; m < l; m++) {
			for (int n = 0; n < k; n++) {
				this.method_145((long)(n + i), (long)(m + j));
				int o = is[n + 1 + (m + 1) * (k + 2)];
				int p = js[n + 1 + (m + 1) * (k + 2)];
				boolean bl = (p - 2) % 29 == 0;
				if (o > 255) {
					LOGGER.debug("old! {}", new Object[]{o});
				}

				Biome biome = Biome.getBiomeFromIndex(o);
				boolean bl2 = biome != null && biome.hasParent();
				if (o != 0 && p >= 2 && (p - 2) % 29 == 1 && !bl2) {
					Biome biome2 = Biome.getBiomeFromList(biome);
					ks[n + m * k] = biome2 == null ? o : Biome.getBiomeIndex(biome2);
				} else if (this.nextInt(3) != 0 && !bl) {
					ks[n + m * k] = o;
				} else {
					Biome biome3 = biome;
					if (biome == Biomes.DESERT) {
						biome3 = Biomes.DESERT_HILLS;
					} else if (biome == Biomes.FOREST) {
						biome3 = Biomes.FOREST_HILLS;
					} else if (biome == Biomes.BIRCH_FOREST) {
						biome3 = Biomes.BIRCH_FOREST_HILLS;
					} else if (biome == Biomes.ROOFED_FOREST) {
						biome3 = Biomes.PLAINS;
					} else if (biome == Biomes.TAIGA) {
						biome3 = Biomes.TAIGA_HILLS;
					} else if (biome == Biomes.REDWOOD_TAIGA) {
						biome3 = Biomes.REDWOOD_TAIGA_HILLS;
					} else if (biome == Biomes.TAIGA_COLD) {
						biome3 = Biomes.TAIGA_COLD_HILLS;
					} else if (biome == Biomes.PLAINS) {
						if (this.nextInt(3) == 0) {
							biome3 = Biomes.FOREST_HILLS;
						} else {
							biome3 = Biomes.FOREST;
						}
					} else if (biome == Biomes.ICE_FLATS) {
						biome3 = Biomes.ICE_MOUNTAINS;
					} else if (biome == Biomes.JUNGLE) {
						biome3 = Biomes.JUNGLE_HILLS;
					} else if (biome == Biomes.OCEAN) {
						biome3 = Biomes.DEEP_OCEAN;
					} else if (biome == Biomes.EXTREME_HILLS) {
						biome3 = Biomes.EXTREME_HILLS_WITH_TREES;
					} else if (biome == Biomes.SAVANNA) {
						biome3 = Biomes.SAVANNA_ROCK;
					} else if (compareBiomes(o, Biome.getBiomeIndex(Biomes.MESA_ROCK))) {
						biome3 = Biomes.MESA;
					} else if (biome == Biomes.DEEP_OCEAN && this.nextInt(3) == 0) {
						int q = this.nextInt(2);
						if (q == 0) {
							biome3 = Biomes.PLAINS;
						} else {
							biome3 = Biomes.FOREST;
						}
					}

					int r = Biome.getBiomeIndex(biome3);
					if (bl && r != o) {
						Biome biome4 = Biome.getBiomeFromList(biome3);
						r = biome4 == null ? o : Biome.getBiomeIndex(biome4);
					}

					if (r == o) {
						ks[n + m * k] = o;
					} else {
						int s = is[n + 1 + (m + 0) * (k + 2)];
						int t = is[n + 2 + (m + 1) * (k + 2)];
						int u = is[n + 0 + (m + 1) * (k + 2)];
						int v = is[n + 1 + (m + 2) * (k + 2)];
						int w = 0;
						if (compareBiomes(s, o)) {
							w++;
						}

						if (compareBiomes(t, o)) {
							w++;
						}

						if (compareBiomes(u, o)) {
							w++;
						}

						if (compareBiomes(v, o)) {
							w++;
						}

						if (w >= 3) {
							ks[n + m * k] = r;
						} else {
							ks[n + m * k] = o;
						}
					}
				}
			}
		}

		return ks;
	}
}
