package net.minecraft.world.gen.feature;

import java.util.Random;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class LilyPadFeature extends Feature {
	@Override
	public boolean generate(World world, Random random, BlockPos blockPos) {
		for (int i = 0; i < 10; i++) {
			int j = blockPos.getX() + random.nextInt(8) - random.nextInt(8);
			int k = blockPos.getY() + random.nextInt(4) - random.nextInt(4);
			int l = blockPos.getZ() + random.nextInt(8) - random.nextInt(8);
			if (world.isAir(new BlockPos(j, k, l)) && Blocks.LILY_PAD.canBePlacedAtPos(world, new BlockPos(j, k, l))) {
				world.setBlockState(new BlockPos(j, k, l), Blocks.LILY_PAD.getDefaultState(), 2);
			}
		}

		return true;
	}
}
