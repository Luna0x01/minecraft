package net.minecraft.world.gen.feature;

import java.util.Random;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public abstract class Feature {
	private final boolean emitNeighborBlockUpdates;

	public Feature() {
		this(false);
	}

	public Feature(boolean bl) {
		this.emitNeighborBlockUpdates = bl;
	}

	public abstract boolean generate(World world, Random random, BlockPos blockPos);

	public void setLeafRadius() {
	}

	protected void setBlockStateWithoutUpdatingNeighbors(World world, BlockPos pos, BlockState state) {
		if (this.emitNeighborBlockUpdates) {
			world.setBlockState(pos, state, 3);
		} else {
			world.setBlockState(pos, state, 2);
		}
	}
}
