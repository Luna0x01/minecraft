package net.minecraft.util.math.noise;

import java.util.Random;
import net.minecraft.world.gen.AbstractNoiseGenerator;

public class PerlinNoiseGenerator extends AbstractNoiseGenerator {
	private final NoiseSampler[] samplers;
	private final int samplersCount;

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

	public double[] method_6580(double d, double e, int i, int j, double f, double g, double h) {
		return this.method_6581(d, e, i, j, f, g, h, 0.5);
	}

	public double[] method_6581(double d, double e, int i, int j, double f, double g, double h, double k) {
		double[] ds = new double[i * j];
		double l = 1.0;
		double m = 1.0;

		for (int n = 0; n < this.samplersCount; n++) {
			this.samplers[n].method_6584(ds, d, e, i, j, f * m * l, g * m * l, 0.55 / l);
			m *= h;
			l *= k;
		}

		return ds;
	}
}
