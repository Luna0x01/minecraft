package net.minecraft.world.biome.layer;

import net.minecraft.util.collection.IntArrayCache;

public class class_69 extends Layer {
	public class_69(long l, Layer layer) {
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
				int s = is[r + 1 + (q + 1) * o];
				this.method_145((long)(r + i), (long)(q + j));
				if (s == 0) {
					js[r + q * k] = 0;
				} else {
					int t = this.nextInt(6);
					byte var15;
					if (t == 0) {
						var15 = 4;
					} else if (t <= 1) {
						var15 = 3;
					} else {
						var15 = 1;
					}

					js[r + q * k] = var15;
				}
			}
		}

		return js;
	}
}
