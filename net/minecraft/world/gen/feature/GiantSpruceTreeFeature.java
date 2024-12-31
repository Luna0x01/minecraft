package net.minecraft.world.gen.feature;

import java.util.Random;
import java.util.Set;
import net.minecraft.class_3871;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.tag.BlockTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.IWorld;

public class GiantSpruceTreeFeature extends AbstractGiantTreeFeature<class_3871> {
	private static final BlockState SPRUCE_LOGS = Blocks.SPRUCE_LOG.getDefaultState();
	private static final BlockState field_19213 = Blocks.SPRUCE_LEAVES.getDefaultState();
	private static final BlockState field_19214 = Blocks.PODZOL.getDefaultState();
	private final boolean useBaseHeight;

	public GiantSpruceTreeFeature(boolean bl, boolean bl2) {
		super(bl, 13, 15, SPRUCE_LOGS, field_19213);
		this.useBaseHeight = bl2;
	}

	@Override
	public boolean method_17294(Set<BlockPos> set, IWorld iWorld, Random random, BlockPos blockPos) {
		int i = this.calculateMaxHeight(random);
		if (!this.method_6557(iWorld, blockPos, i)) {
			return false;
		} else {
			this.method_6554(iWorld, blockPos.getX(), blockPos.getZ(), blockPos.getY() + i, 0, random);

			for (int j = 0; j < i; j++) {
				BlockState blockState = iWorld.getBlockState(blockPos.up(j));
				if (blockState.isAir() || blockState.isIn(BlockTags.LEAVES)) {
					this.method_17293(set, iWorld, blockPos.up(j), this.treeLogState);
				}

				if (j < i - 1) {
					blockState = iWorld.getBlockState(blockPos.add(1, j, 0));
					if (blockState.isAir() || blockState.isIn(BlockTags.LEAVES)) {
						this.method_17293(set, iWorld, blockPos.add(1, j, 0), this.treeLogState);
					}

					blockState = iWorld.getBlockState(blockPos.add(1, j, 1));
					if (blockState.isAir() || blockState.isIn(BlockTags.LEAVES)) {
						this.method_17293(set, iWorld, blockPos.add(1, j, 1), this.treeLogState);
					}

					blockState = iWorld.getBlockState(blockPos.add(0, j, 1));
					if (blockState.isAir() || blockState.isIn(BlockTags.LEAVES)) {
						this.method_17293(set, iWorld, blockPos.add(0, j, 1), this.treeLogState);
					}
				}
			}

			this.method_17382(iWorld, random, blockPos);
			return true;
		}
	}

	private void method_6554(IWorld iWorld, int i, int j, int k, int l, Random random) {
		int m = random.nextInt(5) + (this.useBaseHeight ? this.baseHeight : 3);
		int n = 0;

		for (int o = k - m; o <= k; o++) {
			int p = k - o;
			int q = l + MathHelper.floor((float)p / (float)m * 3.5F);
			this.method_17385(iWorld, new BlockPos(i, o, j), q + (p > 0 && q == n && (o & 1) == 0 ? 1 : 0));
			n = q;
		}
	}

	public void method_17382(IWorld iWorld, Random random, BlockPos blockPos) {
		this.method_9213(iWorld, blockPos.west().north());
		this.method_9213(iWorld, blockPos.east(2).north());
		this.method_9213(iWorld, blockPos.west().south(2));
		this.method_9213(iWorld, blockPos.east(2).south(2));

		for (int i = 0; i < 5; i++) {
			int j = random.nextInt(64);
			int k = j % 8;
			int l = j / 8;
			if (k == 0 || k == 7 || l == 0 || l == 7) {
				this.method_9213(iWorld, blockPos.add(-3 + k, 0, -3 + l));
			}
		}
	}

	private void method_9213(IWorld iWorld, BlockPos blockPos) {
		for (int i = -2; i <= 2; i++) {
			for (int j = -2; j <= 2; j++) {
				if (Math.abs(i) != 2 || Math.abs(j) != 2) {
					this.method_17383(iWorld, blockPos.add(i, 0, j));
				}
			}
		}
	}

	private void method_17383(IWorld iWorld, BlockPos blockPos) {
		for (int i = 2; i >= -3; i--) {
			BlockPos blockPos2 = blockPos.up(i);
			BlockState blockState = iWorld.getBlockState(blockPos2);
			Block block = blockState.getBlock();
			if (block == Blocks.GRASS_BLOCK || Block.method_16588(block)) {
				this.method_17344(iWorld, blockPos2, field_19214);
				break;
			}

			if (!blockState.isAir() && i < 0) {
				break;
			}
		}
	}
}
