package net.minecraft.world.gen.feature;

import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class IceDiskFeature extends Feature {
	private Block iceBlock = Blocks.PACKED_ICE;
	private int width;

	public IceDiskFeature(int i) {
		this.width = i;
	}

	@Override
	public boolean generate(World world, Random random, BlockPos blockPos) {
		while (world.isAir(blockPos) && blockPos.getY() > 2) {
			blockPos = blockPos.down();
		}

		if (world.getBlockState(blockPos).getBlock() != Blocks.SNOW) {
			return false;
		} else {
			int i = random.nextInt(this.width - 2) + 2;
			int j = 1;

			for (int k = blockPos.getX() - i; k <= blockPos.getX() + i; k++) {
				for (int l = blockPos.getZ() - i; l <= blockPos.getZ() + i; l++) {
					int m = k - blockPos.getX();
					int n = l - blockPos.getZ();
					if (m * m + n * n <= i * i) {
						for (int o = blockPos.getY() - j; o <= blockPos.getY() + j; o++) {
							BlockPos blockPos2 = new BlockPos(k, o, l);
							Block block = world.getBlockState(blockPos2).getBlock();
							if (block == Blocks.DIRT || block == Blocks.SNOW || block == Blocks.ICE) {
								world.setBlockState(blockPos2, this.iceBlock.getDefaultState(), 2);
							}
						}
					}
				}
			}

			return true;
		}
	}
}
