package net.minecraft;

import java.util.Random;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.biome.Biome;

public class class_4006 extends class_4007 {
	private static final BlockState field_19450 = Blocks.WHITE_TERRACOTTA.getDefaultState();
	private static final BlockState field_19451 = Blocks.ORANGE_TERRACOTTA.getDefaultState();
	private static final BlockState field_19452 = Blocks.TERRACOTTA.getDefaultState();

	@Override
	public void method_17718(
		Random random, class_3781 arg, Biome biome, int i, int j, int k, double d, BlockState blockState, BlockState blockState2, int l, long m, class_4013 arg2
	) {
		int n = i & 15;
		int o = j & 15;
		BlockState blockState3 = field_19450;
		BlockState blockState4 = biome.method_16450().method_17721();
		int p = (int)(d / 3.0 + 3.0 + random.nextDouble() * 0.25);
		boolean bl = Math.cos(d / 3.0 * Math.PI) > 0.0;
		int q = -1;
		boolean bl2 = false;
		int r = 0;
		BlockPos.Mutable mutable = new BlockPos.Mutable();

		for (int s = k; s >= 0; s--) {
			if (r < 15) {
				mutable.setPosition(n, s, o);
				BlockState blockState5 = arg.getBlockState(mutable);
				if (blockState5.isAir()) {
					q = -1;
				} else if (blockState5.getBlock() == blockState.getBlock()) {
					if (q == -1) {
						bl2 = false;
						if (p <= 0) {
							blockState3 = Blocks.AIR.getDefaultState();
							blockState4 = blockState;
						} else if (s >= l - 4 && s <= l + 1) {
							blockState3 = field_19450;
							blockState4 = biome.method_16450().method_17721();
						}

						if (s < l && (blockState3 == null || blockState3.isAir())) {
							blockState3 = blockState2;
						}

						q = p + Math.max(0, s - l);
						if (s < l - 1) {
							arg.method_16994(mutable, blockState4, false);
							if (blockState4.getBlock() == field_19450) {
								arg.method_16994(mutable, field_19451, false);
							}
						} else if (s > 86 + p * 2) {
							if (bl) {
								arg.method_16994(mutable, Blocks.COARSE_DIRT.getDefaultState(), false);
							} else {
								arg.method_16994(mutable, Blocks.GRASS_BLOCK.getDefaultState(), false);
							}
						} else if (s <= l + 3 + p) {
							arg.method_16994(mutable, biome.method_16450().method_17720(), false);
							bl2 = true;
						} else {
							BlockState blockState6;
							if (s < 64 || s > 127) {
								blockState6 = field_19451;
							} else if (bl) {
								blockState6 = field_19452;
							} else {
								blockState6 = this.method_17707(i, s, j);
							}

							arg.method_16994(mutable, blockState6, false);
						}
					} else if (q > 0) {
						q--;
						if (bl2) {
							arg.method_16994(mutable, field_19451, false);
						} else {
							arg.method_16994(mutable, this.method_17707(i, s, j), false);
						}
					}

					r++;
				}
			}
		}
	}
}
