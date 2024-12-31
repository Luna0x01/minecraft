package net.minecraft;

import java.util.Random;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.biome.Biome;

public class class_4015 implements class_4012<class_4013> {
	public void method_17718(
		Random random, class_3781 arg, Biome biome, int i, int j, int k, double d, BlockState blockState, BlockState blockState2, int l, long m, class_4013 arg2
	) {
		double e = Biome.FOLIAGE_NOISE.noise((double)i * 0.25, (double)j * 0.25);
		if (e > 0.0) {
			int n = i & 15;
			int o = j & 15;
			BlockPos.Mutable mutable = new BlockPos.Mutable();

			for (int p = k; p >= 0; p--) {
				mutable.setPosition(n, p, o);
				if (!arg.getBlockState(mutable).isAir()) {
					if (p == 62 && arg.getBlockState(mutable).getBlock() != blockState2.getBlock()) {
						arg.method_16994(mutable, blockState2, false);
						if (e < 0.12) {
							arg.method_16994(mutable.method_19934(0, 1, 0), Blocks.LILY_PAD.getDefaultState(), false);
						}
					}
					break;
				}
			}
		}

		Biome.field_17594.method_17718(random, arg, biome, i, j, k, d, blockState, blockState2, l, m, arg2);
	}
}
