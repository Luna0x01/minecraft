package net.minecraft;

import java.util.Random;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.biome.Biome;

public class class_3876 extends class_3848 {
	@Override
	public BlockState method_17350(Random random, BlockPos blockPos) {
		double d = Biome.FOLIAGE_NOISE.noise((double)blockPos.getX() / 200.0, (double)blockPos.getZ() / 200.0);
		if (d < -0.8) {
			int i = random.nextInt(4);
			switch (i) {
				case 0:
					return Blocks.ORANGE_TULIP.getDefaultState();
				case 1:
					return Blocks.RED_TULIP.getDefaultState();
				case 2:
					return Blocks.PINK_TULIP.getDefaultState();
				case 3:
				default:
					return Blocks.WHITE_TULIP.getDefaultState();
			}
		} else if (random.nextInt(3) > 0) {
			int j = random.nextInt(3);
			if (j == 0) {
				return Blocks.POPPY.getDefaultState();
			} else {
				return j == 1 ? Blocks.AZURE_BLUET.getDefaultState() : Blocks.OXEYE_DAISY.getDefaultState();
			}
		} else {
			return Blocks.DANDELION.getDefaultState();
		}
	}
}
