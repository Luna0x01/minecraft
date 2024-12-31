package net.minecraft.world.gen.feature;

import java.util.Random;
import net.minecraft.class_3798;
import net.minecraft.class_3819;
import net.minecraft.class_3844;
import net.minecraft.block.BlockState;
import net.minecraft.server.world.ChunkGenerator;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;

public class MushroomFeature extends class_3844<class_3819> {
	public boolean method_17343(IWorld iWorld, ChunkGenerator<? extends class_3798> chunkGenerator, Random random, BlockPos blockPos, class_3819 arg) {
		int i = 0;
		BlockState blockState = arg.field_19073.getDefaultState();

		for (int j = 0; j < 64; j++) {
			BlockPos blockPos2 = blockPos.add(random.nextInt(8) - random.nextInt(8), random.nextInt(4) - random.nextInt(4), random.nextInt(8) - random.nextInt(8));
			if (iWorld.method_8579(blockPos2) && (!iWorld.method_16393().hasNoSkylight() || blockPos2.getY() < 255) && blockState.canPlaceAt(iWorld, blockPos2)) {
				iWorld.setBlockState(blockPos2, blockState, 2);
				i++;
			}
		}

		return i > 0;
	}
}
