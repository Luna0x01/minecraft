package net.minecraft.block;

import java.util.EnumSet;
import java.util.Random;
import java.util.Set;
import net.minecraft.block.material.Material;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

public class FlowingFluidBlock extends AbstractFluidBlock {
	int neighborSourceBlocks;

	protected FlowingFluidBlock(Material material) {
		super(material);
	}

	private void setFluidBlock(World world, BlockPos pos, BlockState state) {
		world.setBlockState(pos, getFluidByMaterial(this.material).getDefaultState().with(LEVEL, state.get(LEVEL)), 2);
	}

	@Override
	public void onScheduledTick(World world, BlockPos pos, BlockState state, Random rand) {
		int i = (Integer)state.get(LEVEL);
		int j = 1;
		if (this.material == Material.LAVA && !world.dimension.doesWaterVaporize()) {
			j = 2;
		}

		int k = this.getTickRate(world);
		if (i > 0) {
			int l = -100;
			this.neighborSourceBlocks = 0;

			for (Direction direction : Direction.DirectionType.HORIZONTAL) {
				l = this.getFluidLevelFromNeighbor(world, pos.offset(direction), l);
			}

			int m = l + j;
			if (m >= 8 || l < 0) {
				m = -1;
			}

			int n = this.method_11620(world.getBlockState(pos.up()));
			if (n >= 0) {
				if (n >= 8) {
					m = n;
				} else {
					m = n + 8;
				}
			}

			if (this.neighborSourceBlocks >= 2 && this.material == Material.WATER) {
				BlockState blockState = world.getBlockState(pos.down());
				if (blockState.getMaterial().isSolid()) {
					m = 0;
				} else if (blockState.getMaterial() == this.material && (Integer)blockState.get(LEVEL) == 0) {
					m = 0;
				}
			}

			if (this.material == Material.LAVA && i < 8 && m < 8 && m > i && rand.nextInt(4) != 0) {
				k *= 4;
			}

			if (m == i) {
				this.setFluidBlock(world, pos, state);
			} else {
				i = m;
				if (m < 0) {
					world.setAir(pos);
				} else {
					state = state.with(LEVEL, m);
					world.setBlockState(pos, state, 2);
					world.createAndScheduleBlockTick(pos, this, k);
					world.updateNeighborsAlways(pos, this);
				}
			}
		} else {
			this.setFluidBlock(world, pos, state);
		}

		BlockState blockState2 = world.getBlockState(pos.down());
		if (this.canFlowTo(world, pos.down(), blockState2)) {
			if (this.material == Material.LAVA && world.getBlockState(pos.down()).getMaterial() == Material.WATER) {
				world.setBlockState(pos.down(), Blocks.STONE.getDefaultState());
				this.method_11619(world, pos.down());
				return;
			}

			if (i >= 8) {
				this.flowTo(world, pos.down(), blockState2, i);
			} else {
				this.flowTo(world, pos.down(), blockState2, i + 8);
			}
		} else if (i >= 0 && (i == 0 || this.blocksFluidFlow(world, pos.down(), blockState2))) {
			Set<Direction> set = this.getFlowDirections(world, pos);
			int o = i + j;
			if (i >= 8) {
				o = 1;
			}

			if (o >= 8) {
				return;
			}

			for (Direction direction2 : set) {
				this.flowTo(world, pos.offset(direction2), world.getBlockState(pos.offset(direction2)), o);
			}
		}
	}

	private void flowTo(World world, BlockPos pos, BlockState state, int level) {
		if (this.canFlowTo(world, pos, state)) {
			if (state.getMaterial() != Material.AIR) {
				if (this.material == Material.LAVA) {
					this.method_11619(world, pos);
				} else {
					state.getBlock().dropAsItem(world, pos, state, 0);
				}
			}

			world.setBlockState(pos, this.getDefaultState().with(LEVEL, level), 3);
		}
	}

	private int getFlowDistance(World world, BlockPos pos, int distance, Direction dir) {
		int i = 1000;

		for (Direction direction : Direction.DirectionType.HORIZONTAL) {
			if (direction != dir) {
				BlockPos blockPos = pos.offset(direction);
				BlockState blockState = world.getBlockState(blockPos);
				if (!this.blocksFluidFlow(world, blockPos, blockState) && (blockState.getMaterial() != this.material || (Integer)blockState.get(LEVEL) > 0)) {
					if (!this.blocksFluidFlow(world, blockPos.down(), blockState)) {
						return distance;
					}

					if (distance < this.method_11607(world)) {
						int j = this.getFlowDistance(world, blockPos, distance + 1, direction.getOpposite());
						if (j < i) {
							i = j;
						}
					}
				}
			}
		}

		return i;
	}

	private int method_11607(World world) {
		return this.material == Material.LAVA && !world.dimension.doesWaterVaporize() ? 2 : 4;
	}

	private Set<Direction> getFlowDirections(World world, BlockPos pos) {
		int i = 1000;
		Set<Direction> set = EnumSet.noneOf(Direction.class);

		for (Direction direction : Direction.DirectionType.HORIZONTAL) {
			BlockPos blockPos = pos.offset(direction);
			BlockState blockState = world.getBlockState(blockPos);
			if (!this.blocksFluidFlow(world, blockPos, blockState) && (blockState.getMaterial() != this.material || (Integer)blockState.get(LEVEL) > 0)) {
				int j;
				if (this.blocksFluidFlow(world, blockPos.down(), world.getBlockState(blockPos.down()))) {
					j = this.getFlowDistance(world, blockPos, 1, direction.getOpposite());
				} else {
					j = 0;
				}

				if (j < i) {
					set.clear();
				}

				if (j <= i) {
					set.add(direction);
					i = j;
				}
			}
		}

		return set;
	}

	private boolean blocksFluidFlow(World world, BlockPos pos, BlockState state) {
		Block block = world.getBlockState(pos).getBlock();
		if (block instanceof DoorBlock || block == Blocks.STANDING_SIGN || block == Blocks.LADDER || block == Blocks.SUGARCANE) {
			return true;
		} else {
			return block.material != Material.PORTAL && block.material != Material.CAVE_AIR ? block.material.blocksMovement() : true;
		}
	}

	protected int getFluidLevelFromNeighbor(World world, BlockPos pos, int baseFluidLevel) {
		int i = this.method_11620(world.getBlockState(pos));
		if (i < 0) {
			return baseFluidLevel;
		} else {
			if (i == 0) {
				this.neighborSourceBlocks++;
			}

			if (i >= 8) {
				i = 0;
			}

			return baseFluidLevel >= 0 && i >= baseFluidLevel ? baseFluidLevel : i;
		}
	}

	private boolean canFlowTo(World world, BlockPos pos, BlockState state) {
		Material material = state.getMaterial();
		return material != this.material && material != Material.LAVA && !this.blocksFluidFlow(world, pos, state);
	}

	@Override
	public void onCreation(World world, BlockPos pos, BlockState state) {
		if (!this.canChangeFromLava(world, pos, state)) {
			world.createAndScheduleBlockTick(pos, this, this.getTickRate(world));
		}
	}
}
