package net.minecraft.world.biome.layer;

import net.minecraft.util.collection.IntArrayCache;

public class class_77 extends Layer {
	public class_77(long l, Layer layer) {
		super(l);
		this.field_172 = layer;
	}

	@Override
	public int[] method_143(int i, int j, int k, int l) {
		int[] is = this.field_172.method_143(i, j, k, l);
		int[] js = IntArrayCache.get(k * l);

		for (int m = 0; m < l; m++) {
			for (int n = 0; n < k; n++) {
				this.method_145((long)(n + i), (long)(m + j));
				js[n + m * k] = is[n + m * k] > 0 ? this.nextInt(299999) + 2 : 0;
			}
		}

		return js;
	}
}
