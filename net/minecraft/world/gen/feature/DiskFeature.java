package net.minecraft.world.gen.feature;

import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.material.Material;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class DiskFeature extends Feature {
	private Block block;
	private int radius;

	public DiskFeature(Block block, int i) {
		this.block = block;
		this.radius = i;
	}

	@Override
	public boolean generate(World world, Random random, BlockPos blockPos) {
		if (world.getBlockState(blockPos).getMaterial() != Material.WATER) {
			return false;
		} else {
			int i = random.nextInt(this.radius - 2) + 2;
			int j = 2;

			for (int k = blockPos.getX() - i; k <= blockPos.getX() + i; k++) {
				for (int l = blockPos.getZ() - i; l <= blockPos.getZ() + i; l++) {
					int m = k - blockPos.getX();
					int n = l - blockPos.getZ();
					if (m * m + n * n <= i * i) {
						for (int o = blockPos.getY() - j; o <= blockPos.getY() + j; o++) {
							BlockPos blockPos2 = new BlockPos(k, o, l);
							Block block = world.getBlockState(blockPos2).getBlock();
							if (block == Blocks.DIRT || block == Blocks.GRASS) {
								world.setBlockState(blockPos2, this.block.getDefaultState(), 2);
							}
						}
					}
				}
			}

			return true;
		}
	}
}
