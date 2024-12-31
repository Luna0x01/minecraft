package net.minecraft.block;

import java.util.Random;
import net.minecraft.class_3726;
import net.minecraft.class_4342;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class MyceliumBlock extends class_3726 {
	public MyceliumBlock(Block.Builder builder) {
		super(builder);
	}

	@Override
	public void randomDisplayTick(BlockState state, World world, BlockPos pos, Random random) {
		super.randomDisplayTick(state, world, pos, random);
		if (random.nextInt(10) == 0) {
			world.method_16343(
				class_4342.field_21358,
				(double)((float)pos.getX() + random.nextFloat()),
				(double)((float)pos.getY() + 1.1F),
				(double)((float)pos.getZ() + random.nextFloat()),
				0.0,
				0.0,
				0.0
			);
		}
	}
}
