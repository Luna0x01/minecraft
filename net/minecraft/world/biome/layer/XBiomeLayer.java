package net.minecraft.world.biome.layer;

import net.minecraft.util.collection.IntArrayCache;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.Biomes;

public class XBiomeLayer extends Layer {
	public XBiomeLayer(long l, Layer layer) {
		super(l);
		this.field_172 = layer;
	}

	@Override
	public int[] method_143(int i, int j, int k, int l) {
		int[] is = this.field_172.method_143(i - 1, j - 1, k + 2, l + 2);
		int[] js = IntArrayCache.get(k * l);

		for (int m = 0; m < l; m++) {
			for (int n = 0; n < k; n++) {
				this.method_145((long)(n + i), (long)(m + j));
				int o = is[n + 1 + (m + 1) * (k + 2)];
				if (!this.method_6592(is, js, n, m, k, o, Biome.getBiomeIndex(Biomes.EXTREME_HILLS), Biome.getBiomeIndex(Biomes.ExTREME_HILLS_SMALLER))
					&& !this.method_6594(is, js, n, m, k, o, Biome.getBiomeIndex(Biomes.MESA_ROCK), Biome.getBiomeIndex(Biomes.MESA))
					&& !this.method_6594(is, js, n, m, k, o, Biome.getBiomeIndex(Biomes.MESA_CLEAR_ROCK), Biome.getBiomeIndex(Biomes.MESA))
					&& !this.method_6594(is, js, n, m, k, o, Biome.getBiomeIndex(Biomes.REDWOOD_TAIGA), Biome.getBiomeIndex(Biomes.TAIGA))) {
					if (o == Biome.getBiomeIndex(Biomes.DESERT)) {
						int p = is[n + 1 + (m + 1 - 1) * (k + 2)];
						int q = is[n + 1 + 1 + (m + 1) * (k + 2)];
						int r = is[n + 1 - 1 + (m + 1) * (k + 2)];
						int s = is[n + 1 + (m + 1 + 1) * (k + 2)];
						if (p != Biome.getBiomeIndex(Biomes.ICE_FLATS)
							&& q != Biome.getBiomeIndex(Biomes.ICE_FLATS)
							&& r != Biome.getBiomeIndex(Biomes.ICE_FLATS)
							&& s != Biome.getBiomeIndex(Biomes.ICE_FLATS)) {
							js[n + m * k] = o;
						} else {
							js[n + m * k] = Biome.getBiomeIndex(Biomes.EXTREME_HILLS_WITH_TREES);
						}
					} else if (o == Biome.getBiomeIndex(Biomes.SWAMP)) {
						int t = is[n + 1 + (m + 1 - 1) * (k + 2)];
						int u = is[n + 1 + 1 + (m + 1) * (k + 2)];
						int v = is[n + 1 - 1 + (m + 1) * (k + 2)];
						int w = is[n + 1 + (m + 1 + 1) * (k + 2)];
						if (t == Biome.getBiomeIndex(Biomes.DESERT)
							|| u == Biome.getBiomeIndex(Biomes.DESERT)
							|| v == Biome.getBiomeIndex(Biomes.DESERT)
							|| w == Biome.getBiomeIndex(Biomes.DESERT)
							|| t == Biome.getBiomeIndex(Biomes.TAIGA_COLD)
							|| u == Biome.getBiomeIndex(Biomes.TAIGA_COLD)
							|| v == Biome.getBiomeIndex(Biomes.TAIGA_COLD)
							|| w == Biome.getBiomeIndex(Biomes.TAIGA_COLD)
							|| t == Biome.getBiomeIndex(Biomes.ICE_FLATS)
							|| u == Biome.getBiomeIndex(Biomes.ICE_FLATS)
							|| v == Biome.getBiomeIndex(Biomes.ICE_FLATS)
							|| w == Biome.getBiomeIndex(Biomes.ICE_FLATS)) {
							js[n + m * k] = Biome.getBiomeIndex(Biomes.PLAINS);
						} else if (t != Biome.getBiomeIndex(Biomes.JUNGLE)
							&& w != Biome.getBiomeIndex(Biomes.JUNGLE)
							&& u != Biome.getBiomeIndex(Biomes.JUNGLE)
							&& v != Biome.getBiomeIndex(Biomes.JUNGLE)) {
							js[n + m * k] = o;
						} else {
							js[n + m * k] = Biome.getBiomeIndex(Biomes.JUNGLE_EDGE);
						}
					} else {
						js[n + m * k] = o;
					}
				}
			}
		}

		return js;
	}

	private boolean method_6592(int[] is, int[] js, int i, int j, int k, int l, int m, int n) {
		if (!compareBiomes(l, m)) {
			return false;
		} else {
			int o = is[i + 1 + (j + 1 - 1) * (k + 2)];
			int p = is[i + 1 + 1 + (j + 1) * (k + 2)];
			int q = is[i + 1 - 1 + (j + 1) * (k + 2)];
			int r = is[i + 1 + (j + 1 + 1) * (k + 2)];
			if (this.method_6593(o, m) && this.method_6593(p, m) && this.method_6593(q, m) && this.method_6593(r, m)) {
				js[i + j * k] = l;
			} else {
				js[i + j * k] = n;
			}

			return true;
		}
	}

	private boolean method_6594(int[] is, int[] js, int i, int j, int k, int l, int m, int n) {
		if (l != m) {
			return false;
		} else {
			int o = is[i + 1 + (j + 1 - 1) * (k + 2)];
			int p = is[i + 1 + 1 + (j + 1) * (k + 2)];
			int q = is[i + 1 - 1 + (j + 1) * (k + 2)];
			int r = is[i + 1 + (j + 1 + 1) * (k + 2)];
			if (compareBiomes(o, m) && compareBiomes(p, m) && compareBiomes(q, m) && compareBiomes(r, m)) {
				js[i + j * k] = l;
			} else {
				js[i + j * k] = n;
			}

			return true;
		}
	}

	private boolean method_6593(int i, int j) {
		if (compareBiomes(i, j)) {
			return true;
		} else {
			Biome biome = Biome.byId(i);
			Biome biome2 = Biome.byId(j);
			if (biome != null && biome2 != null) {
				Biome.Temperature temperature = biome.getBiomeTemperature();
				Biome.Temperature temperature2 = biome2.getBiomeTemperature();
				return temperature == temperature2 || temperature == Biome.Temperature.MEDIUM || temperature2 == Biome.Temperature.MEDIUM;
			} else {
				return false;
			}
		}
	}
}
