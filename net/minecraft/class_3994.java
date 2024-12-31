package net.minecraft;

import com.google.common.collect.ImmutableSet;
import java.util.BitSet;
import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.BlockView;
import net.minecraft.world.IWorld;

public class class_3994 extends class_3971 {
	private final float[] field_19414 = new float[1024];

	public class_3994() {
		this.field_19343 = ImmutableSet.of(
			Blocks.STONE,
			Blocks.GRANITE,
			Blocks.DIORITE,
			Blocks.ANDESITE,
			Blocks.DIRT,
			Blocks.COARSE_DIRT,
			new Block[]{
				Blocks.PODZOL,
				Blocks.GRASS_BLOCK,
				Blocks.TERRACOTTA,
				Blocks.WHITE_TERRACOTTA,
				Blocks.ORANGE_TERRACOTTA,
				Blocks.MAGENTA_TERRACOTTA,
				Blocks.LIGHT_BLUE_TERRACOTTA,
				Blocks.YELLOW_TERRACOTTA,
				Blocks.LIME_TERRACOTTA,
				Blocks.PINK_TERRACOTTA,
				Blocks.GRAY_TERRACOTTA,
				Blocks.LIGHT_GRAY_TERRACOTTA,
				Blocks.CYAN_TERRACOTTA,
				Blocks.PURPLE_TERRACOTTA,
				Blocks.BLUE_TERRACOTTA,
				Blocks.BROWN_TERRACOTTA,
				Blocks.GREEN_TERRACOTTA,
				Blocks.RED_TERRACOTTA,
				Blocks.BLACK_TERRACOTTA,
				Blocks.SANDSTONE,
				Blocks.RED_SANDSTONE,
				Blocks.MYCELIUM,
				Blocks.SNOW,
				Blocks.SAND,
				Blocks.GRAVEL,
				Blocks.WATER,
				Blocks.LAVA,
				Blocks.OBSIDIAN,
				Blocks.AIR,
				Blocks.CAVE_AIR
			}
		);
	}

	@Override
	public boolean method_17679(BlockView blockView, Random random, int i, int j, class_3877 arg) {
		return random.nextFloat() <= arg.field_19231;
	}

	@Override
	protected boolean method_17586(IWorld iWorld, long l, int i, int j, double d, double e, double f, double g, double h, BitSet bitSet) {
		Random random = new Random(l + (long)i + (long)j);
		double k = (double)(i * 16 + 8);
		double m = (double)(j * 16 + 8);
		if (!(d < k - 16.0 - g * 2.0) && !(f < m - 16.0 - g * 2.0) && !(d > k + 16.0 + g * 2.0) && !(f > m + 16.0 + g * 2.0)) {
			int n = Math.max(MathHelper.floor(d - g) - i * 16 - 1, 0);
			int o = Math.min(MathHelper.floor(d + g) - i * 16 + 1, 16);
			int p = Math.max(MathHelper.floor(e - h) - 1, 1);
			int q = Math.min(MathHelper.floor(e + h) + 1, 248);
			int r = Math.max(MathHelper.floor(f - g) - j * 16 - 1, 0);
			int s = Math.min(MathHelper.floor(f + g) - j * 16 + 1, 16);
			if (n <= o && p <= q && r <= s) {
				boolean bl = false;
				BlockPos.Mutable mutable = new BlockPos.Mutable();

				for (int t = n; t < o; t++) {
					int u = t + i * 16;
					double v = ((double)u + 0.5 - d) / g;

					for (int w = r; w < s; w++) {
						int x = w + j * 16;
						double y = ((double)x + 0.5 - f) / g;
						if (v * v + y * y < 1.0) {
							for (int z = q; z > p; z--) {
								double aa = ((double)(z - 1) + 0.5 - e) / h;
								if ((v * v + y * y) * (double)this.field_19414[z - 1] + aa * aa / 6.0 < 1.0 && z < iWorld.method_8483()) {
									int ab = t | w << 4 | z << 8;
									if (!bitSet.get(ab)) {
										bitSet.set(ab);
										mutable.setPosition(u, z, x);
										BlockState blockState = iWorld.getBlockState(mutable);
										if (this.method_17588(blockState)) {
											if (z == 10) {
												float ac = random.nextFloat();
												if ((double)ac < 0.25) {
													iWorld.setBlockState(mutable, Blocks.MAGMA_BLOCK.getDefaultState(), 2);
													iWorld.getBlockTickScheduler().schedule(mutable, Blocks.MAGMA_BLOCK, 0);
													bl = true;
												} else {
													iWorld.setBlockState(mutable, Blocks.OBSIDIAN.getDefaultState(), 2);
													bl = true;
												}
											} else if (z < 10) {
												iWorld.setBlockState(mutable, Blocks.LAVA.getDefaultState(), 2);
											} else {
												boolean bl2 = false;

												for (Direction direction : Direction.DirectionType.HORIZONTAL) {
													BlockState blockState2 = iWorld.getBlockState(mutable.setPosition(u + direction.getOffsetX(), z, x + direction.getOffsetZ()));
													if (blockState2.isAir()) {
														iWorld.setBlockState(mutable, field_19341.method_17813(), 2);
														iWorld.method_16340().schedule(mutable, field_19341.getFluid(), 0);
														bl = true;
														bl2 = true;
														break;
													}
												}

												mutable.setPosition(u, z, x);
												if (!bl2) {
													iWorld.setBlockState(mutable, field_19341.method_17813(), 2);
													bl = true;
												}
											}
										}
									}
								}
							}
						}
					}
				}

				return bl;
			} else {
				return false;
			}
		} else {
			return false;
		}
	}
}
