package net.minecraft.world.gen.feature;

import java.util.Random;
import net.minecraft.block.PlantBlock;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class MushroomFeature extends Feature {
	private final PlantBlock block;

	public MushroomFeature(PlantBlock plantBlock) {
		this.block = plantBlock;
	}

	@Override
	public boolean generate(World world, Random random, BlockPos blockPos) {
		for (int i = 0; i < 64; i++) {
			BlockPos blockPos2 = blockPos.add(random.nextInt(8) - random.nextInt(8), random.nextInt(4) - random.nextInt(4), random.nextInt(8) - random.nextInt(8));
			if (world.isAir(blockPos2)
				&& (!world.dimension.hasNoSkylight() || blockPos2.getY() < 255)
				&& this.block.canPlantAt(world, blockPos2, this.block.getDefaultState())) {
				world.setBlockState(blockPos2, this.block.getDefaultState(), 2);
			}
		}

		return true;
	}
}
