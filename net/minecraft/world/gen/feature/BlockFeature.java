package net.minecraft.world.gen.feature;

import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class BlockFeature extends Feature {
	private final Block block;
	private final int radius;

	public BlockFeature(Block block, int i) {
		super(false);
		this.block = block;
		this.radius = i;
	}

	@Override
	public boolean generate(World world, Random random, BlockPos blockPos) {
		while (blockPos.getY() > 3) {
			if (!world.isAir(blockPos.down())) {
				Block block = world.getBlockState(blockPos.down()).getBlock();
				if (block == Blocks.GRASS || block == Blocks.DIRT || block == Blocks.STONE) {
					break;
				}
			}

			blockPos = blockPos.down();
		}

		if (blockPos.getY() <= 3) {
			return false;
		} else {
			int i = this.radius;

			for (int j = 0; i >= 0 && j < 3; j++) {
				int k = i + random.nextInt(2);
				int l = i + random.nextInt(2);
				int m = i + random.nextInt(2);
				float f = (float)(k + l + m) * 0.333F + 0.5F;

				for (BlockPos blockPos2 : BlockPos.iterate(blockPos.add(-k, -l, -m), blockPos.add(k, l, m))) {
					if (blockPos2.getSquaredDistance(blockPos) <= (double)(f * f)) {
						world.setBlockState(blockPos2, this.block.getDefaultState(), 4);
					}
				}

				blockPos = blockPos.add(-(i + 1) + random.nextInt(2 + i * 2), 0 - random.nextInt(2), -(i + 1) + random.nextInt(2 + i * 2));
			}

			return true;
		}
	}
}
