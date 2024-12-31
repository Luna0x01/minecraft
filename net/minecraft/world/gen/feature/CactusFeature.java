package net.minecraft.world.gen.feature;

import java.util.Random;
import net.minecraft.class_3798;
import net.minecraft.class_3844;
import net.minecraft.class_3871;
import net.minecraft.block.Blocks;
import net.minecraft.server.world.ChunkGenerator;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;

public class CactusFeature extends class_3844<class_3871> {
	public boolean method_17343(IWorld iWorld, ChunkGenerator<? extends class_3798> chunkGenerator, Random random, BlockPos blockPos, class_3871 arg) {
		for (int i = 0; i < 10; i++) {
			BlockPos blockPos2 = blockPos.add(random.nextInt(8) - random.nextInt(8), random.nextInt(4) - random.nextInt(4), random.nextInt(8) - random.nextInt(8));
			if (iWorld.method_8579(blockPos2)) {
				int j = 1 + random.nextInt(random.nextInt(3) + 1);

				for (int k = 0; k < j; k++) {
					if (Blocks.CACTUS.getDefaultState().canPlaceAt(iWorld, blockPos2)) {
						iWorld.setBlockState(blockPos2.up(k), Blocks.CACTUS.getDefaultState(), 2);
					}
				}
			}
		}

		return true;
	}
}
