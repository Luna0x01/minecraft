package net.minecraft.world.gen.feature;

import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.material.Material;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public abstract class AbstractGiantTreeFeature extends FoliageFeature {
	protected final int baseHeight;
	protected final BlockState treeLogState;
	protected final BlockState treeLeafState;
	protected int extraHeight;

	public AbstractGiantTreeFeature(boolean bl, int i, int j, BlockState blockState, BlockState blockState2) {
		super(bl);
		this.baseHeight = i;
		this.extraHeight = j;
		this.treeLogState = blockState;
		this.treeLeafState = blockState2;
	}

	protected int calculateMaxHeight(Random random) {
		int i = random.nextInt(3) + this.baseHeight;
		if (this.extraHeight > 1) {
			i += random.nextInt(this.extraHeight);
		}

		return i;
	}

	private boolean canGenerate(World world, BlockPos blockPos, int maxHeight) {
		boolean bl = true;
		if (blockPos.getY() >= 1 && blockPos.getY() + maxHeight + 1 <= 256) {
			for (int i = 0; i <= 1 + maxHeight; i++) {
				int j = 2;
				if (i == 0) {
					j = 1;
				} else if (i >= 1 + maxHeight - 2) {
					j = 2;
				}

				for (int k = -j; k <= j && bl; k++) {
					for (int l = -j; l <= j && bl; l++) {
						if (blockPos.getY() + i < 0 || blockPos.getY() + i >= 256 || !this.isBlockReplaceable(world.getBlockState(blockPos.add(k, i, l)).getBlock())) {
							bl = false;
						}
					}
				}
			}

			return bl;
		} else {
			return false;
		}
	}

	private boolean checkAndSetDirt(BlockPos blockPos, World world) {
		BlockPos blockPos2 = blockPos.down();
		Block block = world.getBlockState(blockPos2).getBlock();
		if ((block == Blocks.GRASS || block == Blocks.DIRT) && blockPos.getY() >= 2) {
			this.setDirt(world, blockPos2);
			this.setDirt(world, blockPos2.east());
			this.setDirt(world, blockPos2.south());
			this.setDirt(world, blockPos2.south().east());
			return true;
		} else {
			return false;
		}
	}

	protected boolean canGenerate(World world, Random random, BlockPos blockPos, int maxHeight) {
		return this.canGenerate(world, blockPos, maxHeight) && this.checkAndSetDirt(blockPos, world);
	}

	protected void generateLeavesLimited(World world, BlockPos blockPos, int radius) {
		int i = radius * radius;

		for (int j = -radius; j <= radius + 1; j++) {
			for (int k = -radius; k <= radius + 1; k++) {
				int l = j - 1;
				int m = k - 1;
				if (j * j + k * k <= i || l * l + m * m <= i || j * j + m * m <= i || l * l + k * k <= i) {
					BlockPos blockPos2 = blockPos.add(j, 0, k);
					Material material = world.getBlockState(blockPos2).getMaterial();
					if (material == Material.AIR || material == Material.FOLIAGE) {
						this.setBlockStateWithoutUpdatingNeighbors(world, blockPos2, this.treeLeafState);
					}
				}
			}
		}
	}

	protected void generateLeaves(World world, BlockPos blockPos, int radius) {
		int i = radius * radius;

		for (int j = -radius; j <= radius; j++) {
			for (int k = -radius; k <= radius; k++) {
				if (j * j + k * k <= i) {
					BlockPos blockPos2 = blockPos.add(j, 0, k);
					Material material = world.getBlockState(blockPos2).getMaterial();
					if (material == Material.AIR || material == Material.FOLIAGE) {
						this.setBlockStateWithoutUpdatingNeighbors(world, blockPos2, this.treeLeafState);
					}
				}
			}
		}
	}
}
