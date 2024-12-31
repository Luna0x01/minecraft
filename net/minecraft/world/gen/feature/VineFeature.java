package net.minecraft.world.gen.feature;

import java.util.Random;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.VineBlock;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

public class VineFeature extends Feature {
	@Override
	public boolean generate(World world, Random random, BlockPos blockPos) {
		while (blockPos.getY() < 128) {
			if (world.isAir(blockPos)) {
				for (Direction direction : Direction.DirectionType.HORIZONTAL.getDirections()) {
					if (Blocks.VINE.canBePlacedAdjacent(world, blockPos, direction)) {
						BlockState blockState = Blocks.VINE
							.getDefaultState()
							.with(VineBlock.NORTH, direction == Direction.NORTH)
							.with(VineBlock.EAST, direction == Direction.EAST)
							.with(VineBlock.SOUTH, direction == Direction.SOUTH)
							.with(VineBlock.WEST, direction == Direction.WEST);
						world.setBlockState(blockPos, blockState, 2);
						break;
					}
				}
			} else {
				blockPos = blockPos.add(random.nextInt(4) - random.nextInt(4), 0, random.nextInt(4) - random.nextInt(4));
			}

			blockPos = blockPos.up();
		}

		return true;
	}
}
