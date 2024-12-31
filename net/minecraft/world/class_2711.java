package net.minecraft.world;

import java.util.Arrays;
import java.util.List;
import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.SingletonBiomeSource;

public class class_2711 extends SingletonBiomeSource {
	private final Biome field_12534;

	public class_2711(Biome biome) {
		this.field_12534 = biome;
	}

	@Override
	public Biome method_11535(BlockPos blockPos) {
		return this.field_12534;
	}

	@Override
	public Biome[] method_11537(Biome[] biomes, int i, int j, int k, int l) {
		if (biomes == null || biomes.length < k * l) {
			biomes = new Biome[k * l];
		}

		Arrays.fill(biomes, 0, k * l, this.field_12534);
		return biomes;
	}

	@Override
	public Biome[] method_11540(@Nullable Biome[] biomes, int i, int j, int k, int l) {
		if (biomes == null || biomes.length < k * l) {
			biomes = new Biome[k * l];
		}

		Arrays.fill(biomes, 0, k * l, this.field_12534);
		return biomes;
	}

	@Override
	public Biome[] method_11538(@Nullable Biome[] biomes, int i, int j, int k, int l, boolean bl) {
		return this.method_11540(biomes, i, j, k, l);
	}

	@Nullable
	@Override
	public BlockPos method_11534(int i, int j, int k, List<Biome> list, Random random) {
		return list.contains(this.field_12534) ? new BlockPos(i - k + random.nextInt(k * 2 + 1), 0, j - k + random.nextInt(k * 2 + 1)) : null;
	}

	@Override
	public boolean method_3854(int i, int j, int k, List<Biome> list) {
		return list.contains(this.field_12534);
	}

	@Override
	public boolean method_13697() {
		return true;
	}

	@Override
	public Biome method_13698() {
		return this.field_12534;
	}
}
