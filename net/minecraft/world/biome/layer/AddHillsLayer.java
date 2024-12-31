package net.minecraft.world.biome.layer;

import net.minecraft.util.collection.IntArrayCache;
import net.minecraft.world.biome.Biome;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class AddHillsLayer extends Layer {
	private static final Logger LOGGER = LogManager.getLogger();
	private Layer field_7604;

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
					LOGGER.debug("old! " + o);
				}

				if (o != 0 && p >= 2 && (p - 2) % 29 == 1 && o < 128) {
					if (Biome.byId(o + 128) != null) {
						ks[n + m * k] = o + 128;
					} else {
						ks[n + m * k] = o;
					}
				} else if (this.nextInt(3) != 0 && !bl) {
					ks[n + m * k] = o;
				} else {
					int q = o;
					if (o == Biome.DESERT.id) {
						q = Biome.DESERT_HILLS.id;
					} else if (o == Biome.FOREST.id) {
						q = Biome.FOREST_HILLS.id;
					} else if (o == Biome.BIRCH_FOREST.id) {
						q = Biome.BIRCH_FOREST_HILLS.id;
					} else if (o == Biome.ROOFED_FOREST.id) {
						q = Biome.PLAINS.id;
					} else if (o == Biome.TAIGA.id) {
						q = Biome.TAIGA_HILLS.id;
					} else if (o == Biome.MEGA_TAIGA.id) {
						q = Biome.MEGA_TAIGA_HILLS.id;
					} else if (o == Biome.COLD_TAIGA.id) {
						q = Biome.COLD_TAIGA_HILLS.id;
					} else if (o == Biome.PLAINS.id) {
						if (this.nextInt(3) == 0) {
							q = Biome.FOREST_HILLS.id;
						} else {
							q = Biome.FOREST.id;
						}
					} else if (o == Biome.ICE_PLAINS.id) {
						q = Biome.ICE_MOUNTAINS.id;
					} else if (o == Biome.JUNGLE.id) {
						q = Biome.JUNGLE_HILLS.id;
					} else if (o == Biome.OCEAN.id) {
						q = Biome.DEEP_OCEAN.id;
					} else if (o == Biome.EXTREME_HILLS.id) {
						q = Biome.EXTREME_HILLS_PLUS.id;
					} else if (o == Biome.SAVANNA.id) {
						q = Biome.SAVANNA_PLATEAU.id;
					} else if (compareBiomes(o, Biome.MESA_PLATEAU_F.id)) {
						q = Biome.MESA.id;
					} else if (o == Biome.DEEP_OCEAN.id && this.nextInt(3) == 0) {
						int r = this.nextInt(2);
						if (r == 0) {
							q = Biome.PLAINS.id;
						} else {
							q = Biome.FOREST.id;
						}
					}

					if (bl && q != o) {
						if (Biome.byId(q + 128) != null) {
							q += 128;
						} else {
							q = o;
						}
					}

					if (q == o) {
						ks[n + m * k] = o;
					} else {
						int s = is[n + 1 + (m + 1 - 1) * (k + 2)];
						int t = is[n + 1 + 1 + (m + 1) * (k + 2)];
						int u = is[n + 1 - 1 + (m + 1) * (k + 2)];
						int v = is[n + 1 + (m + 1 + 1) * (k + 2)];
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
							ks[n + m * k] = q;
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
