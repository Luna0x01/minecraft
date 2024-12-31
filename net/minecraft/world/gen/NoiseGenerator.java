package net.minecraft.world.gen;

import java.util.Random;
import net.minecraft.util.math.MathHelper;

public class NoiseGenerator extends AbstractNoiseGenerator {
	private class_1779[] field_7557;
	private int field_111;

	public NoiseGenerator(Random random, int i) {
		this.field_111 = i;
		this.field_7557 = new class_1779[i];

		for (int j = 0; j < i; j++) {
			this.field_7557[j] = new class_1779(random);
		}
	}

	public double[] method_122(double[] ds, int i, int j, int k, int l, int m, int n, double d, double e, double f) {
		if (ds == null) {
			ds = new double[l * m * n];
		} else {
			for (int o = 0; o < ds.length; o++) {
				ds[o] = 0.0;
			}
		}

		double g = 1.0;

		for (int p = 0; p < this.field_111; p++) {
			double h = (double)i * g * d;
			double q = (double)j * g * e;
			double r = (double)k * g * f;
			long s = MathHelper.lfloor(h);
			long t = MathHelper.lfloor(r);
			h -= (double)s;
			r -= (double)t;
			s %= 16777216L;
			t %= 16777216L;
			h += (double)s;
			r += (double)t;
			this.field_7557[p].method_6577(ds, h, q, r, l, m, n, d * g, e * g, f * g, g);
			g /= 2.0;
		}

		return ds;
	}

	public double[] method_121(double[] ds, int i, int j, int k, int l, double d, double e, double f) {
		return this.method_122(ds, i, 10, j, k, 1, l, d, 1.0, e);
	}
}
