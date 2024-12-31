package net.minecraft.world.gen.feature;

import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.Leaves1Block;
import net.minecraft.block.Log1Block;
import net.minecraft.block.PlanksBlock;
import net.minecraft.block.VineBlock;
import net.minecraft.block.material.Material;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class OakTreeFeature extends FoliageFeature {
	private static final BlockState LOG = Blocks.LOG.getDefaultState().with(Log1Block.VARIANT, PlanksBlock.WoodType.OAK);
	private static final BlockState LEAVES = Blocks.LEAVES
		.getDefaultState()
		.with(Leaves1Block.VARIANT, PlanksBlock.WoodType.OAK)
		.with(Leaves1Block.CHECK_DECAY, false);

	public OakTreeFeature() {
		super(false);
	}

	@Override
	public boolean generate(World world, Random random, BlockPos blockPos) {
		int i = random.nextInt(4) + 5;

		while (world.getBlockState(blockPos.down()).getMaterial() == Material.WATER) {
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
							BlockState blockState = world.getBlockState(mutable.setPosition(l, j, m));
							Block block = blockState.getBlock();
							if (blockState.getMaterial() != Material.AIR && blockState.getMaterial() != Material.FOLIAGE) {
								if (block != Blocks.WATER && block != Blocks.FLOWING_WATER) {
									bl = false;
								} else if (j > blockPos.getY()) {
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
				Block block2 = world.getBlockState(blockPos.down()).getBlock();
				if ((block2 == Blocks.GRASS || block2 == Blocks.DIRT) && blockPos.getY() < 256 - i - 1) {
					this.setDirt(world, blockPos.down());

					for (int n = blockPos.getY() - 3 + i; n <= blockPos.getY() + i; n++) {
						int o = n - (blockPos.getY() + i);
						int p = 2 - o / 2;

						for (int q = blockPos.getX() - p; q <= blockPos.getX() + p; q++) {
							int r = q - blockPos.getX();

							for (int s = blockPos.getZ() - p; s <= blockPos.getZ() + p; s++) {
								int t = s - blockPos.getZ();
								if (Math.abs(r) != p || Math.abs(t) != p || random.nextInt(2) != 0 && o != 0) {
									BlockPos blockPos2 = new BlockPos(q, n, s);
									if (!world.getBlockState(blockPos2).isFullBlock()) {
										this.setBlockStateWithoutUpdatingNeighbors(world, blockPos2, LEAVES);
									}
								}
							}
						}
					}

					for (int u = 0; u < i; u++) {
						BlockState blockState2 = world.getBlockState(blockPos.up(u));
						Block block3 = blockState2.getBlock();
						if (blockState2.getMaterial() == Material.AIR
							|| blockState2.getMaterial() == Material.FOLIAGE
							|| block3 == Blocks.FLOWING_WATER
							|| block3 == Blocks.WATER) {
							this.setBlockStateWithoutUpdatingNeighbors(world, blockPos.up(u), LOG);
						}
					}

					for (int v = blockPos.getY() - 3 + i; v <= blockPos.getY() + i; v++) {
						int w = v - (blockPos.getY() + i);
						int x = 2 - w / 2;
						BlockPos.Mutable mutable2 = new BlockPos.Mutable();

						for (int y = blockPos.getX() - x; y <= blockPos.getX() + x; y++) {
							for (int z = blockPos.getZ() - x; z <= blockPos.getZ() + x; z++) {
								mutable2.setPosition(y, v, z);
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

					return true;
				} else {
					return false;
				}
			}
		} else {
			return false;
		}
	}

	private void generateVines(World world, BlockPos blockPos, BooleanProperty attachedProperty) {
		BlockState blockState = Blocks.VINE.getDefaultState().with(attachedProperty, true);
		this.setBlockStateWithoutUpdatingNeighbors(world, blockPos, blockState);
		int i = 4;

		for (BlockPos var6 = blockPos.down(); world.getBlockState(var6).getMaterial() == Material.AIR && i > 0; i--) {
			this.setBlockStateWithoutUpdatingNeighbors(world, var6, blockState);
			var6 = var6.down();
		}
	}
}
