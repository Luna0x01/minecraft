package net.minecraft;

import com.google.common.collect.Sets;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.Set;
import javax.annotation.Nullable;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.SingletonBiomeSource;

public class class_3632 extends SingletonBiomeSource {
	private final Biome field_17688;

	public class_3632(class_3633 arg) {
		this.field_17688 = arg.method_16497();
	}

	@Override
	public Biome method_16480(BlockPos blockPos, @Nullable Biome biome) {
		return this.field_17688;
	}

	@Override
	public Biome[] method_16476(int i, int j, int k, int l) {
		return this.method_11540(i, j, k, l);
	}

	@Override
	public Biome[] method_16477(int i, int j, int k, int l, boolean bl) {
		Biome[] biomes = new Biome[k * l];
		Arrays.fill(biomes, 0, k * l, this.field_17688);
		return biomes;
	}

	@Nullable
	@Override
	public BlockPos method_16478(int i, int j, int k, List<Biome> list, Random random) {
		return list.contains(this.field_17688) ? new BlockPos(i - k + random.nextInt(k * 2 + 1), 0, j - k + random.nextInt(k * 2 + 1)) : null;
	}

	@Override
	public boolean method_16479(class_3902<?> arg) {
		return (Boolean)this.field_17661.computeIfAbsent(arg, this.field_17688::method_16435);
	}

	@Override
	public Set<BlockState> method_16481() {
		if (this.field_17662.isEmpty()) {
			this.field_17662.add(this.field_17688.method_16450().method_17720());
		}

		return this.field_17662;
	}

	@Override
	public Set<Biome> method_16475(int i, int j, int k) {
		return Sets.newHashSet(new Biome[]{this.field_17688});
	}
}
