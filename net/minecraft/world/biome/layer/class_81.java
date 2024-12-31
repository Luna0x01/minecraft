package net.minecraft.world.biome.layer;

import net.minecraft.util.collection.IntArrayCache;

public class class_81 extends Layer {
	public class_81(long l, Layer layer) {
		super(l);
		super.field_172 = layer;
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
				int s = is[r + 0 + (q + 1) * o];
				int t = is[r + 2 + (q + 1) * o];
				int u = is[r + 1 + (q + 0) * o];
				int v = is[r + 1 + (q + 2) * o];
				int w = is[r + 1 + (q + 1) * o];
				if (s == t && u == v) {
					this.method_145((long)(r + i), (long)(q + j));
					if (this.nextInt(2) == 0) {
						w = s;
					} else {
						w = u;
					}
				} else {
					if (s == t) {
						w = s;
					}

					if (u == v) {
						w = u;
					}
				}

				js[r + q * k] = w;
			}
		}

		return js;
	}
}
