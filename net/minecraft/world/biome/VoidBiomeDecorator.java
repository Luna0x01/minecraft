package net.minecraft.world.biome;

import java.util.Random;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class VoidBiomeDecorator extends BiomeDecorator {
	@Override
	public void decorate(World world, Random random, Biome biome, BlockPos pos) {
		BlockPos blockPos = world.getSpawnPos();
		int i = 16;
		double d = blockPos.getSquaredDistance(pos.add(8, blockPos.getY(), 8));
		if (!(d > 1024.0)) {
			BlockPos blockPos2 = new BlockPos(blockPos.getX() - 16, blockPos.getY() - 1, blockPos.getZ() - 16);
			BlockPos blockPos3 = new BlockPos(blockPos.getX() + 16, blockPos.getY() - 1, blockPos.getZ() + 16);
			BlockPos.Mutable mutable = new BlockPos.Mutable(blockPos2);

			for (int j = pos.getZ(); j < pos.getZ() + 16; j++) {
				for (int k = pos.getX(); k < pos.getX() + 16; k++) {
					if (j >= blockPos2.getZ() && j <= blockPos3.getZ() && k >= blockPos2.getX() && k <= blockPos3.getX()) {
						mutable.setPosition(k, mutable.getY(), j);
						if (blockPos.getX() == k && blockPos.getZ() == j) {
							world.setBlockState(mutable, Blocks.COBBLESTONE.getDefaultState(), 2);
						} else {
							world.setBlockState(mutable, Blocks.STONE.getDefaultState(), 2);
						}
					}
				}
			}
		}
	}
}
