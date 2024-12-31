package net.minecraft.world.biome.layer;

import net.minecraft.util.collection.IntArrayCache;
import net.minecraft.world.biome.Biome;

public class class_82 extends Layer {
	public class_82(long l, Layer layer) {
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
				if (this.nextInt(57) == 0) {
					if (o == Biome.PLAINS.id) {
						js[n + m * k] = Biome.PLAINS.id + 128;
					} else {
						js[n + m * k] = o;
					}
				} else {
					js[n + m * k] = o;
				}
			}
		}

		return js;
	}
}
