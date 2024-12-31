package net.minecraft.world.biome.layer;

import net.minecraft.util.collection.IntArrayCache;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.Biomes;

public class MushroomIslandLayer extends Layer {
	public MushroomIslandLayer(long l, Layer layer) {
		super(l);
		this.field_172 = layer;
	}

	@Override
	public int[] method_143(int i, int j, int k, int l) {
		int m = i - 1;
		int n = j - 1;
		int o = k + 2;
		int p = l + 2;
		int[] is = this.field_172.method_143(m, n, o, p);
		int[] js = IntArrayCache.get(k * l);

		for (int q = 0; q < l; q++) {
			for (int r = 0; r < k; r++) {
				int s = is[r + 0 + (q + 0) * o];
				int t = is[r + 2 + (q + 0) * o];
				int u = is[r + 0 + (q + 2) * o];
				int v = is[r + 2 + (q + 2) * o];
				int w = is[r + 1 + (q + 1) * o];
				this.method_145((long)(r + i), (long)(q + j));
				if (w == 0 && s == 0 && t == 0 && u == 0 && v == 0 && this.nextInt(100) == 0) {
					js[r + q * k] = Biome.getBiomeIndex(Biomes.MUSHROOM_ISLAND);
				} else {
					js[r + q * k] = w;
				}
			}
		}

		return js;
	}
}
