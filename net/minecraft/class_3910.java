package net.minecraft;

import java.util.Random;
import java.util.Set;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.CocoaBlock;
import net.minecraft.block.VineBlock;
import net.minecraft.block.material.Material;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.tag.BlockTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.IWorld;
import net.minecraft.world.gen.feature.FoliageFeature;

public class class_3910 extends FoliageFeature<class_3871> {
	private static final BlockState field_19269 = Blocks.OAK_LOG.getDefaultState();
	private static final BlockState field_19270 = Blocks.OAK_LEAVES.getDefaultState();
	protected final int field_19267;
	private final boolean field_19271;
	private final BlockState field_19266;
	private final BlockState field_19268;

	public class_3910(boolean bl) {
		this(bl, 4, field_19269, field_19270, false);
	}

	public class_3910(boolean bl, int i, BlockState blockState, BlockState blockState2, boolean bl2) {
		super(bl);
		this.field_19267 = i;
		this.field_19266 = blockState;
		this.field_19268 = blockState2;
		this.field_19271 = bl2;
	}

	@Override
	public boolean method_17294(Set<BlockPos> set, IWorld iWorld, Random random, BlockPos blockPos) {
		int i = this.method_17443(random);
		boolean bl = true;
		if (blockPos.getY() >= 1 && blockPos.getY() + i + 1 <= 256) {
			for (int j = blockPos.getY(); j <= blockPos.getY() + 1 + i; j++) {
				int k = 1;
				if (j == blockPos.getY()) {
					k = 0;
				}

				if (j >= blockPos.getY() + 1 + i - 2) {
					k = 2;
				}

				BlockPos.Mutable mutable = new BlockPos.Mutable();

				for (int l = blockPos.getX() - k; l <= blockPos.getX() + k && bl; l++) {
					for (int m = blockPos.getZ() - k; m <= blockPos.getZ() + k && bl; m++) {
						if (j < 0 || j >= 256) {
							bl = false;
						} else if (!this.isBlockReplaceable(iWorld.getBlockState(mutable.setPosition(l, j, m)).getBlock())) {
							bl = false;
						}
					}
				}
			}

			if (!bl) {
				return false;
			} else {
				Block block = iWorld.getBlockState(blockPos.down()).getBlock();
				if ((block == Blocks.GRASS_BLOCK || Block.method_16588(block) || block == Blocks.FARMLAND) && blockPos.getY() < 256 - i - 1) {
					this.method_17292(iWorld, blockPos.down());
					int n = 3;
					int o = 0;

					for (int p = blockPos.getY() - 3 + i; p <= blockPos.getY() + i; p++) {
						int q = p - (blockPos.getY() + i);
						int r = 1 - q / 2;

						for (int s = blockPos.getX() - r; s <= blockPos.getX() + r; s++) {
							int t = s - blockPos.getX();

							for (int u = blockPos.getZ() - r; u <= blockPos.getZ() + r; u++) {
								int v = u - blockPos.getZ();
								if (Math.abs(t) != r || Math.abs(v) != r || random.nextInt(2) != 0 && q != 0) {
									BlockPos blockPos2 = new BlockPos(s, p, u);
									BlockState blockState = iWorld.getBlockState(blockPos2);
									Material material = blockState.getMaterial();
									if (blockState.isAir() || blockState.isIn(BlockTags.LEAVES) || material == Material.REPLACEABLE_PLANT) {
										this.method_17344(iWorld, blockPos2, this.field_19268);
									}
								}
							}
						}
					}

					for (int w = 0; w < i; w++) {
						BlockState blockState2 = iWorld.getBlockState(blockPos.up(w));
						Material material2 = blockState2.getMaterial();
						if (blockState2.isAir() || blockState2.isIn(BlockTags.LEAVES) || material2 == Material.REPLACEABLE_PLANT) {
							this.method_17293(set, iWorld, blockPos.up(w), this.field_19266);
							if (this.field_19271 && w > 0) {
								if (random.nextInt(3) > 0 && iWorld.method_8579(blockPos.add(-1, w, 0))) {
									this.method_17442(iWorld, blockPos.add(-1, w, 0), VineBlock.field_18565);
								}

								if (random.nextInt(3) > 0 && iWorld.method_8579(blockPos.add(1, w, 0))) {
									this.method_17442(iWorld, blockPos.add(1, w, 0), VineBlock.field_18567);
								}

								if (random.nextInt(3) > 0 && iWorld.method_8579(blockPos.add(0, w, -1))) {
									this.method_17442(iWorld, blockPos.add(0, w, -1), VineBlock.field_18566);
								}

								if (random.nextInt(3) > 0 && iWorld.method_8579(blockPos.add(0, w, 1))) {
									this.method_17442(iWorld, blockPos.add(0, w, 1), VineBlock.field_18564);
								}
							}
						}
					}

					if (this.field_19271) {
						for (int x = blockPos.getY() - 3 + i; x <= blockPos.getY() + i; x++) {
							int y = x - (blockPos.getY() + i);
							int z = 2 - y / 2;
							BlockPos.Mutable mutable2 = new BlockPos.Mutable();

							for (int aa = blockPos.getX() - z; aa <= blockPos.getX() + z; aa++) {
								for (int ab = blockPos.getZ() - z; ab <= blockPos.getZ() + z; ab++) {
									mutable2.setPosition(aa, x, ab);
									if (iWorld.getBlockState(mutable2).isIn(BlockTags.LEAVES)) {
										BlockPos blockPos3 = mutable2.west();
										BlockPos blockPos4 = mutable2.east();
										BlockPos blockPos5 = mutable2.north();
										BlockPos blockPos6 = mutable2.south();
										if (random.nextInt(4) == 0 && iWorld.getBlockState(blockPos3).isAir()) {
											this.method_17444(iWorld, blockPos3, VineBlock.field_18565);
										}

										if (random.nextInt(4) == 0 && iWorld.getBlockState(blockPos4).isAir()) {
											this.method_17444(iWorld, blockPos4, VineBlock.field_18567);
										}

										if (random.nextInt(4) == 0 && iWorld.getBlockState(blockPos5).isAir()) {
											this.method_17444(iWorld, blockPos5, VineBlock.field_18566);
										}

										if (random.nextInt(4) == 0 && iWorld.getBlockState(blockPos6).isAir()) {
											this.method_17444(iWorld, blockPos6, VineBlock.field_18564);
										}
									}
								}
							}
						}

						if (random.nextInt(5) == 0 && i > 5) {
							for (int ac = 0; ac < 2; ac++) {
								for (Direction direction : Direction.DirectionType.HORIZONTAL) {
									if (random.nextInt(4 - ac) == 0) {
										Direction direction2 = direction.getOpposite();
										this.method_17441(iWorld, random.nextInt(3), blockPos.add(direction2.getOffsetX(), i - 5 + ac, direction2.getOffsetZ()), direction);
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

	protected int method_17443(Random random) {
		return this.field_19267 + random.nextInt(3);
	}

	private void method_17441(IWorld iWorld, int i, BlockPos blockPos, Direction direction) {
		this.method_17344(
			iWorld, blockPos, Blocks.COCOA.getDefaultState().withProperty(CocoaBlock.AGE, Integer.valueOf(i)).withProperty(CocoaBlock.FACING, direction)
		);
	}

	private void method_17442(IWorld iWorld, BlockPos blockPos, BooleanProperty booleanProperty) {
		this.method_17344(iWorld, blockPos, Blocks.VINE.getDefaultState().withProperty(booleanProperty, Boolean.valueOf(true)));
	}

	private void method_17444(IWorld iWorld, BlockPos blockPos, BooleanProperty booleanProperty) {
		this.method_17442(iWorld, blockPos, booleanProperty);
		int i = 4;

		for (BlockPos var5 = blockPos.down(); iWorld.getBlockState(var5).isAir() && i > 0; i--) {
			this.method_17442(iWorld, var5, booleanProperty);
			var5 = var5.down();
		}
	}
}
