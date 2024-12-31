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
	private static final double SQRT_3 = Math.sqrt(3.0);
	private static final double SKEW_FACTOR_2D = 0.5 * (SQRT_3 - 1.0);
	private static final double UNSKEW_FACTOR_2D = (3.0 - SQRT_3) / 6.0;
	private final int[] permutations = new int[512];
	public final double originX;
	public final double originY;
	public final double originZ;

	public SimplexNoiseSampler(Random random) {
		this.originX = random.nextDouble() * 256.0;
		this.originY = random.nextDouble() * 256.0;
		this.originZ = random.nextDouble() * 256.0;
		int i = 0;

		while (i < 256) {
			this.permutations[i] = i++;
		}

		for (int j = 0; j < 256; j++) {
			int k = random.nextInt(256 - j);
			int l = this.permutations[j];
			this.permutations[j] = this.permutations[k + j];
			this.permutations[k + j] = l;
		}
	}

	private int getGradient(int hash) {
		return this.permutations[hash & 0xFF];
	}

	protected static double dot(int[] gArr, double x, double y, double z) {
		return (double)gArr[0] * x + (double)gArr[1] * y + (double)gArr[2] * z;
	}

	private double grad(int hash, double x, double y, double z, double d) {
		double e = d - x * x - y * y - z * z;
		double f;
		if (e < 0.0) {
			f = 0.0;
		} else {
			e *= e;
			f = e * e * dot(gradients[hash], x, y, z);
		}

		return f;
	}

	public double sample(double x, double y) {
		double d = (x + y) * SKEW_FACTOR_2D;
		int i = MathHelper.floor(x + d);
		int j = MathHelper.floor(y + d);
		double e = (double)(i + j) * UNSKEW_FACTOR_2D;
		double f = (double)i - e;
		double g = (double)j - e;
		double h = x - f;
		double k = y - g;
		int l;
		int m;
		if (h > k) {
			l = 1;
			m = 0;
		} else {
			l = 0;
			m = 1;
		}

		double p = h - (double)l + UNSKEW_FACTOR_2D;
		double q = k - (double)m + UNSKEW_FACTOR_2D;
		double r = h - 1.0 + 2.0 * UNSKEW_FACTOR_2D;
		double s = k - 1.0 + 2.0 * UNSKEW_FACTOR_2D;
		int t = i & 0xFF;
		int u = j & 0xFF;
		int v = this.getGradient(t + this.getGradient(u)) % 12;
		int w = this.getGradient(t + l + this.getGradient(u + m)) % 12;
		int z = this.getGradient(t + 1 + this.getGradient(u + 1)) % 12;
		double aa = this.grad(v, h, k, 0.0, 0.5);
		double ab = this.grad(w, p, q, 0.0, 0.5);
		double ac = this.grad(z, r, s, 0.0, 0.5);
		return 70.0 * (aa + ab + ac);
	}

	public double method_22416(double d, double e, double f) {
		double g = 0.3333333333333333;
		double h = (d + e + f) * 0.3333333333333333;
		int i = MathHelper.floor(d + h);
		int j = MathHelper.floor(e + h);
		int k = MathHelper.floor(f + h);
		double l = 0.16666666666666666;
		double m = (double)(i + j + k) * 0.16666666666666666;
		double n = (double)i - m;
		double o = (double)j - m;
		double p = (double)k - m;
		double q = d - n;
		double r = e - o;
		double s = f - p;
		int t;
		int u;
		int v;
		int w;
		int x;
		int y;
		if (q >= r) {
			if (r >= s) {
				t = 1;
				u = 0;
				v = 0;
				w = 1;
				x = 1;
				y = 0;
			} else if (q >= s) {
				t = 1;
				u = 0;
				v = 0;
				w = 1;
				x = 0;
				y = 1;
			} else {
				t = 0;
				u = 0;
				v = 1;
				w = 1;
				x = 0;
				y = 1;
			}
		} else if (r < s) {
			t = 0;
			u = 0;
			v = 1;
			w = 0;
			x = 1;
			y = 1;
		} else if (q < s) {
			t = 0;
			u = 1;
			v = 0;
			w = 0;
			x = 1;
			y = 1;
		} else {
			t = 0;
			u = 1;
			v = 0;
			w = 1;
			x = 1;
			y = 0;
		}

		double bd = q - (double)t + 0.16666666666666666;
		double be = r - (double)u + 0.16666666666666666;
		double bf = s - (double)v + 0.16666666666666666;
		double bg = q - (double)w + 0.3333333333333333;
		double bh = r - (double)x + 0.3333333333333333;
		double bi = s - (double)y + 0.3333333333333333;
		double bj = q - 1.0 + 0.5;
		double bk = r - 1.0 + 0.5;
		double bl = s - 1.0 + 0.5;
		int bm = i & 0xFF;
		int bn = j & 0xFF;
		int bo = k & 0xFF;
		int bp = this.getGradient(bm + this.getGradient(bn + this.getGradient(bo))) % 12;
		int bq = this.getGradient(bm + t + this.getGradient(bn + u + this.getGradient(bo + v))) % 12;
		int br = this.getGradient(bm + w + this.getGradient(bn + x + this.getGradient(bo + y))) % 12;
		int bs = this.getGradient(bm + 1 + this.getGradient(bn + 1 + this.getGradient(bo + 1))) % 12;
		double bt = this.grad(bp, q, r, s, 0.6);
		double bu = this.grad(bq, bd, be, bf, 0.6);
		double bv = this.grad(br, bg, bh, bi, 0.6);
		double bw = this.grad(bs, bj, bk, bl, 0.6);
		return 32.0 * (bt + bu + bv + bw);
	}
}
