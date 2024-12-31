package net.minecraft;

import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.server.world.ChunkGenerator;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.gen.feature.FoliageFeature;

public abstract class class_3748 {
	@Nullable
	protected abstract FoliageFeature<class_3871> method_16850(Random random);

	public boolean method_16849(IWorld iWorld, BlockPos blockPos, BlockState blockState, Random random) {
		FoliageFeature<class_3871> foliageFeature = this.method_16850(random);
		if (foliageFeature == null) {
			return false;
		} else {
			iWorld.setBlockState(blockPos, Blocks.AIR.getDefaultState(), 4);
			if (foliageFeature.method_17343(iWorld, (ChunkGenerator<? extends class_3798>)iWorld.method_3586().method_17046(), random, blockPos, class_3845.field_19203)
				)
			 {
				return true;
			} else {
				iWorld.setBlockState(blockPos, blockState, 4);
				return false;
			}
		}
	}
}
