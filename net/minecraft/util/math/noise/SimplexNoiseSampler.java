package net.minecraft.util.math.noise;

import java.util.Random;
import net.minecraft.util.math.MathHelper;

public class SimplexNoiseSampler {
	protected static final int[][] gradients = new int[][]{
		{1, 1, 0},
		{-1, 1, 0},
		{1, -1, 0},
		{-1, -1, 0},
		{1, 0, 1},
		{-1, 0, 1},
		{1, 0, -1},
		{-1, 0, -1},
		{0, 1, 1},
		{0, -1, 1},
		{0, 1, -1},
		{0, -1, -1},
		{1, 1, 0},
		{0, -1, 1},
		{-1, 1, 0},
		{0, -1, -1}
	};
	private static final double sqrt3 = Math.sqrt(3.0);
	private static final double field_15768 = 0.5 * (sqrt3 - 1.0);
	private static final double field_15767 = (3.0 - sqrt3) / 6.0;
	private final int[] field_15765 = new int[512];
	public final double field_15763;
	public final double field_15762;
	public final double field_15761;

	public SimplexNoiseSampler(Random random) {
		this.field_15763 = random.nextDouble() * 256.0;
		this.field_15762 = random.nextDouble() * 256.0;
		this.field_15761 = random.nextDouble() * 256.0;
		int i = 0;

		while (i < 256) {
			this.field_15765[i] = i++;
		}

		for (int j = 0; j < 256; j++) {
			int k = random.nextInt(256 - j);
			int l = this.field_15765[j];
			this.field_15765[j] = this.field_15765[k + j];
			this.field_15765[k + j] = l;
		}
	}

	private int method_16456(int i) {
		return this.field_15765[i & 0xFF];
	}

	protected static double dot(int[] is, double d, double e, double f) {
		return (double)is[0] * d + (double)is[1] * e + (double)is[2] * f;
	}

	private double method_16455(int i, double d, double e, double f, double g) {
		double h = g - d * d - e * e - f * f;
		double j;
		if (h < 0.0) {
			j = 0.0;
		} else {
			h *= h;
			j = h * h * dot(gradients[i], d, e, f);
		}

		return j;
	}

	public double sample(double d, double e) {
		double f = (d + e) * field_15768;
		int i = MathHelper.floor(d + f);
		int j = MathHelper.floor(e + f);
		double g = (double)(i + j) * field_15767;
		double h = (double)i - g;
		double k = (double)j - g;
		double l = d - h;
		double m = e - k;
		int n;
		int o;
		if (l > m) {
			n = 1;
			o = 0;
		} else {
			n = 0;
			o = 1;
		}

		double r = l - (double)n + field_15767;
		double s = m - (double)o + field_15767;
		double t = l - 1.0 + 2.0 * field_15767;
		double u = m - 1.0 + 2.0 * field_15767;
		int v = i & 0xFF;
		int w = j & 0xFF;
		int x = this.method_16456(v + this.method_16456(w)) % 12;
		int y = this.method_16456(v + n + this.method_16456(w + o)) % 12;
		int z = this.method_16456(v + 1 + this.method_16456(w + 1)) % 12;
		double aa = this.method_16455(x, l, m, 0.0, 0.5);
		double ab = this.method_16455(y, r, s, 0.0, 0.5);
		double ac = this.method_16455(z, t, u, 0.0, 0.5);
		return 70.0 * (aa + ab + ac);
	}
}
