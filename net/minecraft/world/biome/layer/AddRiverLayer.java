package net.minecraft.world.biome.layer;

import net.minecraft.util.collection.IntArrayCache;
import net.minecraft.world.biome.Biome;

public class AddRiverLayer extends Layer {
	public AddRiverLayer(long l, Layer layer) {
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
				int s = this.method_6601(is[r + 0 + (q + 1) * o]);
				int t = this.method_6601(is[r + 2 + (q + 1) * o]);
				int u = this.method_6601(is[r + 1 + (q + 0) * o]);
				int v = this.method_6601(is[r + 1 + (q + 2) * o]);
				int w = this.method_6601(is[r + 1 + (q + 1) * o]);
				if (w == s && w == u && w == t && w == v) {
					js[r + q * k] = -1;
				} else {
					js[r + q * k] = Biome.RIVER.id;
				}
			}
		}

		return js;
	}

	private int method_6601(int i) {
		return i >= 2 ? 2 + (i & 1) : i;
	}
}
