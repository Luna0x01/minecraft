package net.minecraft.world.gen;

import javax.annotation.Nullable;
import net.minecraft.util.Util;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.noise.InterpolatedNoiseSampler;
import net.minecraft.util.math.noise.OctavePerlinNoiseSampler;
import net.minecraft.util.math.noise.SimplexNoiseSampler;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.source.BiomeSource;
import net.minecraft.world.biome.source.TheEndBiomeSource;
import net.minecraft.world.gen.chunk.GenerationShapeConfig;
import net.minecraft.world.gen.chunk.WeightSampler;

public class NoiseColumnSampler {
	private static final int field_31470 = 32;
	private static final float[] BIOME_WEIGHT_TABLE = Util.make(new float[25], array -> {
		for (int i = -2; i <= 2; i++) {
			for (int j = -2; j <= 2; j++) {
				float f = 10.0F / MathHelper.sqrt((float)(i * i + j * j) + 0.2F);
				array[i + 2 + (j + 2) * 5] = f;
			}
		}
	});
	private final BiomeSource biomeSource;
	private final int horizontalNoiseResolution;
	private final int verticalNoiseResolution;
	private final int noiseSizeY;
	private final GenerationShapeConfig config;
	private final InterpolatedNoiseSampler noise;
	@Nullable
	private final SimplexNoiseSampler islandNoise;
	private final OctavePerlinNoiseSampler densityNoise;
	private final double topSlideTarget;
	private final double topSlideSize;
	private final double topSlideOffset;
	private final double bottomSlideTarget;
	private final double bottomSlideSize;
	private final double bottomSlideOffset;
	private final double densityFactor;
	private final double densityOffset;
	private final WeightSampler field_33653;

	public NoiseColumnSampler(
		BiomeSource biomeSource,
		int horizontalNoiseResolution,
		int verticalNoiseResolution,
		int noiseSizeY,
		GenerationShapeConfig config,
		InterpolatedNoiseSampler noise,
		@Nullable SimplexNoiseSampler islandNoise,
		OctavePerlinNoiseSampler densityNoise,
		WeightSampler weightSampler
	) {
		this.horizontalNoiseResolution = horizontalNoiseResolution;
		this.verticalNoiseResolution = verticalNoiseResolution;
		this.biomeSource = biomeSource;
		this.noiseSizeY = noiseSizeY;
		this.config = config;
		this.noise = noise;
		this.islandNoise = islandNoise;
		this.densityNoise = densityNoise;
		this.topSlideTarget = (double)config.getTopSlide().getTarget();
		this.topSlideSize = (double)config.getTopSlide().getSize();
		this.topSlideOffset = (double)config.getTopSlide().getOffset();
		this.bottomSlideTarget = (double)config.getBottomSlide().getTarget();
		this.bottomSlideSize = (double)config.getBottomSlide().getSize();
		this.bottomSlideOffset = (double)config.getBottomSlide().getOffset();
		this.densityFactor = config.getDensityFactor();
		this.densityOffset = config.getDensityOffset();
		this.field_33653 = weightSampler;
	}

	public void sampleNoiseColumn(double[] buffer, int x, int z, GenerationShapeConfig config, int seaLevel, int minY, int noiseSizeY) {
		double d;
		double e;
		if (this.islandNoise != null) {
			d = (double)(TheEndBiomeSource.getNoiseAt(this.islandNoise, x, z) - 8.0F);
			if (d > 0.0) {
				e = 0.25;
			} else {
				e = 1.0;
			}
		} else {
			float g = 0.0F;
			float h = 0.0F;
			float i = 0.0F;
			int j = 2;
			int k = seaLevel;
			float l = this.biomeSource.getBiomeForNoiseGen(x, seaLevel, z).getDepth();

			for (int m = -2; m <= 2; m++) {
				for (int n = -2; n <= 2; n++) {
					Biome biome = this.biomeSource.getBiomeForNoiseGen(x + m, k, z + n);
					float o = biome.getDepth();
					float p = biome.getScale();
					float q;
					float r;
					if (config.isAmplified() && o > 0.0F) {
						q = 1.0F + o * 2.0F;
						r = 1.0F + p * 4.0F;
					} else {
						q = o;
						r = p;
					}

					float u = o > l ? 0.5F : 1.0F;
					float v = u * BIOME_WEIGHT_TABLE[m + 2 + (n + 2) * 5] / (q + 2.0F);
					g += r * v;
					h += q * v;
					i += v;
				}
			}

			float w = h / i;
			float y = g / i;
			double aa = (double)(w * 0.5F - 0.125F);
			double ab = (double)(y * 0.9F + 0.1F);
			d = aa * 0.265625;
			e = 96.0 / ab;
		}

		double ae = 684.412 * config.getSampling().getXZScale();
		double af = 684.412 * config.getSampling().getYScale();
		double ag = ae / config.getSampling().getXZFactor();
		double ah = af / config.getSampling().getYFactor();
		double ai = config.hasRandomDensityOffset() ? this.getDensityNoise(x, z) : 0.0;

		for (int aj = 0; aj <= noiseSizeY; aj++) {
			int ak = aj + minY;
			double al = this.noise.sample(x, ak, z, ae, af, ag, ah);
			double am = this.getOffset(ak, d, e, ai) + al;
			am = this.field_33653.sample(am, ak * this.verticalNoiseResolution, z * this.horizontalNoiseResolution, x * this.horizontalNoiseResolution);
			am = this.applySlides(am, ak);
			buffer[aj] = am;
		}
	}

	private double getOffset(int y, double depth, double scale, double randomDensityOffset) {
		double d = 1.0 - (double)y * 2.0 / 32.0 + randomDensityOffset;
		double e = d * this.densityFactor + this.densityOffset;
		double f = (e + depth) * scale;
		return f * (double)(f > 0.0 ? 4 : 1);
	}

	private double applySlides(double noise, int y) {
		int i = MathHelper.floorDiv(this.config.getMinimumY(), this.verticalNoiseResolution);
		int j = y - i;
		if (this.topSlideSize > 0.0) {
			double d = ((double)(this.noiseSizeY - j) - this.topSlideOffset) / this.topSlideSize;
			noise = MathHelper.clampedLerp(this.topSlideTarget, noise, d);
		}

		if (this.bottomSlideSize > 0.0) {
			double e = ((double)j - this.bottomSlideOffset) / this.bottomSlideSize;
			noise = MathHelper.clampedLerp(this.bottomSlideTarget, noise, e);
		}

		return noise;
	}

	private double getDensityNoise(int x, int z) {
		double d = this.densityNoise.sample((double)(x * 200), 10.0, (double)(z * 200), 1.0, 0.0, true);
		double e;
		if (d < 0.0) {
			e = -d * 0.3;
		} else {
			e = d;
		}

		double g = e * 24.575625 - 2.0;
		return g < 0.0 ? g * 0.009486607142857142 : Math.min(g, 1.0) * 0.006640625;
	}
}
