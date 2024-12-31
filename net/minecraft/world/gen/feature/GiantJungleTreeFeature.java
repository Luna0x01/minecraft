package net.minecraft.world.gen.feature;

import java.util.Random;
import java.util.Set;
import net.minecraft.class_3871;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.VineBlock;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.IWorld;

public class GiantJungleTreeFeature extends AbstractGiantTreeFeature<class_3871> {
	public GiantJungleTreeFeature(boolean bl, int i, int j, BlockState blockState, BlockState blockState2) {
		super(bl, i, j, blockState, blockState2);
	}

	@Override
	public boolean method_17294(Set<BlockPos> set, IWorld iWorld, Random random, BlockPos blockPos) {
		int i = this.calculateMaxHeight(random);
		if (!this.method_6557(iWorld, blockPos, i)) {
			return false;
		} else {
			this.method_9212(iWorld, blockPos.up(i), 2);

			for (int j = blockPos.getY() + i - 2 - random.nextInt(4); j > blockPos.getY() + i / 2; j -= 2 + random.nextInt(4)) {
				float f = random.nextFloat() * (float) (Math.PI * 2);
				int k = blockPos.getX() + (int)(0.5F + MathHelper.cos(f) * 4.0F);
				int l = blockPos.getZ() + (int)(0.5F + MathHelper.sin(f) * 4.0F);

				for (int m = 0; m < 5; m++) {
					k = blockPos.getX() + (int)(1.5F + MathHelper.cos(f) * (float)m);
					l = blockPos.getZ() + (int)(1.5F + MathHelper.sin(f) * (float)m);
					this.method_17293(set, iWorld, new BlockPos(k, j - 3 + m / 2, l), this.treeLogState);
				}

				int n = 1 + random.nextInt(2);
				int o = j;

				for (int p = j - n; p <= o; p++) {
					int q = p - o;
					this.method_17386(iWorld, new BlockPos(k, p, l), 1 - q);
				}
			}

			for (int r = 0; r < i; r++) {
				BlockPos blockPos2 = blockPos.up(r);
				if (this.isBlockReplaceable(iWorld.getBlockState(blockPos2).getBlock())) {
					this.method_17293(set, iWorld, blockPos2, this.treeLogState);
					if (r > 0) {
						this.method_9211(iWorld, random, blockPos2.west(), VineBlock.field_18565);
						this.method_9211(iWorld, random, blockPos2.north(), VineBlock.field_18566);
					}
				}

				if (r < i - 1) {
					BlockPos blockPos3 = blockPos2.east();
					if (this.isBlockReplaceable(iWorld.getBlockState(blockPos3).getBlock())) {
						this.method_17293(set, iWorld, blockPos3, this.treeLogState);
						if (r > 0) {
							this.method_9211(iWorld, random, blockPos3.east(), VineBlock.field_18567);
							this.method_9211(iWorld, random, blockPos3.north(), VineBlock.field_18566);
						}
					}

					BlockPos blockPos4 = blockPos2.south().east();
					if (this.isBlockReplaceable(iWorld.getBlockState(blockPos4).getBlock())) {
						this.method_17293(set, iWorld, blockPos4, this.treeLogState);
						if (r > 0) {
							this.method_9211(iWorld, random, blockPos4.east(), VineBlock.field_18567);
							this.method_9211(iWorld, random, blockPos4.south(), VineBlock.field_18564);
						}
					}

					BlockPos blockPos5 = blockPos2.south();
					if (this.isBlockReplaceable(iWorld.getBlockState(blockPos5).getBlock())) {
						this.method_17293(set, iWorld, blockPos5, this.treeLogState);
						if (r > 0) {
							this.method_9211(iWorld, random, blockPos5.west(), VineBlock.field_18565);
							this.method_9211(iWorld, random, blockPos5.south(), VineBlock.field_18564);
						}
					}
				}
			}

			return true;
		}
	}

	private void method_9211(IWorld iWorld, Random random, BlockPos blockPos, BooleanProperty booleanProperty) {
		if (random.nextInt(3) > 0 && iWorld.method_8579(blockPos)) {
			this.method_17344(iWorld, blockPos, Blocks.VINE.getDefaultState().withProperty(booleanProperty, Boolean.valueOf(true)));
		}
	}

	private void method_9212(IWorld iWorld, BlockPos blockPos, int i) {
		int j = 2;

		for (int k = -2; k <= 0; k++) {
			this.method_17385(iWorld, blockPos.up(k), i + 1 - k);
		}
	}
}
