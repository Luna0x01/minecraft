package net.minecraft;

import java.util.Random;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;

public class class_3834 extends class_3848 {
	@Override
	public BlockState method_17350(Random random, BlockPos blockPos) {
		return random.nextFloat() > 0.6666667F ? Blocks.DANDELION.getDefaultState() : Blocks.POPPY.getDefaultState();
	}
}
