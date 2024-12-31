package net.minecraft.block;

import java.util.Random;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class MyceliumBlock extends SpreadableBlock {
	public MyceliumBlock(Block.Settings settings) {
		super(settings);
	}

	@Override
	public void randomDisplayTick(BlockState blockState, World world, BlockPos blockPos, Random random) {
		super.randomDisplayTick(blockState, world, blockPos, random);
		if (random.nextInt(10) == 0) {
			world.addParticle(
				ParticleTypes.field_11219,
				(double)blockPos.getX() + (double)random.nextFloat(),
				(double)blockPos.getY() + 1.1,
				(double)blockPos.getZ() + (double)random.nextFloat(),
				0.0,
				0.0,
				0.0
			);
		}
	}
}
