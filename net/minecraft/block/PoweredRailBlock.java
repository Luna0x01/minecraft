package net.minecraft.block;

import net.minecraft.block.enums.RailShape;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.state.property.Property;
import net.minecraft.states.property.Properties;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class PoweredRailBlock extends AbstractRailBlock {
	public static final EnumProperty<RailShape> field_18429 = Properties.STRAIGHT_RAIL_SHAPE;
	public static final BooleanProperty field_18430 = Properties.POWERED;

	protected PoweredRailBlock(Block.Builder builder) {
		super(true, builder);
		this.setDefaultState(this.stateManager.method_16923().withProperty(field_18429, RailShape.NORTH_SOUTH).withProperty(field_18430, Boolean.valueOf(false)));
	}

	protected boolean isPoweredByOtherRails(World world, BlockPos pos, BlockState state, boolean checkSouthOrWest, int distance) {
		if (distance >= 8) {
			return false;
		} else {
			int i = pos.getX();
			int j = pos.getY();
			int k = pos.getZ();
			boolean bl = true;
			RailShape railShape = state.getProperty(field_18429);
			switch (railShape) {
				case NORTH_SOUTH:
					if (checkSouthOrWest) {
						k++;
					} else {
						k--;
					}
					break;
				case EAST_WEST:
					if (checkSouthOrWest) {
						i--;
					} else {
						i++;
					}
					break;
				case ASCENDING_EAST:
					if (checkSouthOrWest) {
						i--;
					} else {
						i++;
						j++;
						bl = false;
					}

					railShape = RailShape.EAST_WEST;
					break;
				case ASCENDING_WEST:
					if (checkSouthOrWest) {
						i--;
						j++;
						bl = false;
					} else {
						i++;
					}

					railShape = RailShape.EAST_WEST;
					break;
				case ASCENDING_NORTH:
					if (checkSouthOrWest) {
						k++;
					} else {
						k--;
						j++;
						bl = false;
					}

					railShape = RailShape.NORTH_SOUTH;
					break;
				case ASCENDING_SOUTH:
					if (checkSouthOrWest) {
						k++;
						j++;
						bl = false;
					} else {
						k--;
					}

					railShape = RailShape.NORTH_SOUTH;
			}

			return this.method_8855(world, new BlockPos(i, j, k), checkSouthOrWest, distance, railShape)
				? true
				: bl && this.method_8855(world, new BlockPos(i, j - 1, k), checkSouthOrWest, distance, railShape);
		}
	}

	protected boolean method_8855(World world, BlockPos blockPos, boolean bl, int i, RailShape railShape) {
		BlockState blockState = world.getBlockState(blockPos);
		if (blockState.getBlock() != this) {
			return false;
		} else {
			RailShape railShape2 = blockState.getProperty(field_18429);
			if (railShape != RailShape.EAST_WEST
				|| railShape2 != RailShape.NORTH_SOUTH && railShape2 != RailShape.ASCENDING_NORTH && railShape2 != RailShape.ASCENDING_SOUTH) {
				if (railShape != RailShape.NORTH_SOUTH
					|| railShape2 != RailShape.EAST_WEST && railShape2 != RailShape.ASCENDING_EAST && railShape2 != RailShape.ASCENDING_WEST) {
					if (!(Boolean)blockState.getProperty(field_18430)) {
						return false;
					} else {
						return world.isReceivingRedstonePower(blockPos) ? true : this.isPoweredByOtherRails(world, blockPos, blockState, bl, i + 1);
					}
				} else {
					return false;
				}
			} else {
				return false;
			}
		}
	}

	@Override
	protected void updateBlockState(BlockState state, World world, BlockPos pos, Block neighbor) {
		boolean bl = (Boolean)state.getProperty(field_18430);
		boolean bl2 = world.isReceivingRedstonePower(pos)
			|| this.isPoweredByOtherRails(world, pos, state, true, 0)
			|| this.isPoweredByOtherRails(world, pos, state, false, 0);
		if (bl2 != bl) {
			world.setBlockState(pos, state.withProperty(field_18430, Boolean.valueOf(bl2)), 3);
			world.updateNeighborsAlways(pos.down(), this);
			if (((RailShape)state.getProperty(field_18429)).isAscending()) {
				world.updateNeighborsAlways(pos.up(), this);
			}
		}
	}

	@Override
	public Property<RailShape> getShapeProperty() {
		return field_18429;
	}

	@Override
	public BlockState withRotation(BlockState state, BlockRotation rotation) {
		switch (rotation) {
			case CLOCKWISE_180:
				switch ((RailShape)state.getProperty(field_18429)) {
					case ASCENDING_EAST:
						return state.withProperty(field_18429, RailShape.ASCENDING_WEST);
					case ASCENDING_WEST:
						return state.withProperty(field_18429, RailShape.ASCENDING_EAST);
					case ASCENDING_NORTH:
						return state.withProperty(field_18429, RailShape.ASCENDING_SOUTH);
					case ASCENDING_SOUTH:
						return state.withProperty(field_18429, RailShape.ASCENDING_NORTH);
					case SOUTH_EAST:
						return state.withProperty(field_18429, RailShape.NORTH_WEST);
					case SOUTH_WEST:
						return state.withProperty(field_18429, RailShape.NORTH_EAST);
					case NORTH_WEST:
						return state.withProperty(field_18429, RailShape.SOUTH_EAST);
					case NORTH_EAST:
						return state.withProperty(field_18429, RailShape.SOUTH_WEST);
				}
			case COUNTERCLOCKWISE_90:
				switch ((RailShape)state.getProperty(field_18429)) {
					case NORTH_SOUTH:
						return state.withProperty(field_18429, RailShape.EAST_WEST);
					case EAST_WEST:
						return state.withProperty(field_18429, RailShape.NORTH_SOUTH);
					case ASCENDING_EAST:
						return state.withProperty(field_18429, RailShape.ASCENDING_NORTH);
					case ASCENDING_WEST:
						return state.withProperty(field_18429, RailShape.ASCENDING_SOUTH);
					case ASCENDING_NORTH:
						return state.withProperty(field_18429, RailShape.ASCENDING_WEST);
					case ASCENDING_SOUTH:
						return state.withProperty(field_18429, RailShape.ASCENDING_EAST);
					case SOUTH_EAST:
						return state.withProperty(field_18429, RailShape.NORTH_EAST);
					case SOUTH_WEST:
						return state.withProperty(field_18429, RailShape.SOUTH_EAST);
					case NORTH_WEST:
						return state.withProperty(field_18429, RailShape.SOUTH_WEST);
					case NORTH_EAST:
						return state.withProperty(field_18429, RailShape.NORTH_WEST);
				}
			case CLOCKWISE_90:
				switch ((RailShape)state.getProperty(field_18429)) {
					case NORTH_SOUTH:
						return state.withProperty(field_18429, RailShape.EAST_WEST);
					case EAST_WEST:
						return state.withProperty(field_18429, RailShape.NORTH_SOUTH);
					case ASCENDING_EAST:
						return state.withProperty(field_18429, RailShape.ASCENDING_SOUTH);
					case ASCENDING_WEST:
						return state.withProperty(field_18429, RailShape.ASCENDING_NORTH);
					case ASCENDING_NORTH:
						return state.withProperty(field_18429, RailShape.ASCENDING_EAST);
					case ASCENDING_SOUTH:
						return state.withProperty(field_18429, RailShape.ASCENDING_WEST);
					case SOUTH_EAST:
						return state.withProperty(field_18429, RailShape.SOUTH_WEST);
					case SOUTH_WEST:
						return state.withProperty(field_18429, RailShape.NORTH_WEST);
					case NORTH_WEST:
						return state.withProperty(field_18429, RailShape.NORTH_EAST);
					case NORTH_EAST:
						return state.withProperty(field_18429, RailShape.SOUTH_EAST);
				}
			default:
				return state;
		}
	}

	@Override
	public BlockState withMirror(BlockState state, BlockMirror mirror) {
		RailShape railShape = state.getProperty(field_18429);
		switch (mirror) {
			case LEFT_RIGHT:
				switch (railShape) {
					case ASCENDING_NORTH:
						return state.withProperty(field_18429, RailShape.ASCENDING_SOUTH);
					case ASCENDING_SOUTH:
						return state.withProperty(field_18429, RailShape.ASCENDING_NORTH);
					case SOUTH_EAST:
						return state.withProperty(field_18429, RailShape.NORTH_EAST);
					case SOUTH_WEST:
						return state.withProperty(field_18429, RailShape.NORTH_WEST);
					case NORTH_WEST:
						return state.withProperty(field_18429, RailShape.SOUTH_WEST);
					case NORTH_EAST:
						return state.withProperty(field_18429, RailShape.SOUTH_EAST);
					default:
						return super.withMirror(state, mirror);
				}
			case FRONT_BACK:
				switch (railShape) {
					case ASCENDING_EAST:
						return state.withProperty(field_18429, RailShape.ASCENDING_WEST);
					case ASCENDING_WEST:
						return state.withProperty(field_18429, RailShape.ASCENDING_EAST);
					case ASCENDING_NORTH:
					case ASCENDING_SOUTH:
					default:
						break;
					case SOUTH_EAST:
						return state.withProperty(field_18429, RailShape.SOUTH_WEST);
					case SOUTH_WEST:
						return state.withProperty(field_18429, RailShape.SOUTH_EAST);
					case NORTH_WEST:
						return state.withProperty(field_18429, RailShape.NORTH_EAST);
					case NORTH_EAST:
						return state.withProperty(field_18429, RailShape.NORTH_WEST);
				}
		}

		return super.withMirror(state, mirror);
	}

	@Override
	protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
		builder.method_16928(field_18429, field_18430);
	}
}
