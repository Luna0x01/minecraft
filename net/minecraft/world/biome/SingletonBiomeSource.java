package net.minecraft.world.biome;

import java.util.Arrays;
import java.util.List;
import java.util.Random;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.LayeredBiomeSource;

public class SingletonBiomeSource extends LayeredBiomeSource {
	private Biome biome;
	private float field_4719;

	public SingletonBiomeSource(Biome biome, float f) {
		this.biome = biome;
		this.field_4719 = f;
	}

	@Override
	public Biome getBiomeAt(BlockPos pos) {
		return this.biome;
	}

	@Override
	public Biome[] method_3857(Biome[] biomes, int i, int j, int k, int l) {
		if (biomes == null || biomes.length < k * l) {
			biomes = new Biome[k * l];
		}

		Arrays.fill(biomes, 0, k * l, this.biome);
		return biomes;
	}

	@Override
	public float[] method_3856(float[] fs, int x, int z, int w, int h) {
		if (fs == null || fs.length < w * h) {
			fs = new float[w * h];
		}

		Arrays.fill(fs, 0, w * h, this.field_4719);
		return fs;
	}

	@Override
	public Biome[] method_3861(Biome[] biomes, int i, int j, int k, int l) {
		if (biomes == null || biomes.length < k * l) {
			biomes = new Biome[k * l];
		}

		Arrays.fill(biomes, 0, k * l, this.biome);
		return biomes;
	}

	@Override
	public Biome[] method_3858(Biome[] biomes, int i, int j, int k, int l, boolean bl) {
		return this.method_3861(biomes, i, j, k, l);
	}

	@Override
	public BlockPos method_3855(int i, int j, int k, List<Biome> list, Random random) {
		return list.contains(this.biome) ? new BlockPos(i - k + random.nextInt(k * 2 + 1), 0, j - k + random.nextInt(k * 2 + 1)) : null;
	}

	@Override
	public boolean isValid(int x, int z, int radius, List<Biome> biomes) {
		return biomes.contains(this.biome);
	}
}
