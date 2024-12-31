package net.minecraft.world.biome.layer;

import net.minecraft.util.collection.IntArrayCache;

public class class_1788 extends Layer {
	public class_1788(long l, Layer layer) {
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
				js[r + q * k] = w;
				this.method_145((long)(r + i), (long)(q + j));
				if (w == 0 && s == 0 && t == 0 && u == 0 && v == 0 && this.nextInt(2) == 0) {
					js[r + q * k] = 1;
				}
			}
		}

		return js;
	}
}
