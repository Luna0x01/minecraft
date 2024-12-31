package net.minecraft.world.gen.feature;

import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.CocoaBlock;
import net.minecraft.block.Leaves1Block;
import net.minecraft.block.LeavesBlock;
import net.minecraft.block.Log1Block;
import net.minecraft.block.PlanksBlock;
import net.minecraft.block.VineBlock;
import net.minecraft.block.material.Material;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

public class JungleTreeFeature extends FoliageFeature {
	private static final BlockState LOG = Blocks.LOG.getDefaultState().with(Log1Block.VARIANT, PlanksBlock.WoodType.OAK);
	private static final BlockState LEAVES = Blocks.LEAVES
		.getDefaultState()
		.with(Leaves1Block.VARIANT, PlanksBlock.WoodType.OAK)
		.with(LeavesBlock.CHECK_DECAY, false);
	private final int baseHeight;
	private final boolean generateFeatures;
	private final BlockState treeLogState;
	private final BlockState treeLeafState;

	public JungleTreeFeature(boolean bl) {
		this(bl, 4, LOG, LEAVES, false);
	}

	public JungleTreeFeature(boolean bl, int i, BlockState blockState, BlockState blockState2, boolean bl2) {
		super(bl);
		this.baseHeight = i;
		this.treeLogState = blockState;
		this.treeLeafState = blockState2;
		this.generateFeatures = bl2;
	}

	@Override
	public boolean generate(World world, Random random, BlockPos blockPos) {
		int i = random.nextInt(3) + this.baseHeight;
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
						} else if (!this.isBlockReplaceable(world.getBlockState(mutable.setPosition(l, j, m)).getBlock())) {
							bl = false;
						}
					}
				}
			}

			if (!bl) {
				return false;
			} else {
				Block block = world.getBlockState(blockPos.down()).getBlock();
				if ((block == Blocks.GRASS || block == Blocks.DIRT || block == Blocks.FARMLAND) && blockPos.getY() < 256 - i - 1) {
					this.setDirt(world, blockPos.down());
					int n = 3;
					int o = 0;

					for (int p = blockPos.getY() - n + i; p <= blockPos.getY() + i; p++) {
						int q = p - (blockPos.getY() + i);
						int r = o + 1 - q / 2;

						for (int s = blockPos.getX() - r; s <= blockPos.getX() + r; s++) {
							int t = s - blockPos.getX();

							for (int u = blockPos.getZ() - r; u <= blockPos.getZ() + r; u++) {
								int v = u - blockPos.getZ();
								if (Math.abs(t) != r || Math.abs(v) != r || random.nextInt(2) != 0 && q != 0) {
									BlockPos blockPos2 = new BlockPos(s, p, u);
									Material material = world.getBlockState(blockPos2).getMaterial();
									if (material == Material.AIR || material == Material.FOLIAGE || material == Material.REPLACEABLE_PLANT) {
										this.setBlockStateWithoutUpdatingNeighbors(world, blockPos2, this.treeLeafState);
									}
								}
							}
						}
					}

					for (int w = 0; w < i; w++) {
						Material material2 = world.getBlockState(blockPos.up(w)).getMaterial();
						if (material2 == Material.AIR || material2 == Material.FOLIAGE || material2 == Material.REPLACEABLE_PLANT) {
							this.setBlockStateWithoutUpdatingNeighbors(world, blockPos.up(w), this.treeLogState);
							if (this.generateFeatures && w > 0) {
								if (random.nextInt(3) > 0 && world.isAir(blockPos.add(-1, w, 0))) {
									this.setVines(world, blockPos.add(-1, w, 0), VineBlock.EAST);
								}

								if (random.nextInt(3) > 0 && world.isAir(blockPos.add(1, w, 0))) {
									this.setVines(world, blockPos.add(1, w, 0), VineBlock.WEST);
								}

								if (random.nextInt(3) > 0 && world.isAir(blockPos.add(0, w, -1))) {
									this.setVines(world, blockPos.add(0, w, -1), VineBlock.SOUTH);
								}

								if (random.nextInt(3) > 0 && world.isAir(blockPos.add(0, w, 1))) {
									this.setVines(world, blockPos.add(0, w, 1), VineBlock.NORTH);
								}
							}
						}
					}

					if (this.generateFeatures) {
						for (int x = blockPos.getY() - 3 + i; x <= blockPos.getY() + i; x++) {
							int y = x - (blockPos.getY() + i);
							int z = 2 - y / 2;
							BlockPos.Mutable mutable2 = new BlockPos.Mutable();

							for (int aa = blockPos.getX() - z; aa <= blockPos.getX() + z; aa++) {
								for (int ab = blockPos.getZ() - z; ab <= blockPos.getZ() + z; ab++) {
									mutable2.setPosition(aa, x, ab);
									if (world.getBlockState(mutable2).getMaterial() == Material.FOLIAGE) {
										BlockPos blockPos3 = mutable2.west();
										BlockPos blockPos4 = mutable2.east();
										BlockPos blockPos5 = mutable2.north();
										BlockPos blockPos6 = mutable2.south();
										if (random.nextInt(4) == 0 && world.getBlockState(blockPos3).getMaterial() == Material.AIR) {
											this.generateVines(world, blockPos3, VineBlock.EAST);
										}

										if (random.nextInt(4) == 0 && world.getBlockState(blockPos4).getMaterial() == Material.AIR) {
											this.generateVines(world, blockPos4, VineBlock.WEST);
										}

										if (random.nextInt(4) == 0 && world.getBlockState(blockPos5).getMaterial() == Material.AIR) {
											this.generateVines(world, blockPos5, VineBlock.SOUTH);
										}

										if (random.nextInt(4) == 0 && world.getBlockState(blockPos6).getMaterial() == Material.AIR) {
											this.generateVines(world, blockPos6, VineBlock.NORTH);
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
										this.setCocoaBeans(world, random.nextInt(3), blockPos.add(direction2.getOffsetX(), i - 5 + ac, direction2.getOffsetZ()), direction);
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

	private void setCocoaBeans(World world, int age, BlockPos blockPos, Direction direction) {
		this.setBlockStateWithoutUpdatingNeighbors(world, blockPos, Blocks.COCOA.getDefaultState().with(CocoaBlock.AGE, age).with(CocoaBlock.DIRECTION, direction));
	}

	private void setVines(World world, BlockPos blockPos, BooleanProperty booleanProperty) {
		this.setBlockStateWithoutUpdatingNeighbors(world, blockPos, Blocks.VINE.getDefaultState().with(booleanProperty, true));
	}

	private void generateVines(World world, BlockPos blockPos, BooleanProperty booleanProperty) {
		this.setVines(world, blockPos, booleanProperty);
		int i = 4;

		for (BlockPos var5 = blockPos.down(); world.getBlockState(var5).getMaterial() == Material.AIR && i > 0; i--) {
			this.setVines(world, var5, booleanProperty);
			var5 = var5.down();
		}
	}
}
