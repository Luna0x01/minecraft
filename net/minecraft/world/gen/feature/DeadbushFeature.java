package net.minecraft.world.gen.feature;

import java.util.Random;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.material.Material;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class DeadbushFeature extends Feature {
	@Override
	public boolean generate(World world, Random random, BlockPos blockPos) {
		for (BlockState blockState = world.getBlockState(blockPos);
			(blockState.getMaterial() == Material.AIR || blockState.getMaterial() == Material.FOLIAGE) && blockPos.getY() > 0;
			blockState = world.getBlockState(blockPos)
		) {
			blockPos = blockPos.down();
		}

		for (int i = 0; i < 4; i++) {
			BlockPos blockPos2 = blockPos.add(random.nextInt(8) - random.nextInt(8), random.nextInt(4) - random.nextInt(4), random.nextInt(8) - random.nextInt(8));
			if (world.isAir(blockPos2) && Blocks.DEADBUSH.canPlantAt(world, blockPos2, Blocks.DEADBUSH.getDefaultState())) {
				world.setBlockState(blockPos2, Blocks.DEADBUSH.getDefaultState(), 2);
			}
		}

		return true;
	}
}
