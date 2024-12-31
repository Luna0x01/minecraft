package net.minecraft;

import java.util.Random;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.server.world.ChunkGenerator;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.IWorld;
import net.minecraft.world.biome.Biome;

public class class_3898 extends class_3844<class_3871> {
	public boolean method_17343(IWorld iWorld, ChunkGenerator<? extends class_3798> chunkGenerator, Random random, BlockPos blockPos, class_3871 arg) {
		BlockPos.Mutable mutable = new BlockPos.Mutable();
		BlockPos.Mutable mutable2 = new BlockPos.Mutable();

		for (int i = 0; i < 16; i++) {
			for (int j = 0; j < 16; j++) {
				int k = blockPos.getX() + i;
				int l = blockPos.getZ() + j;
				int m = iWorld.method_16372(class_3804.class_3805.MOTION_BLOCKING, k, l);
				mutable.setPosition(k, m, l);
				mutable2.set(mutable).move(Direction.DOWN, 1);
				Biome biome = iWorld.method_8577(mutable);
				if (biome.method_16427(iWorld, mutable2, false)) {
					iWorld.setBlockState(mutable2, Blocks.ICE.getDefaultState(), 2);
				}

				if (biome.method_16439(iWorld, mutable)) {
					iWorld.setBlockState(mutable, Blocks.SNOW.getDefaultState(), 2);
					BlockState blockState = iWorld.getBlockState(mutable2);
					if (blockState.method_16933(class_3725.field_18495)) {
						iWorld.setBlockState(mutable2, blockState.withProperty(class_3725.field_18495, Boolean.valueOf(true)), 2);
					}
				}
			}
		}

		return true;
	}
}
