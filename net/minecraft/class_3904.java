package net.minecraft;

import java.util.Random;
import java.util.Set;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.VineBlock;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.tag.BlockTags;
import net.minecraft.tag.FluidTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.gen.feature.FoliageFeature;

public class class_3904 extends FoliageFeature<class_3871> {
	private static final BlockState field_19262 = Blocks.OAK_LOG.getDefaultState();
	private static final BlockState field_19263 = Blocks.OAK_LEAVES.getDefaultState();

	public class_3904() {
		super(false);
	}

	@Override
	public boolean method_17294(Set<BlockPos> set, IWorld iWorld, Random random, BlockPos blockPos) {
		int i = random.nextInt(4) + 5;

		while (iWorld.getFluidState(blockPos.down()).matches(FluidTags.WATER)) {
			blockPos = blockPos.down();
		}

		boolean bl = true;
		if (blockPos.getY() >= 1 && blockPos.getY() + i + 1 <= 256) {
			for (int j = blockPos.getY(); j <= blockPos.getY() + 1 + i; j++) {
				int k = 1;
				if (j == blockPos.getY()) {
					k = 0;
				}

				if (j >= blockPos.getY() + 1 + i - 2) {
					k = 3;
				}

				BlockPos.Mutable mutable = new BlockPos.Mutable();

				for (int l = blockPos.getX() - k; l <= blockPos.getX() + k && bl; l++) {
					for (int m = blockPos.getZ() - k; m <= blockPos.getZ() + k && bl; m++) {
						if (j >= 0 && j < 256) {
							BlockState blockState = iWorld.getBlockState(mutable.setPosition(l, j, m));
							Block block = blockState.getBlock();
							if (!blockState.isAir() && !blockState.isIn(BlockTags.LEAVES)) {
								if (block == Blocks.WATER) {
									if (j > blockPos.getY()) {
										bl = false;
									}
								} else {
									bl = false;
								}
							}
						} else {
							bl = false;
						}
					}
				}
			}

			if (!bl) {
				return false;
			} else {
				Block block2 = iWorld.getBlockState(blockPos.down()).getBlock();
				if ((block2 == Blocks.GRASS_BLOCK || Block.method_16588(block2)) && blockPos.getY() < 256 - i - 1) {
					this.method_17292(iWorld, blockPos.down());

					for (int n = blockPos.getY() - 3 + i; n <= blockPos.getY() + i; n++) {
						int o = n - (blockPos.getY() + i);
						int p = 2 - o / 2;

						for (int q = blockPos.getX() - p; q <= blockPos.getX() + p; q++) {
							int r = q - blockPos.getX();

							for (int s = blockPos.getZ() - p; s <= blockPos.getZ() + p; s++) {
								int t = s - blockPos.getZ();
								if (Math.abs(r) != p || Math.abs(t) != p || random.nextInt(2) != 0 && o != 0) {
									BlockPos blockPos2 = new BlockPos(q, n, s);
									if (!iWorld.getBlockState(blockPos2).isFullOpaque(iWorld, blockPos2)) {
										this.method_17344(iWorld, blockPos2, field_19263);
									}
								}
							}
						}
					}

					for (int u = 0; u < i; u++) {
						BlockState blockState2 = iWorld.getBlockState(blockPos.up(u));
						Block block3 = blockState2.getBlock();
						if (blockState2.isAir() || blockState2.isIn(BlockTags.LEAVES) || block3 == Blocks.WATER) {
							this.method_17293(set, iWorld, blockPos.up(u), field_19262);
						}
					}

					for (int v = blockPos.getY() - 3 + i; v <= blockPos.getY() + i; v++) {
						int w = v - (blockPos.getY() + i);
						int x = 2 - w / 2;
						BlockPos.Mutable mutable2 = new BlockPos.Mutable();

						for (int y = blockPos.getX() - x; y <= blockPos.getX() + x; y++) {
							for (int z = blockPos.getZ() - x; z <= blockPos.getZ() + x; z++) {
								mutable2.setPosition(y, v, z);
								if (iWorld.getBlockState(mutable2).isIn(BlockTags.LEAVES)) {
									BlockPos blockPos3 = mutable2.west();
									BlockPos blockPos4 = mutable2.east();
									BlockPos blockPos5 = mutable2.north();
									BlockPos blockPos6 = mutable2.south();
									if (random.nextInt(4) == 0 && iWorld.getBlockState(blockPos3).isAir()) {
										this.method_17436(iWorld, blockPos3, VineBlock.field_18565);
									}

									if (random.nextInt(4) == 0 && iWorld.getBlockState(blockPos4).isAir()) {
										this.method_17436(iWorld, blockPos4, VineBlock.field_18567);
									}

									if (random.nextInt(4) == 0 && iWorld.getBlockState(blockPos5).isAir()) {
										this.method_17436(iWorld, blockPos5, VineBlock.field_18566);
									}

									if (random.nextInt(4) == 0 && iWorld.getBlockState(blockPos6).isAir()) {
										this.method_17436(iWorld, blockPos6, VineBlock.field_18564);
									}
								}
							}
						}
					}

					return true;
				} else {
					return false;
				}
			}
		} else {
			return false;
		}
	}

	private void method_17436(IWorld iWorld, BlockPos blockPos, BooleanProperty booleanProperty) {
		BlockState blockState = Blocks.VINE.getDefaultState().withProperty(booleanProperty, Boolean.valueOf(true));
		this.method_17344(iWorld, blockPos, blockState);
		int i = 4;

		for (BlockPos var6 = blockPos.down(); iWorld.getBlockState(var6).isAir() && i > 0; i--) {
			this.method_17344(iWorld, var6, blockState);
			var6 = var6.down();
		}
	}
}
