package net.minecraft.world.gen;

import java.util.Random;

public class class_1779 extends AbstractNoiseGenerator {
	private int[] field_7551 = new int[512];
	public double field_7548;
	public double field_7549;
	public double field_7550;
	private static final double[] field_7552 = new double[]{1.0, -1.0, 1.0, -1.0, 1.0, -1.0, 1.0, -1.0, 0.0, 0.0, 0.0, 0.0, 1.0, 0.0, -1.0, 0.0};
	private static final double[] field_7553 = new double[]{1.0, 1.0, -1.0, -1.0, 0.0, 0.0, 0.0, 0.0, 1.0, -1.0, 1.0, -1.0, 1.0, -1.0, 1.0, -1.0};
	private static final double[] field_7554 = new double[]{0.0, 0.0, 0.0, 0.0, 1.0, 1.0, -1.0, -1.0, 1.0, 1.0, -1.0, -1.0, 0.0, 1.0, 0.0, -1.0};
	private static final double[] field_7555 = new double[]{1.0, -1.0, 1.0, -1.0, 1.0, -1.0, 1.0, -1.0, 0.0, 0.0, 0.0, 0.0, 1.0, 0.0, -1.0, 0.0};
	private static final double[] field_7556 = new double[]{0.0, 0.0, 0.0, 0.0, 1.0, 1.0, -1.0, -1.0, 1.0, 1.0, -1.0, -1.0, 0.0, 1.0, 0.0, -1.0};

	public class_1779() {
		this(new Random());
	}

	public class_1779(Random random) {
		this.field_7548 = random.nextDouble() * 256.0;
		this.field_7549 = random.nextDouble() * 256.0;
		this.field_7550 = random.nextDouble() * 256.0;
		int i = 0;

		while (i < 256) {
			this.field_7551[i] = i++;
		}

		for (int j = 0; j < 256; j++) {
			int k = random.nextInt(256 - j) + j;
			int l = this.field_7551[j];
			this.field_7551[j] = this.field_7551[k];
			this.field_7551[k] = l;
			this.field_7551[j + 256] = this.field_7551[j];
		}
	}

	public final double method_6578(double d, double e, double f) {
		return e + d * (f - e);
	}

	public final double method_6575(int i, double d, double e) {
		int j = i & 15;
		return field_7555[j] * d + field_7556[j] * e;
	}

	public final double method_6576(int i, double d, double e, double f) {
		int j = i & 15;
		return field_7552[j] * d + field_7553[j] * e + field_7554[j] * f;
	}

	public void method_6577(double[] ds, double d, double e, double f, int i, int j, int k, double g, double h, double l, double m) {
		if (j == 1) {
			int n = 0;
			int o = 0;
			int p = 0;
			int q = 0;
			double r = 0.0;
			double s = 0.0;
			int t = 0;
			double u = 1.0 / m;

			for (int v = 0; v < i; v++) {
				double w = d + (double)v * g + this.field_7548;
				int x = (int)w;
				if (w < (double)x) {
					x--;
				}

				int y = x & 0xFF;
				w -= (double)x;
				double z = w * w * w * (w * (w * 6.0 - 15.0) + 10.0);

				for (int aa = 0; aa < k; aa++) {
					double ab = f + (double)aa * l + this.field_7550;
					int ac = (int)ab;
					if (ab < (double)ac) {
						ac--;
					}

					int ad = ac & 0xFF;
					ab -= (double)ac;
					double ae = ab * ab * ab * (ab * (ab * 6.0 - 15.0) + 10.0);
					n = this.field_7551[y] + 0;
					o = this.field_7551[n] + ad;
					p = this.field_7551[y + 1] + 0;
					q = this.field_7551[p] + ad;
					r = this.method_6578(z, this.method_6575(this.field_7551[o], w, ab), this.method_6576(this.field_7551[q], w - 1.0, 0.0, ab));
					s = this.method_6578(z, this.method_6576(this.field_7551[o + 1], w, 0.0, ab - 1.0), this.method_6576(this.field_7551[q + 1], w - 1.0, 0.0, ab - 1.0));
					double af = this.method_6578(ae, r, s);
					ds[t++] += af * u;
				}
			}
		} else {
			int ag = 0;
			double ah = 1.0 / m;
			int ai = -1;
			int aj = 0;
			int ak = 0;
			int al = 0;
			int am = 0;
			int an = 0;
			int ao = 0;
			double ap = 0.0;
			double aq = 0.0;
			double ar = 0.0;
			double as = 0.0;

			for (int at = 0; at < i; at++) {
				double au = d + (double)at * g + this.field_7548;
				int av = (int)au;
				if (au < (double)av) {
					av--;
				}

				int aw = av & 0xFF;
				au -= (double)av;
				double ax = au * au * au * (au * (au * 6.0 - 15.0) + 10.0);

				for (int ay = 0; ay < k; ay++) {
					double az = f + (double)ay * l + this.field_7550;
					int ba = (int)az;
					if (az < (double)ba) {
						ba--;
					}

					int bb = ba & 0xFF;
					az -= (double)ba;
					double bc = az * az * az * (az * (az * 6.0 - 15.0) + 10.0);

					for (int bd = 0; bd < j; bd++) {
						double be = e + (double)bd * h + this.field_7549;
						int bf = (int)be;
						if (be < (double)bf) {
							bf--;
						}

						int bg = bf & 0xFF;
						be -= (double)bf;
						double bh = be * be * be * (be * (be * 6.0 - 15.0) + 10.0);
						if (bd == 0 || bg != ai) {
							ai = bg;
							aj = this.field_7551[aw] + bg;
							ak = this.field_7551[aj] + bb;
							al = this.field_7551[aj + 1] + bb;
							am = this.field_7551[aw + 1] + bg;
							an = this.field_7551[am] + bb;
							ao = this.field_7551[am + 1] + bb;
							ap = this.method_6578(ax, this.method_6576(this.field_7551[ak], au, be, az), this.method_6576(this.field_7551[an], au - 1.0, be, az));
							aq = this.method_6578(ax, this.method_6576(this.field_7551[al], au, be - 1.0, az), this.method_6576(this.field_7551[ao], au - 1.0, be - 1.0, az));
							ar = this.method_6578(ax, this.method_6576(this.field_7551[ak + 1], au, be, az - 1.0), this.method_6576(this.field_7551[an + 1], au - 1.0, be, az - 1.0));
							as = this.method_6578(
								ax, this.method_6576(this.field_7551[al + 1], au, be - 1.0, az - 1.0), this.method_6576(this.field_7551[ao + 1], au - 1.0, be - 1.0, az - 1.0)
							);
						}

						double bi = this.method_6578(bh, ap, aq);
						double bj = this.method_6578(bh, ar, as);
						double bk = this.method_6578(bc, bi, bj);
						ds[ag++] += bk * ah;
					}
				}
			}
		}
	}
}
