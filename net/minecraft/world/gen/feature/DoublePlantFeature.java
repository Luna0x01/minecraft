package net.minecraft.world.gen.feature;

import java.util.Random;
import net.minecraft.class_3798;
import net.minecraft.class_3840;
import net.minecraft.class_3844;
import net.minecraft.block.DoublePlantBlock;
import net.minecraft.server.world.ChunkGenerator;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;

public class DoublePlantFeature extends class_3844<class_3840> {
	public boolean method_17343(IWorld iWorld, ChunkGenerator<? extends class_3798> chunkGenerator, Random random, BlockPos blockPos, class_3840 arg) {
		boolean bl = false;

		for (int i = 0; i < 64; i++) {
			BlockPos blockPos2 = blockPos.add(random.nextInt(8) - random.nextInt(8), random.nextInt(4) - random.nextInt(4), random.nextInt(8) - random.nextInt(8));
			if (iWorld.method_8579(blockPos2) && blockPos2.getY() < 254 && arg.field_19119.canPlaceAt(iWorld, blockPos2)) {
				((DoublePlantBlock)arg.field_19119.getBlock()).method_16669(iWorld, blockPos2, 2);
				bl = true;
			}
		}

		return bl;
	}
}
