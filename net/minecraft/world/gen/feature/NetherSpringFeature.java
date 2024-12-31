package net.minecraft.world.gen.feature;

import java.util.Random;
import net.minecraft.class_3798;
import net.minecraft.class_3844;
import net.minecraft.class_3850;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.fluid.Fluids;
import net.minecraft.server.world.ChunkGenerator;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;

public class NetherSpringFeature extends class_3844<class_3850> {
	private static final BlockState field_19207 = Blocks.NETHERRACK.getDefaultState();

	public boolean method_17343(IWorld iWorld, ChunkGenerator<? extends class_3798> chunkGenerator, Random random, BlockPos blockPos, class_3850 arg) {
		if (iWorld.getBlockState(blockPos.up()) != field_19207) {
			return false;
		} else if (!iWorld.getBlockState(blockPos).isAir() && iWorld.getBlockState(blockPos) != field_19207) {
			return false;
		} else {
			int i = 0;
			if (iWorld.getBlockState(blockPos.west()) == field_19207) {
				i++;
			}

			if (iWorld.getBlockState(blockPos.east()) == field_19207) {
				i++;
			}

			if (iWorld.getBlockState(blockPos.north()) == field_19207) {
				i++;
			}

			if (iWorld.getBlockState(blockPos.south()) == field_19207) {
				i++;
			}

			if (iWorld.getBlockState(blockPos.down()) == field_19207) {
				i++;
			}

			int j = 0;
			if (iWorld.method_8579(blockPos.west())) {
				j++;
			}

			if (iWorld.method_8579(blockPos.east())) {
				j++;
			}

			if (iWorld.method_8579(blockPos.north())) {
				j++;
			}

			if (iWorld.method_8579(blockPos.south())) {
				j++;
			}

			if (iWorld.method_8579(blockPos.down())) {
				j++;
			}

			if (!arg.field_19206 && i == 4 && j == 1 || i == 5) {
				iWorld.setBlockState(blockPos, Blocks.LAVA.getDefaultState(), 2);
				iWorld.method_16340().schedule(blockPos, Fluids.LAVA, 0);
			}

			return true;
		}
	}
}
