package net.minecraft.block;

import net.minecraft.block.material.Material;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.itemgroup.ItemGroup;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.util.StringIdentifiable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

public class LeverBlock extends Block {
	public static final EnumProperty<LeverBlock.LeverType> FACING = EnumProperty.of("facing", LeverBlock.LeverType.class);
	public static final BooleanProperty POWERED = BooleanProperty.of("powered");

	protected LeverBlock() {
		super(Material.DECORATION);
		this.setDefaultState(this.stateManager.getDefaultState().with(FACING, LeverBlock.LeverType.NORTH).with(POWERED, false));
		this.setItemGroup(ItemGroup.REDSTONE);
	}

	@Override
	public Box getCollisionBox(World world, BlockPos pos, BlockState state) {
		return null;
	}

	@Override
	public boolean hasTransparency() {
		return false;
	}

	@Override
	public boolean renderAsNormalBlock() {
		return false;
	}

	@Override
	public boolean canBePlacedAdjacent(World world, BlockPos pos, Direction direction) {
		return canHoldLever(world, pos, direction.getOpposite());
	}

	@Override
	public boolean canBePlacedAtPos(World world, BlockPos pos) {
		for (Direction direction : Direction.values()) {
			if (canHoldLever(world, pos, direction)) {
				return true;
			}
		}

		return false;
	}

	protected static boolean canHoldLever(World world, BlockPos pos, Direction dir) {
		return AbstractButtonBlock.canHoldButton(world, pos, dir);
	}

	@Override
	public BlockState getStateFromData(World world, BlockPos pos, Direction dir, float x, float y, float z, int id, LivingEntity entity) {
		BlockState blockState = this.getDefaultState().with(POWERED, false);
		if (canHoldLever(world, pos, dir.getOpposite())) {
			return blockState.with(FACING, LeverBlock.LeverType.getByDirection(dir, entity.getHorizontalDirection()));
		} else {
			for (Direction direction : Direction.DirectionType.HORIZONTAL) {
				if (direction != dir && canHoldLever(world, pos, direction.getOpposite())) {
					return blockState.with(FACING, LeverBlock.LeverType.getByDirection(direction, entity.getHorizontalDirection()));
				}
			}

			return World.isOpaque(world, pos.down())
				? blockState.with(FACING, LeverBlock.LeverType.getByDirection(Direction.UP, entity.getHorizontalDirection()))
				: blockState;
		}
	}

	public static int getDataFromDirection(Direction dir) {
		switch (dir) {
			case DOWN:
				return 0;
			case UP:
				return 5;
			case NORTH:
				return 4;
			case SOUTH:
				return 3;
			case WEST:
				return 2;
			case EAST:
				return 1;
			default:
				return -1;
		}
	}

	@Override
	public void neighborUpdate(World world, BlockPos pos, BlockState state, Block block) {
		if (this.placeLever(world, pos, state) && !canHoldLever(world, pos, ((LeverBlock.LeverType)state.get(FACING)).getDirection().getOpposite())) {
			this.dropAsItem(world, pos, state, 0);
			world.setAir(pos);
		}
	}

	private boolean placeLever(World world, BlockPos pos, BlockState state) {
		if (this.canBePlacedAtPos(world, pos)) {
			return true;
		} else {
			this.dropAsItem(world, pos, state, 0);
			world.setAir(pos);
			return false;
		}
	}

	@Override
	public void setBoundingBox(BlockView view, BlockPos pos) {
		float f = 0.1875F;
		switch ((LeverBlock.LeverType)view.getBlockState(pos).get(FACING)) {
			case EAST:
				this.setBoundingBox(0.0F, 0.2F, 0.5F - f, f * 2.0F, 0.8F, 0.5F + f);
				break;
			case WEST:
				this.setBoundingBox(1.0F - f * 2.0F, 0.2F, 0.5F - f, 1.0F, 0.8F, 0.5F + f);
				break;
			case SOUTH:
				this.setBoundingBox(0.5F - f, 0.2F, 0.0F, 0.5F + f, 0.8F, f * 2.0F);
				break;
			case NORTH:
				this.setBoundingBox(0.5F - f, 0.2F, 1.0F - f * 2.0F, 0.5F + f, 0.8F, 1.0F);
				break;
			case UP_Z:
			case UP_X:
				f = 0.25F;
				this.setBoundingBox(0.5F - f, 0.0F, 0.5F - f, 0.5F + f, 0.6F, 0.5F + f);
				break;
			case DOWN_X:
			case DOWN_Z:
				f = 0.25F;
				this.setBoundingBox(0.5F - f, 0.4F, 0.5F - f, 0.5F + f, 1.0F, 0.5F + f);
		}
	}

	@Override
	public boolean onUse(World world, BlockPos pos, BlockState state, PlayerEntity player, Direction direction, float posX, float posY, float posZ) {
		if (world.isClient) {
			return true;
		} else {
			state = state.withDefaultValue(POWERED);
			world.setBlockState(pos, state, 3);
			world.playSound((double)pos.getX() + 0.5, (double)pos.getY() + 0.5, (double)pos.getZ() + 0.5, "random.click", 0.3F, state.get(POWERED) ? 0.6F : 0.5F);
			world.updateNeighborsAlways(pos, this);
			Direction direction2 = ((LeverBlock.LeverType)state.get(FACING)).getDirection();
			world.updateNeighborsAlways(pos.offset(direction2.getOpposite()), this);
			return true;
		}
	}

	@Override
	public void onBreaking(World world, BlockPos pos, BlockState state) {
		if ((Boolean)state.get(POWERED)) {
			world.updateNeighborsAlways(pos, this);
			Direction direction = ((LeverBlock.LeverType)state.get(FACING)).getDirection();
			world.updateNeighborsAlways(pos.offset(direction.getOpposite()), this);
		}

		super.onBreaking(world, pos, state);
	}

	@Override
	public int getWeakRedstonePower(BlockView view, BlockPos pos, BlockState state, Direction facing) {
		return state.get(POWERED) ? 15 : 0;
	}

	@Override
	public int getStrongRedstonePower(BlockView view, BlockPos pos, BlockState state, Direction facing) {
		if (!(Boolean)state.get(POWERED)) {
			return 0;
		} else {
			return ((LeverBlock.LeverType)state.get(FACING)).getDirection() == facing ? 15 : 0;
		}
	}

	@Override
	public boolean emitsRedstonePower() {
		return true;
	}

	@Override
	public BlockState stateFromData(int data) {
		return this.getDefaultState().with(FACING, LeverBlock.LeverType.getById(data & 7)).with(POWERED, (data & 8) > 0);
	}

	@Override
	public int getData(BlockState state) {
		int i = 0;
		i |= ((LeverBlock.LeverType)state.get(FACING)).getId();
		if ((Boolean)state.get(POWERED)) {
			i |= 8;
		}

		return i;
	}

	@Override
	protected StateManager appendProperties() {
		return new StateManager(this, FACING, POWERED);
	}

	public static enum LeverType implements StringIdentifiable {
		DOWN_X(0, "down_x", Direction.DOWN),
		EAST(1, "east", Direction.EAST),
		WEST(2, "west", Direction.WEST),
		SOUTH(3, "south", Direction.SOUTH),
		NORTH(4, "north", Direction.NORTH),
		UP_Z(5, "up_z", Direction.UP),
		UP_X(6, "up_x", Direction.UP),
		DOWN_Z(7, "down_z", Direction.DOWN);

		private static final LeverBlock.LeverType[] TYPES = new LeverBlock.LeverType[values().length];
		private final int id;
		private final String name;
		private final Direction direction;

		private LeverType(int j, String string2, Direction direction) {
			this.id = j;
			this.name = string2;
			this.direction = direction;
		}

		public int getId() {
			return this.id;
		}

		public Direction getDirection() {
			return this.direction;
		}

		public String toString() {
			return this.name;
		}

		public static LeverBlock.LeverType getById(int id) {
			if (id < 0 || id >= TYPES.length) {
				id = 0;
			}

			return TYPES[id];
		}

		public static LeverBlock.LeverType getByDirection(Direction dir1, Direction dir2) {
			switch (dir1) {
				case DOWN:
					switch (dir2.getAxis()) {
						case X:
							return DOWN_X;
						case Z:
							return DOWN_Z;
						default:
							throw new IllegalArgumentException("Invalid entityFacing " + dir2 + " for facing " + dir1);
					}
				case UP:
					switch (dir2.getAxis()) {
						case X:
							return UP_X;
						case Z:
							return UP_Z;
						default:
							throw new IllegalArgumentException("Invalid entityFacing " + dir2 + " for facing " + dir1);
					}
				case NORTH:
					return NORTH;
				case SOUTH:
					return SOUTH;
				case WEST:
					return WEST;
				case EAST:
					return EAST;
				default:
					throw new IllegalArgumentException("Invalid facing: " + dir1);
			}
		}

		@Override
		public String asString() {
			return this.name;
		}

		static {
			for (LeverBlock.LeverType leverType : values()) {
				TYPES[leverType.getId()] = leverType;
			}
		}
	}
}
