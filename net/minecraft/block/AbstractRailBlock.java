package net.minecraft.block;

import com.google.common.collect.Lists;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.block.material.Material;
import net.minecraft.block.piston.PistonBehavior;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.item.itemgroup.ItemGroup;
import net.minecraft.state.property.Property;
import net.minecraft.util.StringIdentifiable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

public abstract class AbstractRailBlock extends Block {
	protected static final Box field_12566 = new Box(0.0, 0.0, 0.0, 1.0, 0.125, 1.0);
	protected static final Box field_15123 = new Box(0.0, 0.0, 0.0, 1.0, 0.5, 1.0);
	protected final boolean forbidCurves;

	public static boolean isRail(World world, BlockPos pos) {
		return isRail(world.getBlockState(pos));
	}

	public static boolean isRail(BlockState state) {
		Block block = state.getBlock();
		return block == Blocks.RAIL || block == Blocks.POWERED_RAIL || block == Blocks.DETECTOR_RAIL || block == Blocks.ACTIVATOR_RAIL;
	}

	protected AbstractRailBlock(boolean bl) {
		super(Material.DECORATION);
		this.forbidCurves = bl;
		this.setItemGroup(ItemGroup.TRANSPORTATION);
	}

	@Nullable
	@Override
	public Box method_8640(BlockState state, BlockView view, BlockPos pos) {
		return EMPTY_BOX;
	}

	@Override
	public boolean isFullBoundsCubeForCulling(BlockState blockState) {
		return false;
	}

	@Override
	public Box getCollisionBox(BlockState state, BlockView view, BlockPos pos) {
		AbstractRailBlock.RailShapeType railShapeType = state.getBlock() == this ? state.get(this.getShapeProperty()) : null;
		return railShapeType != null && railShapeType.isAscending() ? field_15123 : field_12566;
	}

	@Override
	public boolean method_11562(BlockState state) {
		return false;
	}

	@Override
	public boolean canBePlacedAtPos(World world, BlockPos pos) {
		return world.getBlockState(pos.down()).method_11739();
	}

	@Override
	public void onCreation(World world, BlockPos pos, BlockState state) {
		if (!world.isClient) {
			state = this.updateBlockState(world, pos, state, true);
			if (this.forbidCurves) {
				state.neighbourUpdate(world, pos, this, pos);
			}
		}
	}

	@Override
	public void neighborUpdate(BlockState state, World world, BlockPos pos, Block block, BlockPos neighborPos) {
		if (!world.isClient) {
			AbstractRailBlock.RailShapeType railShapeType = state.get(this.getShapeProperty());
			boolean bl = false;
			if (!world.getBlockState(pos.down()).method_11739()) {
				bl = true;
			}

			if (railShapeType == AbstractRailBlock.RailShapeType.ASCENDING_EAST && !world.getBlockState(pos.east()).method_11739()) {
				bl = true;
			} else if (railShapeType == AbstractRailBlock.RailShapeType.ASCENDING_WEST && !world.getBlockState(pos.west()).method_11739()) {
				bl = true;
			} else if (railShapeType == AbstractRailBlock.RailShapeType.ASCENDING_NORTH && !world.getBlockState(pos.north()).method_11739()) {
				bl = true;
			} else if (railShapeType == AbstractRailBlock.RailShapeType.ASCENDING_SOUTH && !world.getBlockState(pos.south()).method_11739()) {
				bl = true;
			}

			if (bl && !world.isAir(pos)) {
				this.dropAsItem(world, pos, state, 0);
				world.setAir(pos);
			} else {
				this.updateBlockState(state, world, pos, block);
			}
		}
	}

	protected void updateBlockState(BlockState state, World world, BlockPos pos, Block neighbor) {
	}

	protected BlockState updateBlockState(World world, BlockPos pos, BlockState state, boolean forceUpdate) {
		return world.isClient
			? state
			: new AbstractRailBlock.RailPlacementHelper(world, pos, state).getRealignedHelper(world.isReceivingRedstonePower(pos), forceUpdate).getBlockState();
	}

	@Override
	public PistonBehavior getPistonBehavior(BlockState state) {
		return PistonBehavior.NORMAL;
	}

	@Override
	public RenderLayer getRenderLayerType() {
		return RenderLayer.CUTOUT;
	}

	@Override
	public void onBreaking(World world, BlockPos pos, BlockState state) {
		super.onBreaking(world, pos, state);
		if (((AbstractRailBlock.RailShapeType)state.get(this.getShapeProperty())).isAscending()) {
			world.method_13692(pos.up(), this, false);
		}

		if (this.forbidCurves) {
			world.method_13692(pos, this, false);
			world.method_13692(pos.down(), this, false);
		}
	}

	public abstract Property<AbstractRailBlock.RailShapeType> getShapeProperty();

	public class RailPlacementHelper {
		private final World world;
		private final BlockPos pos;
		private final AbstractRailBlock block;
		private BlockState state;
		private final boolean allowSlopes;
		private final List<BlockPos> neighbors = Lists.newArrayList();

		public RailPlacementHelper(World world, BlockPos blockPos, BlockState blockState) {
			this.world = world;
			this.pos = blockPos;
			this.state = blockState;
			this.block = (AbstractRailBlock)blockState.getBlock();
			AbstractRailBlock.RailShapeType railShapeType = blockState.get(this.block.getShapeProperty());
			this.allowSlopes = this.block.forbidCurves;
			this.computeNeighbors(railShapeType);
		}

		public List<BlockPos> method_11551() {
			return this.neighbors;
		}

		private void computeNeighbors(AbstractRailBlock.RailShapeType shape) {
			this.neighbors.clear();
			switch (shape) {
				case NORTH_SOUTH:
					this.neighbors.add(this.pos.north());
					this.neighbors.add(this.pos.south());
					break;
				case EAST_WEST:
					this.neighbors.add(this.pos.west());
					this.neighbors.add(this.pos.east());
					break;
				case ASCENDING_EAST:
					this.neighbors.add(this.pos.west());
					this.neighbors.add(this.pos.east().up());
					break;
				case ASCENDING_WEST:
					this.neighbors.add(this.pos.west().up());
					this.neighbors.add(this.pos.east());
					break;
				case ASCENDING_NORTH:
					this.neighbors.add(this.pos.north().up());
					this.neighbors.add(this.pos.south());
					break;
				case ASCENDING_SOUTH:
					this.neighbors.add(this.pos.north());
					this.neighbors.add(this.pos.south().up());
					break;
				case SOUTH_EAST:
					this.neighbors.add(this.pos.east());
					this.neighbors.add(this.pos.south());
					break;
				case SOUTH_WEST:
					this.neighbors.add(this.pos.west());
					this.neighbors.add(this.pos.south());
					break;
				case NORTH_WEST:
					this.neighbors.add(this.pos.west());
					this.neighbors.add(this.pos.north());
					break;
				case NORTH_EAST:
					this.neighbors.add(this.pos.east());
					this.neighbors.add(this.pos.north());
			}
		}

		private void correctNeighbors() {
			for (int i = 0; i < this.neighbors.size(); i++) {
				AbstractRailBlock.RailPlacementHelper railPlacementHelper = this.getVerticalHelper((BlockPos)this.neighbors.get(i));
				if (railPlacementHelper != null && railPlacementHelper.isNeighbor(this)) {
					this.neighbors.set(i, railPlacementHelper.pos);
				} else {
					this.neighbors.remove(i--);
				}
			}
		}

		private boolean isVerticallyNearRail(BlockPos pos) {
			return AbstractRailBlock.isRail(this.world, pos) || AbstractRailBlock.isRail(this.world, pos.up()) || AbstractRailBlock.isRail(this.world, pos.down());
		}

		@Nullable
		private AbstractRailBlock.RailPlacementHelper getVerticalHelper(BlockPos pos) {
			BlockState blockState = this.world.getBlockState(pos);
			if (AbstractRailBlock.isRail(blockState)) {
				return AbstractRailBlock.this.new RailPlacementHelper(this.world, pos, blockState);
			} else {
				BlockPos blockPos = pos.up();
				blockState = this.world.getBlockState(blockPos);
				if (AbstractRailBlock.isRail(blockState)) {
					return AbstractRailBlock.this.new RailPlacementHelper(this.world, blockPos, blockState);
				} else {
					blockPos = pos.down();
					blockState = this.world.getBlockState(blockPos);
					return AbstractRailBlock.isRail(blockState) ? AbstractRailBlock.this.new RailPlacementHelper(this.world, blockPos, blockState) : null;
				}
			}
		}

		private boolean isNeighbor(AbstractRailBlock.RailPlacementHelper other) {
			return this.isNeighbor(other.pos);
		}

		private boolean isNeighbor(BlockPos pos) {
			for (int i = 0; i < this.neighbors.size(); i++) {
				BlockPos blockPos = (BlockPos)this.neighbors.get(i);
				if (blockPos.getX() == pos.getX() && blockPos.getZ() == pos.getZ()) {
					return true;
				}
			}

			return false;
		}

		protected int getVerticalNearbyRailCount() {
			int i = 0;

			for (Direction direction : Direction.DirectionType.HORIZONTAL) {
				if (this.isVerticallyNearRail(this.pos.offset(direction))) {
					i++;
				}
			}

			return i;
		}

		private boolean willAlignTo(AbstractRailBlock.RailPlacementHelper helper) {
			return this.isNeighbor(helper) || this.neighbors.size() != 2;
		}

		private void realignTo(AbstractRailBlock.RailPlacementHelper other) {
			this.neighbors.add(other.pos);
			BlockPos blockPos = this.pos.north();
			BlockPos blockPos2 = this.pos.south();
			BlockPos blockPos3 = this.pos.west();
			BlockPos blockPos4 = this.pos.east();
			boolean bl = this.isNeighbor(blockPos);
			boolean bl2 = this.isNeighbor(blockPos2);
			boolean bl3 = this.isNeighbor(blockPos3);
			boolean bl4 = this.isNeighbor(blockPos4);
			AbstractRailBlock.RailShapeType railShapeType = null;
			if (bl || bl2) {
				railShapeType = AbstractRailBlock.RailShapeType.NORTH_SOUTH;
			}

			if (bl3 || bl4) {
				railShapeType = AbstractRailBlock.RailShapeType.EAST_WEST;
			}

			if (!this.allowSlopes) {
				if (bl2 && bl4 && !bl && !bl3) {
					railShapeType = AbstractRailBlock.RailShapeType.SOUTH_EAST;
				}

				if (bl2 && bl3 && !bl && !bl4) {
					railShapeType = AbstractRailBlock.RailShapeType.SOUTH_WEST;
				}

				if (bl && bl3 && !bl2 && !bl4) {
					railShapeType = AbstractRailBlock.RailShapeType.NORTH_WEST;
				}

				if (bl && bl4 && !bl2 && !bl3) {
					railShapeType = AbstractRailBlock.RailShapeType.NORTH_EAST;
				}
			}

			if (railShapeType == AbstractRailBlock.RailShapeType.NORTH_SOUTH) {
				if (AbstractRailBlock.isRail(this.world, blockPos.up())) {
					railShapeType = AbstractRailBlock.RailShapeType.ASCENDING_NORTH;
				}

				if (AbstractRailBlock.isRail(this.world, blockPos2.up())) {
					railShapeType = AbstractRailBlock.RailShapeType.ASCENDING_SOUTH;
				}
			}

			if (railShapeType == AbstractRailBlock.RailShapeType.EAST_WEST) {
				if (AbstractRailBlock.isRail(this.world, blockPos4.up())) {
					railShapeType = AbstractRailBlock.RailShapeType.ASCENDING_EAST;
				}

				if (AbstractRailBlock.isRail(this.world, blockPos3.up())) {
					railShapeType = AbstractRailBlock.RailShapeType.ASCENDING_WEST;
				}
			}

			if (railShapeType == null) {
				railShapeType = AbstractRailBlock.RailShapeType.NORTH_SOUTH;
			}

			this.state = this.state.with(this.block.getShapeProperty(), railShapeType);
			this.world.setBlockState(this.pos, this.state, 3);
		}

		private boolean willAlignTo(BlockPos pos) {
			AbstractRailBlock.RailPlacementHelper railPlacementHelper = this.getVerticalHelper(pos);
			if (railPlacementHelper == null) {
				return false;
			} else {
				railPlacementHelper.correctNeighbors();
				return railPlacementHelper.willAlignTo(this);
			}
		}

		public AbstractRailBlock.RailPlacementHelper getRealignedHelper(boolean redstonePowered, boolean forceUpdate) {
			BlockPos blockPos = this.pos.north();
			BlockPos blockPos2 = this.pos.south();
			BlockPos blockPos3 = this.pos.west();
			BlockPos blockPos4 = this.pos.east();
			boolean bl = this.willAlignTo(blockPos);
			boolean bl2 = this.willAlignTo(blockPos2);
			boolean bl3 = this.willAlignTo(blockPos3);
			boolean bl4 = this.willAlignTo(blockPos4);
			AbstractRailBlock.RailShapeType railShapeType = null;
			if ((bl || bl2) && !bl3 && !bl4) {
				railShapeType = AbstractRailBlock.RailShapeType.NORTH_SOUTH;
			}

			if ((bl3 || bl4) && !bl && !bl2) {
				railShapeType = AbstractRailBlock.RailShapeType.EAST_WEST;
			}

			if (!this.allowSlopes) {
				if (bl2 && bl4 && !bl && !bl3) {
					railShapeType = AbstractRailBlock.RailShapeType.SOUTH_EAST;
				}

				if (bl2 && bl3 && !bl && !bl4) {
					railShapeType = AbstractRailBlock.RailShapeType.SOUTH_WEST;
				}

				if (bl && bl3 && !bl2 && !bl4) {
					railShapeType = AbstractRailBlock.RailShapeType.NORTH_WEST;
				}

				if (bl && bl4 && !bl2 && !bl3) {
					railShapeType = AbstractRailBlock.RailShapeType.NORTH_EAST;
				}
			}

			if (railShapeType == null) {
				if (bl || bl2) {
					railShapeType = AbstractRailBlock.RailShapeType.NORTH_SOUTH;
				}

				if (bl3 || bl4) {
					railShapeType = AbstractRailBlock.RailShapeType.EAST_WEST;
				}

				if (!this.allowSlopes) {
					if (redstonePowered) {
						if (bl2 && bl4) {
							railShapeType = AbstractRailBlock.RailShapeType.SOUTH_EAST;
						}

						if (bl3 && bl2) {
							railShapeType = AbstractRailBlock.RailShapeType.SOUTH_WEST;
						}

						if (bl4 && bl) {
							railShapeType = AbstractRailBlock.RailShapeType.NORTH_EAST;
						}

						if (bl && bl3) {
							railShapeType = AbstractRailBlock.RailShapeType.NORTH_WEST;
						}
					} else {
						if (bl && bl3) {
							railShapeType = AbstractRailBlock.RailShapeType.NORTH_WEST;
						}

						if (bl4 && bl) {
							railShapeType = AbstractRailBlock.RailShapeType.NORTH_EAST;
						}

						if (bl3 && bl2) {
							railShapeType = AbstractRailBlock.RailShapeType.SOUTH_WEST;
						}

						if (bl2 && bl4) {
							railShapeType = AbstractRailBlock.RailShapeType.SOUTH_EAST;
						}
					}
				}
			}

			if (railShapeType == AbstractRailBlock.RailShapeType.NORTH_SOUTH) {
				if (AbstractRailBlock.isRail(this.world, blockPos.up())) {
					railShapeType = AbstractRailBlock.RailShapeType.ASCENDING_NORTH;
				}

				if (AbstractRailBlock.isRail(this.world, blockPos2.up())) {
					railShapeType = AbstractRailBlock.RailShapeType.ASCENDING_SOUTH;
				}
			}

			if (railShapeType == AbstractRailBlock.RailShapeType.EAST_WEST) {
				if (AbstractRailBlock.isRail(this.world, blockPos4.up())) {
					railShapeType = AbstractRailBlock.RailShapeType.ASCENDING_EAST;
				}

				if (AbstractRailBlock.isRail(this.world, blockPos3.up())) {
					railShapeType = AbstractRailBlock.RailShapeType.ASCENDING_WEST;
				}
			}

			if (railShapeType == null) {
				railShapeType = AbstractRailBlock.RailShapeType.NORTH_SOUTH;
			}

			this.computeNeighbors(railShapeType);
			this.state = this.state.with(this.block.getShapeProperty(), railShapeType);
			if (forceUpdate || this.world.getBlockState(this.pos) != this.state) {
				this.world.setBlockState(this.pos, this.state, 3);

				for (int i = 0; i < this.neighbors.size(); i++) {
					AbstractRailBlock.RailPlacementHelper railPlacementHelper = this.getVerticalHelper((BlockPos)this.neighbors.get(i));
					if (railPlacementHelper != null) {
						railPlacementHelper.correctNeighbors();
						if (railPlacementHelper.willAlignTo(this)) {
							railPlacementHelper.realignTo(this);
						}
					}
				}
			}

			return this;
		}

		public BlockState getBlockState() {
			return this.state;
		}
	}

	public static enum RailShapeType implements StringIdentifiable {
		NORTH_SOUTH(0, "north_south"),
		EAST_WEST(1, "east_west"),
		ASCENDING_EAST(2, "ascending_east"),
		ASCENDING_WEST(3, "ascending_west"),
		ASCENDING_NORTH(4, "ascending_north"),
		ASCENDING_SOUTH(5, "ascending_south"),
		SOUTH_EAST(6, "south_east"),
		SOUTH_WEST(7, "south_west"),
		NORTH_WEST(8, "north_west"),
		NORTH_EAST(9, "north_east");

		private static final AbstractRailBlock.RailShapeType[] TYPES = new AbstractRailBlock.RailShapeType[values().length];
		private final int id;
		private final String name;

		private RailShapeType(int j, String string2) {
			this.id = j;
			this.name = string2;
		}

		public int getData() {
			return this.id;
		}

		public String toString() {
			return this.name;
		}

		public boolean isAscending() {
			return this == ASCENDING_NORTH || this == ASCENDING_EAST || this == ASCENDING_SOUTH || this == ASCENDING_WEST;
		}

		public static AbstractRailBlock.RailShapeType getById(int id) {
			if (id < 0 || id >= TYPES.length) {
				id = 0;
			}

			return TYPES[id];
		}

		@Override
		public String asString() {
			return this.name;
		}

		static {
			for (AbstractRailBlock.RailShapeType railShapeType : values()) {
				TYPES[railShapeType.getData()] = railShapeType;
			}
		}
	}
}
