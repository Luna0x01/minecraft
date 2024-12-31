package net.minecraft.world.biome.layer;

import net.minecraft.util.collection.IntArrayCache;

public class class_83 extends Layer {
	public class_83(long l, Layer layer) {
		super(l);
		super.field_172 = layer;
	}

	@Override
	public int[] method_143(int i, int j, int k, int l) {
		i -= 2;
		j -= 2;
		int m = i >> 2;
		int n = j >> 2;
		int o = (k >> 2) + 2;
		int p = (l >> 2) + 2;
		int[] is = this.field_172.method_143(m, n, o, p);
		int q = o - 1 << 2;
		int r = p - 1 << 2;
		int[] js = IntArrayCache.get(q * r);

		for (int s = 0; s < p - 1; s++) {
			int t = 0;
			int u = is[t + 0 + (s + 0) * o];

			for (int v = is[t + 0 + (s + 1) * o]; t < o - 1; t++) {
				double d = 3.6;
				this.method_145((long)(t + m << 2), (long)(s + n << 2));
				double e = ((double)this.nextInt(1024) / 1024.0 - 0.5) * 3.6;
				double f = ((double)this.nextInt(1024) / 1024.0 - 0.5) * 3.6;
				this.method_145((long)(t + m + 1 << 2), (long)(s + n << 2));
				double g = ((double)this.nextInt(1024) / 1024.0 - 0.5) * 3.6 + 4.0;
				double h = ((double)this.nextInt(1024) / 1024.0 - 0.5) * 3.6;
				this.method_145((long)(t + m << 2), (long)(s + n + 1 << 2));
				double w = ((double)this.nextInt(1024) / 1024.0 - 0.5) * 3.6;
				double x = ((double)this.nextInt(1024) / 1024.0 - 0.5) * 3.6 + 4.0;
				this.method_145((long)(t + m + 1 << 2), (long)(s + n + 1 << 2));
				double y = ((double)this.nextInt(1024) / 1024.0 - 0.5) * 3.6 + 4.0;
				double z = ((double)this.nextInt(1024) / 1024.0 - 0.5) * 3.6 + 4.0;
				int aa = is[t + 1 + (s + 0) * o] & 0xFF;
				int ab = is[t + 1 + (s + 1) * o] & 0xFF;

				for (int ac = 0; ac < 4; ac++) {
					int ad = ((s << 2) + ac) * q + (t << 2);

					for (int ae = 0; ae < 4; ae++) {
						double af = ((double)ac - f) * ((double)ac - f) + ((double)ae - e) * ((double)ae - e);
						double ag = ((double)ac - h) * ((double)ac - h) + ((double)ae - g) * ((double)ae - g);
						double ah = ((double)ac - x) * ((double)ac - x) + ((double)ae - w) * ((double)ae - w);
						double ai = ((double)ac - z) * ((double)ac - z) + ((double)ae - y) * ((double)ae - y);
						if (af < ag && af < ah && af < ai) {
							js[ad++] = u;
						} else if (ag < af && ag < ah && ag < ai) {
							js[ad++] = aa;
						} else if (ah < af && ah < ag && ah < ai) {
							js[ad++] = v;
						} else {
							js[ad++] = ab;
						}
					}
				}

				u = aa;
				v = ab;
			}
		}

		int[] ks = IntArrayCache.get(k * l);

		for (int aj = 0; aj < l; aj++) {
			System.arraycopy(js, (aj + (j & 3)) * q + (i & 3), ks, aj * k, k);
		}

		return ks;
	}
}
