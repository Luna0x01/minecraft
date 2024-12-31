package net.minecraft.world.biome.layer;

import net.minecraft.util.collection.IntArrayCache;
import net.minecraft.world.biome.Biome;

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
				if (!this.method_6592(is, js, n, m, k, o, Biome.EXTREME_HILLS.id, Biome.EXTREME_HILLS_EDGE.id)
					&& !this.method_6594(is, js, n, m, k, o, Biome.MESA_PLATEAU_F.id, Biome.MESA.id)
					&& !this.method_6594(is, js, n, m, k, o, Biome.MESA_PLATEAU.id, Biome.MESA.id)
					&& !this.method_6594(is, js, n, m, k, o, Biome.MEGA_TAIGA.id, Biome.TAIGA.id)) {
					if (o == Biome.DESERT.id) {
						int p = is[n + 1 + (m + 1 - 1) * (k + 2)];
						int q = is[n + 1 + 1 + (m + 1) * (k + 2)];
						int r = is[n + 1 - 1 + (m + 1) * (k + 2)];
						int s = is[n + 1 + (m + 1 + 1) * (k + 2)];
						if (p != Biome.ICE_PLAINS.id && q != Biome.ICE_PLAINS.id && r != Biome.ICE_PLAINS.id && s != Biome.ICE_PLAINS.id) {
							js[n + m * k] = o;
						} else {
							js[n + m * k] = Biome.EXTREME_HILLS_PLUS.id;
						}
					} else if (o == Biome.SWAMPLAND.id) {
						int t = is[n + 1 + (m + 1 - 1) * (k + 2)];
						int u = is[n + 1 + 1 + (m + 1) * (k + 2)];
						int v = is[n + 1 - 1 + (m + 1) * (k + 2)];
						int w = is[n + 1 + (m + 1 + 1) * (k + 2)];
						if (t == Biome.DESERT.id
							|| u == Biome.DESERT.id
							|| v == Biome.DESERT.id
							|| w == Biome.DESERT.id
							|| t == Biome.COLD_TAIGA.id
							|| u == Biome.COLD_TAIGA.id
							|| v == Biome.COLD_TAIGA.id
							|| w == Biome.COLD_TAIGA.id
							|| t == Biome.ICE_PLAINS.id
							|| u == Biome.ICE_PLAINS.id
							|| v == Biome.ICE_PLAINS.id
							|| w == Biome.ICE_PLAINS.id) {
							js[n + m * k] = Biome.PLAINS.id;
						} else if (t != Biome.JUNGLE.id && w != Biome.JUNGLE.id && u != Biome.JUNGLE.id && v != Biome.JUNGLE.id) {
							js[n + m * k] = o;
						} else {
							js[n + m * k] = Biome.JUNGLE_EDGE.id;
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
