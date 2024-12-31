package net.minecraft.world.biome.layer;

import net.minecraft.util.collection.IntArrayCache;

public class class_74 extends Layer {
	public class_74(long l) {
		super(l);
	}

	@Override
	public int[] method_143(int i, int j, int k, int l) {
		int[] is = IntArrayCache.get(k * l);

		for (int m = 0; m < l; m++) {
			for (int n = 0; n < k; n++) {
				this.method_145((long)(i + n), (long)(j + m));
				is[n + m * k] = this.nextInt(10) == 0 ? 1 : 0;
			}
		}

		if (i > -k && i <= 0 && j > -l && j <= 0) {
			is[-i + -j * k] = 1;
		}

		return is;
	}
}
