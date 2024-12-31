package net.minecraft.world.gen.feature;

import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.DirtBlock;
import net.minecraft.block.Leaves1Block;
import net.minecraft.block.LeavesBlock;
import net.minecraft.block.Log1Block;
import net.minecraft.block.PlanksBlock;
import net.minecraft.block.material.Material;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

public class GiantSpruceTreeFeature extends AbstractGiantTreeFeature {
	private static final BlockState SPRUCE_LOGS = Blocks.LOG.getDefaultState().with(Log1Block.VARIANT, PlanksBlock.WoodType.SPRUCE);
	private static final BlockState SPRUCE_LEAVES = Blocks.LEAVES
		.getDefaultState()
		.with(Leaves1Block.VARIANT, PlanksBlock.WoodType.SPRUCE)
		.with(LeavesBlock.CHECK_DECAY, false);
	private static final BlockState PODZOL = Blocks.DIRT.getDefaultState().with(DirtBlock.VARIANT, DirtBlock.DirtType.PODZOL);
	private boolean useBaseHeight;

	public GiantSpruceTreeFeature(boolean bl, boolean bl2) {
		super(bl, 13, 15, SPRUCE_LOGS, SPRUCE_LEAVES);
		this.useBaseHeight = bl2;
	}

	@Override
	public boolean generate(World world, Random random, BlockPos blockPos) {
		int i = this.calculateMaxHeight(random);
		if (!this.canGenerate(world, random, blockPos, i)) {
			return false;
		} else {
			this.generateTopLeaves(world, blockPos.getX(), blockPos.getZ(), blockPos.getY() + i, 0, random);

			for (int j = 0; j < i; j++) {
				BlockState blockState = world.getBlockState(blockPos.up(j));
				if (blockState.getMaterial() == Material.AIR || blockState.getMaterial() == Material.FOLIAGE) {
					this.setBlockStateWithoutUpdatingNeighbors(world, blockPos.up(j), this.treeLogState);
				}

				if (j < i - 1) {
					blockState = world.getBlockState(blockPos.add(1, j, 0));
					if (blockState.getMaterial() == Material.AIR || blockState.getMaterial() == Material.FOLIAGE) {
						this.setBlockStateWithoutUpdatingNeighbors(world, blockPos.add(1, j, 0), this.treeLogState);
					}

					blockState = world.getBlockState(blockPos.add(1, j, 1));
					if (blockState.getMaterial() == Material.AIR || blockState.getMaterial() == Material.FOLIAGE) {
						this.setBlockStateWithoutUpdatingNeighbors(world, blockPos.add(1, j, 1), this.treeLogState);
					}

					blockState = world.getBlockState(blockPos.add(0, j, 1));
					if (blockState.getMaterial() == Material.AIR || blockState.getMaterial() == Material.FOLIAGE) {
						this.setBlockStateWithoutUpdatingNeighbors(world, blockPos.add(0, j, 1), this.treeLogState);
					}
				}
			}

			return true;
		}
	}

	private void generateTopLeaves(World world, int posX, int posZ, int maxHeight, int nothing, Random random) {
		int i = random.nextInt(5) + (this.useBaseHeight ? this.baseHeight : 3);
		int j = 0;

		for (int k = maxHeight - i; k <= maxHeight; k++) {
			int l = maxHeight - k;
			int m = nothing + MathHelper.floor((float)l / (float)i * 3.5F);
			this.generateLeavesLimited(world, new BlockPos(posX, k, posZ), m + (l > 0 && m == j && (k & 1) == 0 ? 1 : 0));
			j = m;
		}
	}

	@Override
	public void generateSurroundingFeatures(World world, Random random, BlockPos pos) {
		this.setPodzol(world, pos.west().north());
		this.setPodzol(world, pos.east(2).north());
		this.setPodzol(world, pos.west().south(2));
		this.setPodzol(world, pos.east(2).south(2));

		for (int i = 0; i < 5; i++) {
			int j = random.nextInt(64);
			int k = j % 8;
			int l = j / 8;
			if (k == 0 || k == 7 || l == 0 || l == 7) {
				this.setPodzol(world, pos.add(-3 + k, 0, -3 + l));
			}
		}
	}

	private void setPodzol(World world, BlockPos blockPos) {
		for (int i = -2; i <= 2; i++) {
			for (int j = -2; j <= 2; j++) {
				if (Math.abs(i) != 2 || Math.abs(j) != 2) {
					this.changeDirtToPodzol(world, blockPos.add(i, 0, j));
				}
			}
		}
	}

	private void changeDirtToPodzol(World world, BlockPos blockPos) {
		for (int i = 2; i >= -3; i--) {
			BlockPos blockPos2 = blockPos.up(i);
			BlockState blockState = world.getBlockState(blockPos2);
			Block block = blockState.getBlock();
			if (block == Blocks.GRASS || block == Blocks.DIRT) {
				this.setBlockStateWithoutUpdatingNeighbors(world, blockPos2, PODZOL);
				break;
			}

			if (blockState.getMaterial() != Material.AIR && i < 0) {
				break;
			}
		}
	}
}
