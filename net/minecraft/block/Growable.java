package net.minecraft.block;

import java.util.Random;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public interface Growable {
	boolean canGrow(World world, BlockPos pos, BlockState state, boolean bl);

	boolean canBeFertilized(World world, Random random, BlockPos pos, BlockState state);

	void grow(World world, Random random, BlockPos pos, BlockState state);
}
