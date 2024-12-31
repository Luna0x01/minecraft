package net.minecraft.world.gen.feature;

import java.util.Random;
import net.minecraft.block.Blocks;
import net.minecraft.block.material.Material;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class SugarcaneFeature extends Feature {
	@Override
	public boolean generate(World world, Random random, BlockPos blockPos) {
		for (int i = 0; i < 20; i++) {
			BlockPos blockPos2 = blockPos.add(random.nextInt(4) - random.nextInt(4), 0, random.nextInt(4) - random.nextInt(4));
			if (world.isAir(blockPos2)) {
				BlockPos blockPos3 = blockPos2.down();
				if (world.getBlockState(blockPos3.west()).getBlock().getMaterial() == Material.WATER
					|| world.getBlockState(blockPos3.east()).getBlock().getMaterial() == Material.WATER
					|| world.getBlockState(blockPos3.north()).getBlock().getMaterial() == Material.WATER
					|| world.getBlockState(blockPos3.south()).getBlock().getMaterial() == Material.WATER) {
					int j = 2 + random.nextInt(random.nextInt(3) + 1);

					for (int k = 0; k < j; k++) {
						if (Blocks.SUGARCANE.canPlaceAt(world, blockPos2)) {
							world.setBlockState(blockPos2.up(k), Blocks.SUGARCANE.getDefaultState(), 2);
						}
					}
				}
			}
		}

		return true;
	}
}
