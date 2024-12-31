package net.minecraft.world.gen.feature;

import java.util.Random;
import net.minecraft.class_3798;
import net.minecraft.class_3844;
import net.minecraft.class_3871;
import net.minecraft.block.Blocks;
import net.minecraft.server.world.ChunkGenerator;
import net.minecraft.tag.FluidTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;

public class SugarcaneFeature extends class_3844<class_3871> {
	public boolean method_17343(IWorld iWorld, ChunkGenerator<? extends class_3798> chunkGenerator, Random random, BlockPos blockPos, class_3871 arg) {
		int i = 0;

		for (int j = 0; j < 20; j++) {
			BlockPos blockPos2 = blockPos.add(random.nextInt(4) - random.nextInt(4), 0, random.nextInt(4) - random.nextInt(4));
			if (iWorld.method_8579(blockPos2)) {
				BlockPos blockPos3 = blockPos2.down();
				if (iWorld.getFluidState(blockPos3.west()).matches(FluidTags.WATER)
					|| iWorld.getFluidState(blockPos3.east()).matches(FluidTags.WATER)
					|| iWorld.getFluidState(blockPos3.north()).matches(FluidTags.WATER)
					|| iWorld.getFluidState(blockPos3.south()).matches(FluidTags.WATER)) {
					int k = 2 + random.nextInt(random.nextInt(3) + 1);

					for (int l = 0; l < k; l++) {
						if (Blocks.SUGAR_CANE.getDefaultState().canPlaceAt(iWorld, blockPos2)) {
							iWorld.setBlockState(blockPos2.up(l), Blocks.SUGAR_CANE.getDefaultState(), 2);
							i++;
						}
					}
				}
			}
		}

		return i > 0;
	}
}
