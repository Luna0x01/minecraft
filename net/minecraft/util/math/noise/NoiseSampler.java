package net.minecraft.util.math.noise;

import java.util.Random;

public class NoiseSampler {
	private static final int[][] field_7564 = new int[][]{
		{1, 1, 0}, {-1, 1, 0}, {1, -1, 0}, {-1, -1, 0}, {1, 0, 1}, {-1, 0, 1}, {1, 0, -1}, {-1, 0, -1}, {0, 1, 1}, {0, -1, 1}, {0, 1, -1}, {0, -1, -1}
	};
	public static final double SQRT3 = Math.sqrt(3.0);
	private final int[] permutations = new int[512];
	public double field_7561;
	public double field_7562;
	public double field_7563;
	private static final double field_7566 = 0.5 * (SQRT3 - 1.0);
	private static final double field_7567 = (3.0 - SQRT3) / 6.0;

	public NoiseSampler() {
		this(new Random());
	}

	public NoiseSampler(Random random) {
		this.field_7561 = random.nextDouble() * 256.0;
		this.field_7562 = random.nextDouble() * 256.0;
		this.field_7563 = random.nextDouble() * 256.0;
		int i = 0;

		while (i < 256) {
			this.permutations[i] = i++;
		}

		for (int j = 0; j < 256; j++) {
			int k = random.nextInt(256 - j) + j;
			int l = this.permutations[j];
			this.permutations[j] = this.permutations[k];
			this.permutations[k] = l;
			this.permutations[j + 256] = this.permutations[j];
		}
	}

	private static int method_6582(double d) {
		return d > 0.0 ? (int)d : (int)d - 1;
	}

	private static double method_6585(int[] is, double d, double e) {
		return (double)is[0] * d + (double)is[1] * e;
	}

	public double sample(double x, double y) {
		double d = 0.5 * (SQRT3 - 1.0);
		double e = (x + y) * d;
		int i = method_6582(x + e);
		int j = method_6582(y + e);
		double f = (3.0 - SQRT3) / 6.0;
		double g = (double)(i + j) * f;
		double h = (double)i - g;
		double k = (double)j - g;
		double l = x - h;
		double m = y - k;
		int n;
		int o;
		if (l > m) {
			n = 1;
			o = 0;
		} else {
			n = 0;
			o = 1;
		}

		double r = l - (double)n + f;
		double s = m - (double)o + f;
		double t = l - 1.0 + 2.0 * f;
		double u = m - 1.0 + 2.0 * f;
		int v = i & 0xFF;
		int w = j & 0xFF;
		int z = this.permutations[v + this.permutations[w]] % 12;
		int aa = this.permutations[v + n + this.permutations[w + o]] % 12;
		int ab = this.permutations[v + 1 + this.permutations[w + 1]] % 12;
		double ac = 0.5 - l * l - m * m;
		double ad;
		if (ac < 0.0) {
			ad = 0.0;
		} else {
			ac *= ac;
			ad = ac * ac * method_6585(field_7564[z], l, m);
		}

		double af = 0.5 - r * r - s * s;
		double ag;
		if (af < 0.0) {
			ag = 0.0;
		} else {
			af *= af;
			ag = af * af * method_6585(field_7564[aa], r, s);
		}

		double ai = 0.5 - t * t - u * u;
		double aj;
		if (ai < 0.0) {
			aj = 0.0;
		} else {
			ai *= ai;
			aj = ai * ai * method_6585(field_7564[ab], t, u);
		}

		return 70.0 * (ad + ag + aj);
	}

	public void method_6584(double[] ds, double d, double e, int i, int j, double f, double g, double h) {
		int k = 0;

		for (int l = 0; l < j; l++) {
			double m = (e + (double)l) * g + this.field_7562;

			for (int n = 0; n < i; n++) {
				double o = (d + (double)n) * f + this.field_7561;
				double p = (o + m) * field_7566;
				int q = method_6582(o + p);
				int r = method_6582(m + p);
				double s = (double)(q + r) * field_7567;
				double t = (double)q - s;
				double u = (double)r - s;
				double v = o - t;
				double w = m - u;
				int x;
				int y;
				if (v > w) {
					x = 1;
					y = 0;
				} else {
					x = 0;
					y = 1;
				}

				double ab = v - (double)x + field_7567;
				double ac = w - (double)y + field_7567;
				double ad = v - 1.0 + 2.0 * field_7567;
				double ae = w - 1.0 + 2.0 * field_7567;
				int af = q & 0xFF;
				int ag = r & 0xFF;
				int ah = this.permutations[af + this.permutations[ag]] % 12;
				int ai = this.permutations[af + x + this.permutations[ag + y]] % 12;
				int aj = this.permutations[af + 1 + this.permutations[ag + 1]] % 12;
				double ak = 0.5 - v * v - w * w;
				double al;
				if (ak < 0.0) {
					al = 0.0;
				} else {
					ak *= ak;
					al = ak * ak * method_6585(field_7564[ah], v, w);
				}

				double an = 0.5 - ab * ab - ac * ac;
				double ao;
				if (an < 0.0) {
					ao = 0.0;
				} else {
					an *= an;
					ao = an * an * method_6585(field_7564[ai], ab, ac);
				}

				double aq = 0.5 - ad * ad - ae * ae;
				double ar;
				if (aq < 0.0) {
					ar = 0.0;
				} else {
					aq *= aq;
					ar = aq * aq * method_6585(field_7564[aj], ad, ae);
				}

				ds[k++] += 70.0 * (al + ao + ar) * h;
			}
		}
	}
}
