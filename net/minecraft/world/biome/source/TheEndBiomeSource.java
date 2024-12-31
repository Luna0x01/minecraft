package net.minecraft.world.biome.source;

import com.google.common.collect.ImmutableSet;
import java.util.Set;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.noise.SimplexNoiseSampler;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.Biomes;
import net.minecraft.world.gen.ChunkRandom;

public class TheEndBiomeSource extends BiomeSource {
	private final SimplexNoiseSampler noise;
	private final ChunkRandom random;
	private static final Set<Biome> BIOMES = ImmutableSet.of(Biomes.field_9411, Biomes.field_9442, Biomes.field_9447, Biomes.field_9457, Biomes.field_9465);

	public TheEndBiomeSource(TheEndBiomeSourceConfig theEndBiomeSourceConfig) {
		super(BIOMES);
		this.random = new ChunkRandom(theEndBiomeSourceConfig.getSeed());
		this.random.consume(17292);
		this.noise = new SimplexNoiseSampler(this.random);
	}

	@Override
	public Biome getBiomeForNoiseGen(int i, int j, int k) {
		int l = i >> 2;
		int m = k >> 2;
		if ((long)l * (long)l + (long)m * (long)m <= 4096L) {
			return Biomes.field_9411;
		} else {
			float f = this.getNoiseRange(l * 2 + 1, m * 2 + 1);
			if (f > 40.0F) {
				return Biomes.field_9442;
			} else if (f >= 0.0F) {
				return Biomes.field_9447;
			} else {
				return f < -20.0F ? Biomes.field_9457 : Biomes.field_9465;
			}
		}
	}

	@Override
	public float getNoiseRange(int i, int j) {
		int k = i / 2;
		int l = j / 2;
		int m = i % 2;
		int n = j % 2;
		float f = 100.0F - MathHelper.sqrt((float)(i * i + j * j)) * 8.0F;
		f = MathHelper.clamp(f, -100.0F, 80.0F);

		for (int o = -12; o <= 12; o++) {
			for (int p = -12; p <= 12; p++) {
				long q = (long)(k + o);
				long r = (long)(l + p);
				if (q * q + r * r > 4096L && this.noise.sample((double)q, (double)r) < -0.9F) {
					float g = (MathHelper.abs((float)q) * 3439.0F + MathHelper.abs((float)r) * 147.0F) % 13.0F + 9.0F;
					float h = (float)(m - o * 2);
					float s = (float)(n - p * 2);
					float t = 100.0F - MathHelper.sqrt(h * h + s * s) * g;
					t = MathHelper.clamp(t, -100.0F, 80.0F);
					f = Math.max(f, t);
				}
			}
		}

		return f;
	}
}
