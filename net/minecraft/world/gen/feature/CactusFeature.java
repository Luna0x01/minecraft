package net.minecraft.world.gen.feature;

import java.util.Random;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class CactusFeature extends Feature {
	@Override
	public boolean generate(World world, Random random, BlockPos blockPos) {
		for (int i = 0; i < 10; i++) {
			BlockPos blockPos2 = blockPos.add(random.nextInt(8) - random.nextInt(8), random.nextInt(4) - random.nextInt(4), random.nextInt(8) - random.nextInt(8));
			if (world.isAir(blockPos2)) {
				int j = 1 + random.nextInt(random.nextInt(3) + 1);

				for (int k = 0; k < j; k++) {
					if (Blocks.CACTUS.canPlaceCactusAt(world, blockPos2)) {
						world.setBlockState(blockPos2.up(k), Blocks.CACTUS.getDefaultState(), 2);
					}
				}
			}
		}

		return true;
	}
}
