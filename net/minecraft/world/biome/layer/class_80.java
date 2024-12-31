package net.minecraft.world.biome.layer;

import net.minecraft.util.collection.IntArrayCache;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.Biomes;
import net.minecraft.world.biome.JungleBiome;
import net.minecraft.world.biome.MesaBiome;

public class class_80 extends Layer {
	public class_80(long l, Layer layer) {
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
				Biome biome = Biome.byId(o);
				if (o == Biome.getBiomeIndex(Biomes.MUSHROOM_ISLAND)) {
					int p = is[n + 1 + (m + 1 - 1) * (k + 2)];
					int q = is[n + 1 + 1 + (m + 1) * (k + 2)];
					int r = is[n + 1 - 1 + (m + 1) * (k + 2)];
					int s = is[n + 1 + (m + 1 + 1) * (k + 2)];
					if (p != Biome.getBiomeIndex(Biomes.OCEAN)
						&& q != Biome.getBiomeIndex(Biomes.OCEAN)
						&& r != Biome.getBiomeIndex(Biomes.OCEAN)
						&& s != Biome.getBiomeIndex(Biomes.OCEAN)) {
						js[n + m * k] = o;
					} else {
						js[n + m * k] = Biome.getBiomeIndex(Biomes.MUSHROOM_ISLAND_SHORE);
					}
				} else if (biome != null && biome.asClass() == JungleBiome.class) {
					int t = is[n + 1 + (m + 1 - 1) * (k + 2)];
					int u = is[n + 1 + 1 + (m + 1) * (k + 2)];
					int v = is[n + 1 - 1 + (m + 1) * (k + 2)];
					int w = is[n + 1 + (m + 1 + 1) * (k + 2)];
					if (!this.method_6603(t) || !this.method_6603(u) || !this.method_6603(v) || !this.method_6603(w)) {
						js[n + m * k] = Biome.getBiomeIndex(Biomes.JUNGLE_EDGE);
					} else if (!isOcean(t) && !isOcean(u) && !isOcean(v) && !isOcean(w)) {
						js[n + m * k] = o;
					} else {
						js[n + m * k] = Biome.getBiomeIndex(Biomes.BEACH);
					}
				} else if (o == Biome.getBiomeIndex(Biomes.EXTREME_HILLS)
					|| o == Biome.getBiomeIndex(Biomes.EXTREME_HILLS_WITH_TREES)
					|| o == Biome.getBiomeIndex(Biomes.ExTREME_HILLS_SMALLER)) {
					this.method_6602(is, js, n, m, k, o, Biome.getBiomeIndex(Biomes.STONE_BEACH));
				} else if (biome != null && biome.isMutatedBiome()) {
					this.method_6602(is, js, n, m, k, o, Biome.getBiomeIndex(Biomes.COLD_BEACH));
				} else if (o == Biome.getBiomeIndex(Biomes.MESA) || o == Biome.getBiomeIndex(Biomes.MESA_ROCK)) {
					int x = is[n + 1 + (m + 1 - 1) * (k + 2)];
					int y = is[n + 1 + 1 + (m + 1) * (k + 2)];
					int z = is[n + 1 - 1 + (m + 1) * (k + 2)];
					int aa = is[n + 1 + (m + 1 + 1) * (k + 2)];
					if (isOcean(x) || isOcean(y) || isOcean(z) || isOcean(aa)) {
						js[n + m * k] = o;
					} else if (this.method_6604(x) && this.method_6604(y) && this.method_6604(z) && this.method_6604(aa)) {
						js[n + m * k] = o;
					} else {
						js[n + m * k] = Biome.getBiomeIndex(Biomes.DESERT);
					}
				} else if (o != Biome.getBiomeIndex(Biomes.OCEAN)
					&& o != Biome.getBiomeIndex(Biomes.DEEP_OCEAN)
					&& o != Biome.getBiomeIndex(Biomes.RIVER)
					&& o != Biome.getBiomeIndex(Biomes.SWAMP)) {
					int ab = is[n + 1 + (m + 1 - 1) * (k + 2)];
					int ac = is[n + 1 + 1 + (m + 1) * (k + 2)];
					int ad = is[n + 1 - 1 + (m + 1) * (k + 2)];
					int ae = is[n + 1 + (m + 1 + 1) * (k + 2)];
					if (!isOcean(ab) && !isOcean(ac) && !isOcean(ad) && !isOcean(ae)) {
						js[n + m * k] = o;
					} else {
						js[n + m * k] = Biome.getBiomeIndex(Biomes.BEACH);
					}
				} else {
					js[n + m * k] = o;
				}
			}
		}

		return js;
	}

	private void method_6602(int[] is, int[] js, int i, int j, int k, int l, int m) {
		if (isOcean(l)) {
			js[i + j * k] = l;
		} else {
			int n = is[i + 1 + (j + 1 - 1) * (k + 2)];
			int o = is[i + 1 + 1 + (j + 1) * (k + 2)];
			int p = is[i + 1 - 1 + (j + 1) * (k + 2)];
			int q = is[i + 1 + (j + 1 + 1) * (k + 2)];
			if (!isOcean(n) && !isOcean(o) && !isOcean(p) && !isOcean(q)) {
				js[i + j * k] = l;
			} else {
				js[i + j * k] = m;
			}
		}
	}

	private boolean method_6603(int i) {
		return Biome.byId(i) != null && Biome.byId(i).asClass() == JungleBiome.class
			? true
			: i == Biome.getBiomeIndex(Biomes.JUNGLE_EDGE)
				|| i == Biome.getBiomeIndex(Biomes.JUNGLE)
				|| i == Biome.getBiomeIndex(Biomes.JUNGLE_HILLS)
				|| i == Biome.getBiomeIndex(Biomes.FOREST)
				|| i == Biome.getBiomeIndex(Biomes.TAIGA)
				|| isOcean(i);
	}

	private boolean method_6604(int i) {
		return Biome.byId(i) instanceof MesaBiome;
	}
}
