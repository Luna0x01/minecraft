package net.minecraft.world.gen.feature;

import java.util.Random;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class EndGatewayFeature extends Feature {
	@Override
	public boolean generate(World world, Random random, BlockPos blockPos) {
		for (BlockPos.Mutable mutable : BlockPos.mutableIterate(blockPos.add(-1, -2, -1), blockPos.add(1, 2, 1))) {
			boolean bl = mutable.getX() == blockPos.getX();
			boolean bl2 = mutable.getY() == blockPos.getY();
			boolean bl3 = mutable.getZ() == blockPos.getZ();
			boolean bl4 = Math.abs(mutable.getY() - blockPos.getY()) == 2;
			if (bl && bl2 && bl3) {
				this.setBlockStateWithoutUpdatingNeighbors(world, new BlockPos(mutable), Blocks.END_GATEWAY.getDefaultState());
			} else if (bl2) {
				this.setBlockStateWithoutUpdatingNeighbors(world, mutable, Blocks.AIR.getDefaultState());
			} else if (bl4 && bl && bl3) {
				this.setBlockStateWithoutUpdatingNeighbors(world, mutable, Blocks.BEDROCK.getDefaultState());
			} else if ((bl || bl3) && !bl4) {
				this.setBlockStateWithoutUpdatingNeighbors(world, mutable, Blocks.BEDROCK.getDefaultState());
			} else {
				this.setBlockStateWithoutUpdatingNeighbors(world, mutable, Blocks.AIR.getDefaultState());
			}
		}

		return true;
	}
}
