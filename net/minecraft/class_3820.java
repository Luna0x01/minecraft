package net.minecraft;

import java.util.Random;
import net.minecraft.block.Blocks;
import net.minecraft.block.ChorusFlowerBlock;
import net.minecraft.server.world.ChunkGenerator;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;

public class class_3820 extends class_3844<class_3871> {
	public boolean method_17343(IWorld iWorld, ChunkGenerator<? extends class_3798> chunkGenerator, Random random, BlockPos blockPos, class_3871 arg) {
		if (iWorld.method_8579(blockPos.up()) && iWorld.getBlockState(blockPos).getBlock() == Blocks.END_STONE) {
			ChorusFlowerBlock.generate(iWorld, blockPos.up(), random, 8);
			return true;
		} else {
			return false;
		}
	}
}
