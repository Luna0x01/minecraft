package net.minecraft;

import java.util.Random;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.biome.Biome;

public class class_4001 implements class_4012<class_4013> {
	public void method_17718(
		Random random, class_3781 arg, Biome biome, int i, int j, int k, double d, BlockState blockState, BlockState blockState2, int l, long m, class_4013 arg2
	) {
		this.method_17703(random, arg, biome, i, j, k, d, blockState, blockState2, arg2.method_17720(), arg2.method_17721(), arg2.method_17719(), l);
	}

	protected void method_17703(
		Random random,
		class_3781 arg,
		Biome biome,
		int i,
		int j,
		int k,
		double d,
		BlockState blockState,
		BlockState blockState2,
		BlockState blockState3,
		BlockState blockState4,
		BlockState blockState5,
		int l
	) {
		BlockState blockState6 = blockState3;
		BlockState blockState7 = blockState4;
		BlockPos.Mutable mutable = new BlockPos.Mutable();
		int m = -1;
		int n = (int)(d / 3.0 + 3.0 + random.nextDouble() * 0.25);
		int o = i & 15;
		int p = j & 15;

		for (int q = k; q >= 0; q--) {
			mutable.setPosition(o, q, p);
			BlockState blockState8 = arg.getBlockState(mutable);
			if (blockState8.isAir()) {
				m = -1;
			} else if (blockState8.getBlock() == blockState.getBlock()) {
				if (m == -1) {
					if (n <= 0) {
						blockState6 = Blocks.AIR.getDefaultState();
						blockState7 = blockState;
					} else if (q >= l - 4 && q <= l + 1) {
						blockState6 = blockState3;
						blockState7 = blockState4;
					}

					if (q < l && (blockState6 == null || blockState6.isAir())) {
						if (biome.getTemperature(mutable.setPosition(i, q, j)) < 0.15F) {
							blockState6 = Blocks.ICE.getDefaultState();
						} else {
							blockState6 = blockState2;
						}

						mutable.setPosition(o, q, p);
					}

					m = n;
					if (q >= l - 1) {
						arg.method_16994(mutable, blockState6, false);
					} else if (q < l - 7 - n) {
						blockState6 = Blocks.AIR.getDefaultState();
						blockState7 = blockState;
						arg.method_16994(mutable, blockState5, false);
					} else {
						arg.method_16994(mutable, blockState7, false);
					}
				} else if (m > 0) {
					m--;
					arg.method_16994(mutable, blockState7, false);
					if (m == 0 && blockState7.getBlock() == Blocks.SAND && n > 1) {
						m = random.nextInt(4) + Math.max(0, q - 63);
						blockState7 = blockState7.getBlock() == Blocks.RED_SAND ? Blocks.RED_SANDSTONE.getDefaultState() : Blocks.SANDSTONE.getDefaultState();
					}
				}
			}
		}
	}
}
