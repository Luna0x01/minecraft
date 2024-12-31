package net.minecraft.world.biome.layer;

import net.minecraft.util.collection.IntArrayCache;
import net.minecraft.world.biome.Biome;

public class class_1781 extends Layer {
	public class_1781(long l, Layer layer) {
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
				int s = is[r + 1 + (q + 1 - 1) * (k + 2)];
				int t = is[r + 1 + 1 + (q + 1) * (k + 2)];
				int u = is[r + 1 - 1 + (q + 1) * (k + 2)];
				int v = is[r + 1 + (q + 1 + 1) * (k + 2)];
				int w = is[r + 1 + (q + 1) * o];
				int x = 0;
				if (s == 0) {
					x++;
				}

				if (t == 0) {
					x++;
				}

				if (u == 0) {
					x++;
				}

				if (v == 0) {
					x++;
				}

				if (w == 0 && x > 3) {
					js[r + q * k] = Biome.DEEP_OCEAN.id;
				} else {
					js[r + q * k] = w;
				}
			}
		}

		return js;
	}
}
