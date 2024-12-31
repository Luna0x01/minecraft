package net.minecraft.world.gen.feature;

import java.util.Random;
import net.minecraft.block.BlockState;
import net.minecraft.block.FlowerBlock;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class FlowerPatchFeature extends Feature {
	private FlowerBlock block;
	private BlockState state;

	public FlowerPatchFeature(FlowerBlock flowerBlock, FlowerBlock.FlowerType flowerType) {
		this.setFlowers(flowerBlock, flowerType);
	}

	public void setFlowers(FlowerBlock block, FlowerBlock.FlowerType type) {
		this.block = block;
		this.state = block.getDefaultState().with(block.getFlowerProperties(), type);
	}

	@Override
	public boolean generate(World world, Random random, BlockPos blockPos) {
		for (int i = 0; i < 64; i++) {
			BlockPos blockPos2 = blockPos.add(random.nextInt(8) - random.nextInt(8), random.nextInt(4) - random.nextInt(4), random.nextInt(8) - random.nextInt(8));
			if (world.isAir(blockPos2) && (!world.dimension.hasNoSkylight() || blockPos2.getY() < 255) && this.block.canPlantAt(world, blockPos2, this.state)) {
				world.setBlockState(blockPos2, this.state, 2);
			}
		}

		return true;
	}
}
