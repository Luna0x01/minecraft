package net.minecraft.world.gen.feature;

import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.material.Material;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class ClayFeature extends Feature {
	private Block clay = Blocks.CLAY;
	private int count;

	public ClayFeature(int i) {
		this.count = i;
	}

	@Override
	public boolean generate(World world, Random random, BlockPos blockPos) {
		if (world.getBlockState(blockPos).getMaterial() != Material.WATER) {
			return false;
		} else {
			int i = random.nextInt(this.count - 2) + 2;
			int j = 1;

			for (int k = blockPos.getX() - i; k <= blockPos.getX() + i; k++) {
				for (int l = blockPos.getZ() - i; l <= blockPos.getZ() + i; l++) {
					int m = k - blockPos.getX();
					int n = l - blockPos.getZ();
					if (m * m + n * n <= i * i) {
						for (int o = blockPos.getY() - j; o <= blockPos.getY() + j; o++) {
							BlockPos blockPos2 = new BlockPos(k, o, l);
							Block block = world.getBlockState(blockPos2).getBlock();
							if (block == Blocks.DIRT || block == Blocks.CLAY) {
								world.setBlockState(blockPos2, this.clay.getDefaultState(), 2);
							}
						}
					}
				}
			}

			return true;
		}
	}
}
