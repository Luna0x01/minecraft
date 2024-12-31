package net.minecraft;

import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.server.world.ChunkGenerator;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockView;
import net.minecraft.world.IWorld;
import net.minecraft.world.gen.feature.FoliageFeature;

public abstract class class_3747 extends class_3748 {
	@Override
	public boolean method_16849(IWorld iWorld, BlockPos blockPos, BlockState blockState, Random random) {
		for (int i = 0; i >= -1; i--) {
			for (int j = 0; j >= -1; j--) {
				if (method_16847(blockState, iWorld, blockPos, i, j)) {
					return this.method_16846(iWorld, blockPos, blockState, random, i, j);
				}
			}
		}

		return super.method_16849(iWorld, blockPos, blockState, random);
	}

	@Nullable
	protected abstract FoliageFeature<class_3871> method_16848(Random random);

	public boolean method_16846(IWorld iWorld, BlockPos blockPos, BlockState blockState, Random random, int i, int j) {
		FoliageFeature<class_3871> foliageFeature = this.method_16848(random);
		if (foliageFeature == null) {
			return false;
		} else {
			BlockState blockState2 = Blocks.AIR.getDefaultState();
			iWorld.setBlockState(blockPos.add(i, 0, j), blockState2, 4);
			iWorld.setBlockState(blockPos.add(i + 1, 0, j), blockState2, 4);
			iWorld.setBlockState(blockPos.add(i, 0, j + 1), blockState2, 4);
			iWorld.setBlockState(blockPos.add(i + 1, 0, j + 1), blockState2, 4);
			if (foliageFeature.method_17343(
				iWorld, (ChunkGenerator<? extends class_3798>)iWorld.method_3586().method_17046(), random, blockPos.add(i, 0, j), class_3845.field_19203
			)) {
				return true;
			} else {
				iWorld.setBlockState(blockPos.add(i, 0, j), blockState, 4);
				iWorld.setBlockState(blockPos.add(i + 1, 0, j), blockState, 4);
				iWorld.setBlockState(blockPos.add(i, 0, j + 1), blockState, 4);
				iWorld.setBlockState(blockPos.add(i + 1, 0, j + 1), blockState, 4);
				return false;
			}
		}
	}

	public static boolean method_16847(BlockState blockState, BlockView blockView, BlockPos blockPos, int i, int j) {
		Block block = blockState.getBlock();
		return block == blockView.getBlockState(blockPos.add(i, 0, j)).getBlock()
			&& block == blockView.getBlockState(blockPos.add(i + 1, 0, j)).getBlock()
			&& block == blockView.getBlockState(blockPos.add(i, 0, j + 1)).getBlock()
			&& block == blockView.getBlockState(blockPos.add(i + 1, 0, j + 1)).getBlock();
	}
}
