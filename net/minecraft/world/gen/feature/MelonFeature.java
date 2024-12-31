package net.minecraft.world.gen.feature;

import java.util.Random;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class MelonFeature extends Feature {
	@Override
	public boolean generate(World world, Random random, BlockPos blockPos) {
		for (int i = 0; i < 64; i++) {
			BlockPos blockPos2 = blockPos.add(random.nextInt(8) - random.nextInt(8), random.nextInt(4) - random.nextInt(4), random.nextInt(8) - random.nextInt(8));
			if (Blocks.MELON_BLOCK.canBePlacedAtPos(world, blockPos2) && world.getBlockState(blockPos2.down()).getBlock() == Blocks.GRASS) {
				world.setBlockState(blockPos2, Blocks.MELON_BLOCK.getDefaultState(), 2);
			}
		}

		return true;
	}
}
