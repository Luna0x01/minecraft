package net.minecraft;

import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.server.world.ChunkGenerator;
import net.minecraft.tag.FluidTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;

public class class_3839 extends class_3844<class_3838> {
	public boolean method_17343(IWorld iWorld, ChunkGenerator<? extends class_3798> chunkGenerator, Random random, BlockPos blockPos, class_3838 arg) {
		if (!iWorld.getFluidState(blockPos).matches(FluidTags.WATER)) {
			return false;
		} else {
			int i = 0;
			int j = random.nextInt(arg.field_19116 - 2) + 2;

			for (int k = blockPos.getX() - j; k <= blockPos.getX() + j; k++) {
				for (int l = blockPos.getZ() - j; l <= blockPos.getZ() + j; l++) {
					int m = k - blockPos.getX();
					int n = l - blockPos.getZ();
					if (m * m + n * n <= j * j) {
						for (int o = blockPos.getY() - arg.field_19117; o <= blockPos.getY() + arg.field_19117; o++) {
							BlockPos blockPos2 = new BlockPos(k, o, l);
							Block block = iWorld.getBlockState(blockPos2).getBlock();
							if (arg.field_19118.contains(block)) {
								iWorld.setBlockState(blockPos2, arg.field_19115.getDefaultState(), 2);
								i++;
							}
						}
					}
				}
			}

			return i > 0;
		}
	}
}
