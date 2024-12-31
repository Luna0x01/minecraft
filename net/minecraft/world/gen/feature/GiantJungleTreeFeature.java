package net.minecraft.world.gen.feature;

import java.util.Random;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.VineBlock;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

public class GiantJungleTreeFeature extends AbstractGiantTreeFeature {
	public GiantJungleTreeFeature(boolean bl, int i, int j, BlockState blockState, BlockState blockState2) {
		super(bl, i, j, blockState, blockState2);
	}

	@Override
	public boolean generate(World world, Random random, BlockPos blockPos) {
		int i = this.calculateMaxHeight(random);
		if (!this.canGenerate(world, random, blockPos, i)) {
			return false;
		} else {
			this.generateJungleLeaves(world, blockPos.up(i), 2);

			for (int j = blockPos.getY() + i - 2 - random.nextInt(4); j > blockPos.getY() + i / 2; j -= 2 + random.nextInt(4)) {
				float f = random.nextFloat() * (float) Math.PI * 2.0F;
				int k = blockPos.getX() + (int)(0.5F + MathHelper.cos(f) * 4.0F);
				int l = blockPos.getZ() + (int)(0.5F + MathHelper.sin(f) * 4.0F);

				for (int m = 0; m < 5; m++) {
					k = blockPos.getX() + (int)(1.5F + MathHelper.cos(f) * (float)m);
					l = blockPos.getZ() + (int)(1.5F + MathHelper.sin(f) * (float)m);
					this.setBlockStateWithoutUpdatingNeighbors(world, new BlockPos(k, j - 3 + m / 2, l), this.treeLogState);
				}

				int n = 1 + random.nextInt(2);
				int o = j;

				for (int p = j - n; p <= o; p++) {
					int q = p - o;
					this.generateLeaves(world, new BlockPos(k, p, l), 1 - q);
				}
			}

			for (int r = 0; r < i; r++) {
				BlockPos blockPos2 = blockPos.up(r);
				if (this.isBlockReplaceable(world.getBlockState(blockPos2).getBlock())) {
					this.setBlockStateWithoutUpdatingNeighbors(world, blockPos2, this.treeLogState);
					if (r > 0) {
						this.generateVines(world, random, blockPos2.west(), VineBlock.EAST);
						this.generateVines(world, random, blockPos2.north(), VineBlock.SOUTH);
					}
				}

				if (r < i - 1) {
					BlockPos blockPos3 = blockPos2.east();
					if (this.isBlockReplaceable(world.getBlockState(blockPos3).getBlock())) {
						this.setBlockStateWithoutUpdatingNeighbors(world, blockPos3, this.treeLogState);
						if (r > 0) {
							this.generateVines(world, random, blockPos3.east(), VineBlock.WEST);
							this.generateVines(world, random, blockPos3.north(), VineBlock.SOUTH);
						}
					}

					BlockPos blockPos4 = blockPos2.south().east();
					if (this.isBlockReplaceable(world.getBlockState(blockPos4).getBlock())) {
						this.setBlockStateWithoutUpdatingNeighbors(world, blockPos4, this.treeLogState);
						if (r > 0) {
							this.generateVines(world, random, blockPos4.east(), VineBlock.WEST);
							this.generateVines(world, random, blockPos4.south(), VineBlock.NORTH);
						}
					}

					BlockPos blockPos5 = blockPos2.south();
					if (this.isBlockReplaceable(world.getBlockState(blockPos5).getBlock())) {
						this.setBlockStateWithoutUpdatingNeighbors(world, blockPos5, this.treeLogState);
						if (r > 0) {
							this.generateVines(world, random, blockPos5.west(), VineBlock.EAST);
							this.generateVines(world, random, blockPos5.south(), VineBlock.NORTH);
						}
					}
				}
			}

			return true;
		}
	}

	private void generateVines(World world, Random random, BlockPos blockPos, BooleanProperty side) {
		if (random.nextInt(3) > 0 && world.isAir(blockPos)) {
			this.setBlockStateWithoutUpdatingNeighbors(world, blockPos, Blocks.VINE.getDefaultState().with(side, true));
		}
	}

	private void generateJungleLeaves(World world, BlockPos blockPos, int radius) {
		int i = 2;

		for (int j = -i; j <= 0; j++) {
			this.generateLeavesLimited(world, blockPos.up(j), radius + 1 - j);
		}
	}
}
