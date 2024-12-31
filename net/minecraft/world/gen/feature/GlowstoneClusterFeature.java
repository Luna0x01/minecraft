package net.minecraft.world.gen.feature;

import java.util.Random;
import net.minecraft.block.Blocks;
import net.minecraft.block.material.Material;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

public class GlowstoneClusterFeature extends Feature {
	@Override
	public boolean generate(World world, Random random, BlockPos blockPos) {
		if (!world.isAir(blockPos)) {
			return false;
		} else if (world.getBlockState(blockPos.up()).getBlock() != Blocks.NETHERRACK) {
			return false;
		} else {
			world.setBlockState(blockPos, Blocks.GLOWSTONE.getDefaultState(), 2);

			for (int i = 0; i < 1500; i++) {
				BlockPos blockPos2 = blockPos.add(random.nextInt(8) - random.nextInt(8), -random.nextInt(12), random.nextInt(8) - random.nextInt(8));
				if (world.getBlockState(blockPos2).getBlock().getMaterial() == Material.AIR) {
					int j = 0;

					for (Direction direction : Direction.values()) {
						if (world.getBlockState(blockPos2.offset(direction)).getBlock() == Blocks.GLOWSTONE) {
							j++;
						}

						if (j > 1) {
							break;
						}
					}

					if (j == 1) {
						world.setBlockState(blockPos2, Blocks.GLOWSTONE.getDefaultState(), 2);
					}
				}
			}

			return true;
		}
	}
}
