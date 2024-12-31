package net.minecraft.world.biome.layer;

import net.minecraft.util.collection.IntArrayCache;

public class class_84 extends Layer {
	public class_84(long l, Layer layer) {
		super(l);
		super.field_172 = layer;
	}

	@Override
	public int[] method_143(int i, int j, int k, int l) {
		int m = i >> 1;
		int n = j >> 1;
		int o = (k >> 1) + 2;
		int p = (l >> 1) + 2;
		int[] is = this.field_172.method_143(m, n, o, p);
		int q = o - 1 << 1;
		int r = p - 1 << 1;
		int[] js = IntArrayCache.get(q * r);

		for (int s = 0; s < p - 1; s++) {
			int t = (s << 1) * q;
			int u = 0;
			int v = is[u + 0 + (s + 0) * o];

			for (int w = is[u + 0 + (s + 1) * o]; u < o - 1; u++) {
				this.method_145((long)(u + m << 1), (long)(s + n << 1));
				int x = is[u + 1 + (s + 0) * o];
				int y = is[u + 1 + (s + 1) * o];
				js[t] = v;
				js[t++ + q] = this.getRandomBiome(new int[]{v, w});
				js[t] = this.getRandomBiome(new int[]{v, x});
				js[t++ + q] = this.method_6598(v, x, w, y);
				v = x;
				w = y;
			}
		}

		int[] ks = IntArrayCache.get(k * l);

		for (int z = 0; z < l; z++) {
			System.arraycopy(js, (z + (j & 1)) * q + (i & 1), ks, z * k, k);
		}

		return ks;
	}

	public static Layer method_148(long l, Layer layer, int i) {
		Layer layer2 = layer;

		for (int j = 0; j < i; j++) {
			layer2 = new class_84(l + (long)j, layer2);
		}

		return layer2;
	}
}
