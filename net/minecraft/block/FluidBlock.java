package net.minecraft.block;

import java.util.Random;
import net.minecraft.block.material.Material;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

public class FluidBlock extends AbstractFluidBlock {
	protected FluidBlock(Material material) {
		super(material);
		this.setTickRandomly(false);
		if (material == Material.LAVA) {
			this.setTickRandomly(true);
		}
	}

	@Override
	public void neighborUpdate(World world, BlockPos pos, BlockState state, Block block) {
		if (!this.canChangeFromLava(world, pos, state)) {
			this.updateLevel(world, pos, state);
		}
	}

	private void updateLevel(World world, BlockPos pos, BlockState state) {
		FlowingFluidBlock flowingFluidBlock = getFlowingFluidByMaterial(this.material);
		world.setBlockState(pos, flowingFluidBlock.getDefaultState().with(LEVEL, state.get(LEVEL)), 2);
		world.createAndScheduleBlockTick(pos, flowingFluidBlock, this.getTickRate(world));
	}

	@Override
	public void onScheduledTick(World world, BlockPos pos, BlockState state, Random rand) {
		if (this.material == Material.LAVA) {
			if (world.getGameRules().getBoolean("doFireTick")) {
				int i = rand.nextInt(3);
				if (i > 0) {
					BlockPos blockPos = pos;

					for (int j = 0; j < i; j++) {
						blockPos = blockPos.add(rand.nextInt(3) - 1, 1, rand.nextInt(3) - 1);
						Block block = world.getBlockState(blockPos).getBlock();
						if (block.material == Material.AIR) {
							if (this.isAdjacentBurnable(world, blockPos)) {
								world.setBlockState(blockPos, Blocks.FIRE.getDefaultState());
								return;
							}
						} else if (block.material.blocksMovement()) {
							return;
						}
					}
				} else {
					for (int k = 0; k < 3; k++) {
						BlockPos blockPos2 = pos.add(rand.nextInt(3) - 1, 0, rand.nextInt(3) - 1);
						if (world.isAir(blockPos2.up()) && this.isBurnable(world, blockPos2)) {
							world.setBlockState(blockPos2.up(), Blocks.FIRE.getDefaultState());
						}
					}
				}
			}
		}
	}

	protected boolean isAdjacentBurnable(World world, BlockPos pos) {
		for (Direction direction : Direction.values()) {
			if (this.isBurnable(world, pos.offset(direction))) {
				return true;
			}
		}

		return false;
	}

	private boolean isBurnable(World world, BlockPos pos) {
		return world.getBlockState(pos).getBlock().getMaterial().isBurnable();
	}
}
