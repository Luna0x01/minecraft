package net.minecraft.util.math.noise;

import java.util.Random;
import net.minecraft.world.gen.AbstractNoiseGenerator;

public class PerlinNoiseGenerator extends AbstractNoiseGenerator {
	private NoiseSampler[] samplers;
	private int samplersCount;

	public PerlinNoiseGenerator(Random random, int i) {
		this.samplersCount = i;
		this.samplers = new NoiseSampler[i];

		for (int j = 0; j < i; j++) {
			this.samplers[j] = new NoiseSampler(random);
		}
	}

	public double noise(double x, double y) {
		double d = 0.0;
		double e = 1.0;

		for (int i = 0; i < this.samplersCount; i++) {
			d += this.samplers[i].sample(x * e, y * e) / e;
			e /= 2.0;
		}

		return d;
	}

	public double[] method_6580(double[] ds, double d, double e, int i, int j, double f, double g, double h) {
		return this.method_6581(ds, d, e, i, j, f, g, h, 0.5);
	}

	public double[] method_6581(double[] ds, double d, double e, int i, int j, double f, double g, double h, double k) {
		if (ds != null && ds.length >= i * j) {
			for (int l = 0; l < ds.length; l++) {
				ds[l] = 0.0;
			}
		} else {
			ds = new double[i * j];
		}

		double m = 1.0;
		double n = 1.0;

		for (int o = 0; o < this.samplersCount; o++) {
			this.samplers[o].method_6584(ds, d, e, i, j, f * n * m, g * n * m, 0.55 / m);
			n *= h;
			m *= k;
		}

		return ds;
	}
}
