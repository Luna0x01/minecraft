package net.minecraft.world.biome.layer;

import net.minecraft.util.collection.IntArrayCache;

public class EdgeLayer extends Layer {
	private final EdgeLayer.Type type;

	public EdgeLayer(long l, Layer layer, EdgeLayer.Type type) {
		super(l);
		this.field_172 = layer;
		this.type = type;
	}

	@Override
	public int[] method_143(int i, int j, int k, int l) {
		switch (this.type) {
			case COOL_WARM:
			default:
				return this.method_6589(i, j, k, l);
			case HEAT_ICE:
				return this.method_6590(i, j, k, l);
			case SPECIAL:
				return this.method_6591(i, j, k, l);
		}
	}

	private int[] method_6589(int i, int j, int k, int l) {
		int m = i - 1;
		int n = j - 1;
		int o = 1 + k + 1;
		int p = 1 + l + 1;
		int[] is = this.field_172.method_143(m, n, o, p);
		int[] js = IntArrayCache.get(k * l);

		for (int q = 0; q < l; q++) {
			for (int r = 0; r < k; r++) {
				this.method_145((long)(r + i), (long)(q + j));
				int s = is[r + 1 + (q + 1) * o];
				if (s == 1) {
					int t = is[r + 1 + (q + 1 - 1) * o];
					int u = is[r + 1 + 1 + (q + 1) * o];
					int v = is[r + 1 - 1 + (q + 1) * o];
					int w = is[r + 1 + (q + 1 + 1) * o];
					boolean bl = t == 3 || u == 3 || v == 3 || w == 3;
					boolean bl2 = t == 4 || u == 4 || v == 4 || w == 4;
					if (bl || bl2) {
						s = 2;
					}
				}

				js[r + q * k] = s;
			}
		}

		return js;
	}

	private int[] method_6590(int i, int j, int k, int l) {
		int m = i - 1;
		int n = j - 1;
		int o = 1 + k + 1;
		int p = 1 + l + 1;
		int[] is = this.field_172.method_143(m, n, o, p);
		int[] js = IntArrayCache.get(k * l);

		for (int q = 0; q < l; q++) {
			for (int r = 0; r < k; r++) {
				int s = is[r + 1 + (q + 1) * o];
				if (s == 4) {
					int t = is[r + 1 + (q + 1 - 1) * o];
					int u = is[r + 1 + 1 + (q + 1) * o];
					int v = is[r + 1 - 1 + (q + 1) * o];
					int w = is[r + 1 + (q + 1 + 1) * o];
					boolean bl = t == 2 || u == 2 || v == 2 || w == 2;
					boolean bl2 = t == 1 || u == 1 || v == 1 || w == 1;
					if (bl2 || bl) {
						s = 3;
					}
				}

				js[r + q * k] = s;
			}
		}

		return js;
	}

	private int[] method_6591(int i, int j, int k, int l) {
		int[] is = this.field_172.method_143(i, j, k, l);
		int[] js = IntArrayCache.get(k * l);

		for (int m = 0; m < l; m++) {
			for (int n = 0; n < k; n++) {
				this.method_145((long)(n + i), (long)(m + j));
				int o = is[n + m * k];
				if (o != 0 && this.nextInt(13) == 0) {
					o |= 1 + this.nextInt(15) << 8 & 3840;
				}

				js[n + m * k] = o;
			}
		}

		return js;
	}

	public static enum Type {
		COOL_WARM,
		HEAT_ICE,
		SPECIAL;
	}
}
