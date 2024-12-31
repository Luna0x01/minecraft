package net.minecraft.block;

import com.google.common.collect.Lists;
import java.util.Queue;
import net.minecraft.class_3710;
import net.minecraft.block.material.Material;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.tag.FluidTags;
import net.minecraft.util.Pair;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

public class SpongeBlock extends Block {
	protected SpongeBlock(Block.Builder builder) {
		super(builder);
	}

	@Override
	public void onBlockAdded(BlockState state, World world, BlockPos pos, BlockState oldState) {
		if (oldState.getBlock() != state.getBlock()) {
			this.method_16736(world, pos);
		}
	}

	@Override
	public void neighborUpdate(BlockState state, World world, BlockPos pos, Block block, BlockPos neighborPos) {
		this.method_16736(world, pos);
		super.neighborUpdate(state, world, pos, block, neighborPos);
	}

	protected void method_16736(World world, BlockPos blockPos) {
		if (this.absorbWater(world, blockPos)) {
			world.setBlockState(blockPos, Blocks.WET_SPONGE.getDefaultState(), 2);
			world.syncGlobalEvent(2001, blockPos, Block.getRawIdFromState(Blocks.WATER.getDefaultState()));
		}
	}

	private boolean absorbWater(World world, BlockPos pos) {
		Queue<Pair<BlockPos, Integer>> queue = Lists.newLinkedList();
		queue.add(new Pair<>(pos, 0));
		int i = 0;

		while (!queue.isEmpty()) {
			Pair<BlockPos, Integer> pair = (Pair<BlockPos, Integer>)queue.poll();
			BlockPos blockPos = pair.getLeft();
			int j = pair.getRight();

			for (Direction direction : Direction.values()) {
				BlockPos blockPos2 = blockPos.offset(direction);
				BlockState blockState = world.getBlockState(blockPos2);
				FluidState fluidState = world.getFluidState(blockPos2);
				Material material = blockState.getMaterial();
				if (fluidState.matches(FluidTags.WATER)) {
					if (blockState.getBlock() instanceof FluidDrainable && ((FluidDrainable)blockState.getBlock()).tryDrainFluid(world, blockPos2, blockState) != Fluids.EMPTY
						)
					 {
						i++;
						if (j < 6) {
							queue.add(new Pair<>(blockPos2, j + 1));
						}
					} else if (blockState.getBlock() instanceof class_3710) {
						world.setBlockState(blockPos2, Blocks.AIR.getDefaultState(), 3);
						i++;
						if (j < 6) {
							queue.add(new Pair<>(blockPos2, j + 1));
						}
					} else if (material == Material.field_19498 || material == Material.field_19499) {
						blockState.method_16867(world, blockPos2, 0);
						world.setBlockState(blockPos2, Blocks.AIR.getDefaultState(), 3);
						i++;
						if (j < 6) {
							queue.add(new Pair<>(blockPos2, j + 1));
						}
					}
				}
			}

			if (i > 64) {
				break;
			}
		}

		return i > 0;
	}
}
