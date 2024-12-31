package net.minecraft.world.gen.feature;

import java.util.Random;
import net.minecraft.class_3845;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.tag.BlockTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockView;
import net.minecraft.world.IWorld;

public abstract class AbstractGiantTreeFeature<T extends class_3845> extends FoliageFeature<T> {
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

	private boolean method_9218(BlockView blockView, BlockPos blockPos, int i) {
		boolean bl = true;
		if (blockPos.getY() >= 1 && blockPos.getY() + i + 1 <= 256) {
			for (int j = 0; j <= 1 + i; j++) {
				int k = 2;
				if (j == 0) {
					k = 1;
				} else if (j >= 1 + i - 2) {
					k = 2;
				}

				for (int l = -k; l <= k && bl; l++) {
					for (int m = -k; m <= k && bl; m++) {
						if (blockPos.getY() + j < 0 || blockPos.getY() + j >= 256 || !this.isBlockReplaceable(blockView.getBlockState(blockPos.add(l, j, m)).getBlock())) {
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

	private boolean method_17384(IWorld world, BlockPos pos) {
		BlockPos blockPos = pos.down();
		Block block = world.getBlockState(blockPos).getBlock();
		if ((block == Blocks.GRASS_BLOCK || Block.method_16588(block)) && pos.getY() >= 2) {
			this.method_17292(world, blockPos);
			this.method_17292(world, blockPos.east());
			this.method_17292(world, blockPos.south());
			this.method_17292(world, blockPos.south().east());
			return true;
		} else {
			return false;
		}
	}

	protected boolean method_6557(IWorld world, BlockPos pos, int i) {
		return this.method_9218(world, pos, i) && this.method_17384(world, pos);
	}

	protected void method_17385(IWorld iWorld, BlockPos blockPos, int i) {
		int j = i * i;

		for (int k = -i; k <= i + 1; k++) {
			for (int l = -i; l <= i + 1; l++) {
				int m = Math.min(Math.abs(k), Math.abs(k - 1));
				int n = Math.min(Math.abs(l), Math.abs(l - 1));
				if (m + n < 7 && m * m + n * n <= j) {
					BlockPos blockPos2 = blockPos.add(k, 0, l);
					BlockState blockState = iWorld.getBlockState(blockPos2);
					if (blockState.isAir() || blockState.isIn(BlockTags.LEAVES)) {
						this.method_17344(iWorld, blockPos2, this.treeLeafState);
					}
				}
			}
		}
	}

	protected void method_17386(IWorld iWorld, BlockPos blockPos, int i) {
		int j = i * i;

		for (int k = -i; k <= i; k++) {
			for (int l = -i; l <= i; l++) {
				if (k * k + l * l <= j) {
					BlockPos blockPos2 = blockPos.add(k, 0, l);
					BlockState blockState = iWorld.getBlockState(blockPos2);
					if (blockState.isAir() || blockState.isIn(BlockTags.LEAVES)) {
						this.method_17344(iWorld, blockPos2, this.treeLeafState);
					}
				}
			}
		}
	}
}
