package net.minecraft.block;

import com.google.common.base.Predicate;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.state.property.Property;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class PoweredRailBlock extends AbstractRailBlock {
	public static final EnumProperty<AbstractRailBlock.RailShapeType> SHAPE = EnumProperty.of(
		"shape",
		AbstractRailBlock.RailShapeType.class,
		new Predicate<AbstractRailBlock.RailShapeType>() {
			public boolean apply(AbstractRailBlock.RailShapeType railShapeType) {
				return railShapeType != AbstractRailBlock.RailShapeType.NORTH_EAST
					&& railShapeType != AbstractRailBlock.RailShapeType.NORTH_WEST
					&& railShapeType != AbstractRailBlock.RailShapeType.SOUTH_EAST
					&& railShapeType != AbstractRailBlock.RailShapeType.SOUTH_WEST;
			}
		}
	);
	public static final BooleanProperty POWERED = BooleanProperty.of("powered");

	protected PoweredRailBlock() {
		super(true);
		this.setDefaultState(this.stateManager.getDefaultState().with(SHAPE, AbstractRailBlock.RailShapeType.NORTH_SOUTH).with(POWERED, false));
	}

	protected boolean isPoweredByOtherRails(World world, BlockPos pos, BlockState state, boolean checkSouthOrWest, int distance) {
		if (distance >= 8) {
			return false;
		} else {
			int i = pos.getX();
			int j = pos.getY();
			int k = pos.getZ();
			boolean bl = true;
			AbstractRailBlock.RailShapeType railShapeType = state.get(SHAPE);
			switch (railShapeType) {
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

					railShapeType = AbstractRailBlock.RailShapeType.EAST_WEST;
					break;
				case ASCENDING_WEST:
					if (checkSouthOrWest) {
						i--;
						j++;
						bl = false;
					} else {
						i++;
					}

					railShapeType = AbstractRailBlock.RailShapeType.EAST_WEST;
					break;
				case ASCENDING_NORTH:
					if (checkSouthOrWest) {
						k++;
					} else {
						k--;
						j++;
						bl = false;
					}

					railShapeType = AbstractRailBlock.RailShapeType.NORTH_SOUTH;
					break;
				case ASCENDING_SOUTH:
					if (checkSouthOrWest) {
						k++;
						j++;
						bl = false;
					} else {
						k--;
					}

					railShapeType = AbstractRailBlock.RailShapeType.NORTH_SOUTH;
			}

			return this.canBePowered(world, new BlockPos(i, j, k), checkSouthOrWest, distance, railShapeType)
				? true
				: bl && this.canBePowered(world, new BlockPos(i, j - 1, k), checkSouthOrWest, distance, railShapeType);
		}
	}

	protected boolean canBePowered(World world, BlockPos pos, boolean checkSouthOrWest, int distance, AbstractRailBlock.RailShapeType type) {
		BlockState blockState = world.getBlockState(pos);
		if (blockState.getBlock() != this) {
			return false;
		} else {
			AbstractRailBlock.RailShapeType railShapeType = blockState.get(SHAPE);
			if (type != AbstractRailBlock.RailShapeType.EAST_WEST
				|| railShapeType != AbstractRailBlock.RailShapeType.NORTH_SOUTH
					&& railShapeType != AbstractRailBlock.RailShapeType.ASCENDING_NORTH
					&& railShapeType != AbstractRailBlock.RailShapeType.ASCENDING_SOUTH) {
				if (type != AbstractRailBlock.RailShapeType.NORTH_SOUTH
					|| railShapeType != AbstractRailBlock.RailShapeType.EAST_WEST
						&& railShapeType != AbstractRailBlock.RailShapeType.ASCENDING_EAST
						&& railShapeType != AbstractRailBlock.RailShapeType.ASCENDING_WEST) {
					if (!(Boolean)blockState.get(POWERED)) {
						return false;
					} else {
						return world.isReceivingRedstonePower(pos) ? true : this.isPoweredByOtherRails(world, pos, blockState, checkSouthOrWest, distance + 1);
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
	protected void updateBlockState(World world, BlockPos pos, BlockState state, Block block) {
		boolean bl = (Boolean)state.get(POWERED);
		boolean bl2 = world.isReceivingRedstonePower(pos)
			|| this.isPoweredByOtherRails(world, pos, state, true, 0)
			|| this.isPoweredByOtherRails(world, pos, state, false, 0);
		if (bl2 != bl) {
			world.setBlockState(pos, state.with(POWERED, bl2), 3);
			world.updateNeighborsAlways(pos.down(), this);
			if (((AbstractRailBlock.RailShapeType)state.get(SHAPE)).isAscending()) {
				world.updateNeighborsAlways(pos.up(), this);
			}
		}
	}

	@Override
	public Property<AbstractRailBlock.RailShapeType> getShapeProperty() {
		return SHAPE;
	}

	@Override
	public BlockState stateFromData(int data) {
		return this.getDefaultState().with(SHAPE, AbstractRailBlock.RailShapeType.getById(data & 7)).with(POWERED, (data & 8) > 0);
	}

	@Override
	public int getData(BlockState state) {
		int i = 0;
		i |= ((AbstractRailBlock.RailShapeType)state.get(SHAPE)).getData();
		if ((Boolean)state.get(POWERED)) {
			i |= 8;
		}

		return i;
	}

	@Override
	protected StateManager appendProperties() {
		return new StateManager(this, SHAPE, POWERED);
	}
}
