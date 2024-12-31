package net.minecraft;

import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.biome.Biome;

public class class_4005 extends class_4007 {
	private static final BlockState field_19447 = Blocks.WHITE_TERRACOTTA.getDefaultState();
	private static final BlockState field_19448 = Blocks.ORANGE_TERRACOTTA.getDefaultState();
	private static final BlockState field_19449 = Blocks.TERRACOTTA.getDefaultState();

	@Override
	public void method_17718(
		Random random, class_3781 arg, Biome biome, int i, int j, int k, double d, BlockState blockState, BlockState blockState2, int l, long m, class_4013 arg2
	) {
		double e = 0.0;
		double f = Math.min(Math.abs(d), this.field_19455.noise((double)i * 0.25, (double)j * 0.25));
		if (f > 0.0) {
			double g = 0.001953125;
			double h = Math.abs(this.field_19456.noise((double)i * 0.001953125, (double)j * 0.001953125));
			e = f * f * 2.5;
			double n = Math.ceil(h * 50.0) + 14.0;
			if (e > n) {
				e = n;
			}

			e += 64.0;
		}

		int o = i & 15;
		int p = j & 15;
		BlockState blockState3 = field_19447;
		BlockState blockState4 = biome.method_16450().method_17721();
		int q = (int)(d / 3.0 + 3.0 + random.nextDouble() * 0.25);
		boolean bl = Math.cos(d / 3.0 * Math.PI) > 0.0;
		int r = -1;
		boolean bl2 = false;
		BlockPos.Mutable mutable = new BlockPos.Mutable();

		for (int s = Math.max(k, (int)e + 1); s >= 0; s--) {
			mutable.setPosition(o, s, p);
			if (arg.getBlockState(mutable).isAir() && s < (int)e) {
				arg.method_16994(mutable, blockState, false);
			}

			BlockState blockState5 = arg.getBlockState(mutable);
			if (blockState5.isAir()) {
				r = -1;
			} else if (blockState5.getBlock() == blockState.getBlock()) {
				if (r == -1) {
					bl2 = false;
					if (q <= 0) {
						blockState3 = Blocks.AIR.getDefaultState();
						blockState4 = blockState;
					} else if (s >= l - 4 && s <= l + 1) {
						blockState3 = field_19447;
						blockState4 = biome.method_16450().method_17721();
					}

					if (s < l && (blockState3 == null || blockState3.isAir())) {
						blockState3 = blockState2;
					}

					r = q + Math.max(0, s - l);
					if (s >= l - 1) {
						if (s > l + 3 + q) {
							BlockState blockState6;
							if (s < 64 || s > 127) {
								blockState6 = field_19448;
							} else if (bl) {
								blockState6 = field_19449;
							} else {
								blockState6 = this.method_17707(i, s, j);
							}

							arg.method_16994(mutable, blockState6, false);
						} else {
							arg.method_16994(mutable, biome.method_16450().method_17720(), false);
							bl2 = true;
						}
					} else {
						arg.method_16994(mutable, blockState4, false);
						Block block = blockState4.getBlock();
						if (block == Blocks.WHITE_TERRACOTTA
							|| block == Blocks.ORANGE_TERRACOTTA
							|| block == Blocks.MAGENTA_TERRACOTTA
							|| block == Blocks.LIGHT_BLUE_TERRACOTTA
							|| block == Blocks.YELLOW_TERRACOTTA
							|| block == Blocks.LIME_TERRACOTTA
							|| block == Blocks.PINK_TERRACOTTA
							|| block == Blocks.GRAY_TERRACOTTA
							|| block == Blocks.LIGHT_GRAY_TERRACOTTA
							|| block == Blocks.CYAN_TERRACOTTA
							|| block == Blocks.PURPLE_TERRACOTTA
							|| block == Blocks.BLUE_TERRACOTTA
							|| block == Blocks.BROWN_TERRACOTTA
							|| block == Blocks.GREEN_TERRACOTTA
							|| block == Blocks.RED_TERRACOTTA
							|| block == Blocks.BLACK_TERRACOTTA) {
							arg.method_16994(mutable, field_19448, false);
						}
					}
				} else if (r > 0) {
					r--;
					if (bl2) {
						arg.method_16994(mutable, field_19448, false);
					} else {
						arg.method_16994(mutable, this.method_17707(i, s, j), false);
					}
				}
			}
		}
	}
}
