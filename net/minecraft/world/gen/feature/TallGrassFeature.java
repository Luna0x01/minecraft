package net.minecraft.world.gen.feature;

import java.util.Random;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.TallPlantBlock;
import net.minecraft.block.material.Material;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class TallGrassFeature extends Feature {
	private final BlockState state;

	public TallGrassFeature(TallPlantBlock.GrassType grassType) {
		this.state = Blocks.TALLGRASS.getDefaultState().with(TallPlantBlock.TYPE, grassType);
	}

	@Override
	public boolean generate(World world, Random random, BlockPos blockPos) {
		for (BlockState blockState = world.getBlockState(blockPos);
			(blockState.getMaterial() == Material.AIR || blockState.getMaterial() == Material.FOLIAGE) && blockPos.getY() > 0;
			blockState = world.getBlockState(blockPos)
		) {
			blockPos = blockPos.down();
		}

		for (int i = 0; i < 128; i++) {
			BlockPos blockPos2 = blockPos.add(random.nextInt(8) - random.nextInt(8), random.nextInt(4) - random.nextInt(4), random.nextInt(8) - random.nextInt(8));
			if (world.isAir(blockPos2) && Blocks.TALLGRASS.canPlantAt(world, blockPos2, this.state)) {
				world.setBlockState(blockPos2, this.state, 2);
			}
		}

		return true;
	}
}
