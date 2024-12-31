package net.minecraft;

import com.google.common.collect.Sets;
import java.util.List;
import java.util.Random;
import java.util.Set;
import javax.annotation.Nullable;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.SingletonBiomeSource;

public class class_3616 extends SingletonBiomeSource {
	private final Biome[] field_17683;
	private final int field_17684;

	public class_3616(class_3617 arg) {
		this.field_17683 = arg.method_16493();
		this.field_17684 = arg.method_16496() + 4;
	}

	@Override
	public Biome method_16480(BlockPos blockPos, @Nullable Biome biome) {
		return this.field_17683[Math.abs(((blockPos.getX() >> this.field_17684) + (blockPos.getZ() >> this.field_17684)) % this.field_17683.length)];
	}

	@Override
	public Biome[] method_16476(int i, int j, int k, int l) {
		return this.method_11540(i, j, k, l);
	}

	@Override
	public Biome[] method_16477(int i, int j, int k, int l, boolean bl) {
		Biome[] biomes = new Biome[k * l];

		for (int m = 0; m < l; m++) {
			for (int n = 0; n < k; n++) {
				int o = Math.abs(((i + m >> this.field_17684) + (j + n >> this.field_17684)) % this.field_17683.length);
				Biome biome = this.field_17683[o];
				biomes[m * k + n] = biome;
			}
		}

		return biomes;
	}

	@Nullable
	@Override
	public BlockPos method_16478(int i, int j, int k, List<Biome> list, Random random) {
		return null;
	}

	@Override
	public boolean method_16479(class_3902<?> arg) {
		return (Boolean)this.field_17661.computeIfAbsent(arg, argx -> {
			for (Biome biome : this.field_17683) {
				if (biome.method_16435(argx)) {
					return true;
				}
			}

			return false;
		});
	}

	@Override
	public Set<BlockState> method_16481() {
		if (this.field_17662.isEmpty()) {
			for (Biome biome : this.field_17683) {
				this.field_17662.add(biome.method_16450().method_17720());
			}
		}

		return this.field_17662;
	}

	@Override
	public Set<Biome> method_16475(int i, int j, int k) {
		return Sets.newHashSet(this.field_17683);
	}
}
