package net.minecraft.world.gen;

import java.util.Random;
import net.minecraft.util.math.MathHelper;

public class NoiseGenerator extends AbstractNoiseGenerator {
	private final class_1779[] field_7557;
	private final int field_111;

	public NoiseGenerator(Random random, int i) {
		this.field_111 = i;
		this.field_7557 = new class_1779[i];

		for (int j = 0; j < i; j++) {
			this.field_7557[j] = new class_1779(random);
		}
	}

	public double method_17727(double d, double e, double f) {
		double g = 0.0;
		double h = 1.0;

		for (int i = 0; i < this.field_111; i++) {
			g += this.field_7557[i].method_17726(d * h, e * h, f * h) / h;
			h /= 2.0;
		}

		return g;
	}

	public double[] method_122(int i, int j, int k, int l, int m, int n, double d, double e, double f) {
		double[] ds = new double[l * m * n];
		double g = 1.0;

		for (int o = 0; o < this.field_111; o++) {
			double h = (double)i * g * d;
			double p = (double)j * g * e;
			double q = (double)k * g * f;
			long r = MathHelper.lfloor(h);
			long s = MathHelper.lfloor(q);
			h -= (double)r;
			q -= (double)s;
			r %= 16777216L;
			s %= 16777216L;
			h += (double)r;
			q += (double)s;
			this.field_7557[o].method_6577(ds, h, p, q, l, m, n, d * g, e * g, f * g, g);
			g /= 2.0;
		}

		return ds;
	}

	public double[] method_121(int i, int j, int k, int l, double d, double e, double f) {
		return this.method_122(i, 10, j, k, 1, l, d, 1.0, e);
	}
}
