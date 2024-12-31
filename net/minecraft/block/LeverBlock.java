package net.minecraft.block;

import javax.annotation.Nullable;
import net.minecraft.block.material.Material;
import net.minecraft.client.sound.SoundCategory;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.itemgroup.ItemGroup;
import net.minecraft.sound.Sounds;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.Hand;
import net.minecraft.util.StringIdentifiable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

public class LeverBlock extends Block {
	public static final EnumProperty<LeverBlock.LeverType> FACING = EnumProperty.of("facing", LeverBlock.LeverType.class);
	public static final BooleanProperty POWERED = BooleanProperty.of("powered");
	protected static final Box field_12698 = new Box(0.3125, 0.2F, 0.625, 0.6875, 0.8F, 1.0);
	protected static final Box field_12699 = new Box(0.3125, 0.2F, 0.0, 0.6875, 0.8F, 0.375);
	protected static final Box field_12700 = new Box(0.625, 0.2F, 0.3125, 1.0, 0.8F, 0.6875);
	protected static final Box field_12701 = new Box(0.0, 0.2F, 0.3125, 0.375, 0.8F, 0.6875);
	protected static final Box field_12702 = new Box(0.25, 0.0, 0.25, 0.75, 0.6F, 0.75);
	protected static final Box field_12697 = new Box(0.25, 0.4F, 0.25, 0.75, 1.0, 0.75);

	protected LeverBlock() {
		super(Material.DECORATION);
		this.setDefaultState(this.stateManager.getDefaultState().with(FACING, LeverBlock.LeverType.NORTH).with(POWERED, false));
		this.setItemGroup(ItemGroup.REDSTONE);
	}

	@Nullable
	@Override
	public Box getCollisionBox(BlockState state, World world, BlockPos pos) {
		return EMPTY_BOX;
	}

	@Override
	public boolean isFullBoundsCubeForCulling(BlockState blockState) {
		return false;
	}

	@Override
	public boolean method_11562(BlockState state) {
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

			return world.getBlockState(pos.down()).method_11739()
				? blockState.with(FACING, LeverBlock.LeverType.getByDirection(Direction.UP, entity.getHorizontalDirection()))
				: blockState;
		}
	}

	@Override
	public void method_8641(BlockState blockState, World world, BlockPos blockPos, Block block) {
		if (this.placeLever(world, blockPos, blockState)
			&& !canHoldLever(world, blockPos, ((LeverBlock.LeverType)blockState.get(FACING)).getDirection().getOpposite())) {
			this.dropAsItem(world, blockPos, blockState, 0);
			world.setAir(blockPos);
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
	public Box getCollisionBox(BlockState state, BlockView view, BlockPos pos) {
		switch ((LeverBlock.LeverType)state.get(FACING)) {
			case EAST:
			default:
				return field_12701;
			case WEST:
				return field_12700;
			case SOUTH:
				return field_12699;
			case NORTH:
				return field_12698;
			case UP_Z:
			case UP_X:
				return field_12702;
			case DOWN_X:
			case DOWN_Z:
				return field_12697;
		}
	}

	@Override
	public boolean method_421(
		World world,
		BlockPos blockPos,
		BlockState blockState,
		PlayerEntity playerEntity,
		Hand hand,
		@Nullable ItemStack itemStack,
		Direction direction,
		float f,
		float g,
		float h
	) {
		if (world.isClient) {
			return true;
		} else {
			blockState = blockState.withDefaultValue(POWERED);
			world.setBlockState(blockPos, blockState, 3);
			float i = blockState.get(POWERED) ? 0.6F : 0.5F;
			world.method_11486(null, blockPos, Sounds.BLOCK_LEVER_CLICK, SoundCategory.BLOCKS, 0.3F, i);
			world.updateNeighborsAlways(blockPos, this);
			Direction direction2 = ((LeverBlock.LeverType)blockState.get(FACING)).getDirection();
			world.updateNeighborsAlways(blockPos.offset(direction2.getOpposite()), this);
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
	public int getWeakRedstonePower(BlockState state, BlockView world, BlockPos pos, Direction direction) {
		return state.get(POWERED) ? 15 : 0;
	}

	@Override
	public int getStrongRedstonePower(BlockState state, BlockView world, BlockPos pos, Direction direction) {
		if (!(Boolean)state.get(POWERED)) {
			return 0;
		} else {
			return ((LeverBlock.LeverType)state.get(FACING)).getDirection() == direction ? 15 : 0;
		}
	}

	@Override
	public boolean emitsRedstonePower(BlockState state) {
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
	public BlockState withRotation(BlockState state, BlockRotation rotation) {
		switch (rotation) {
			case CLOCKWISE_180:
				switch ((LeverBlock.LeverType)state.get(FACING)) {
					case EAST:
						return state.with(FACING, LeverBlock.LeverType.WEST);
					case WEST:
						return state.with(FACING, LeverBlock.LeverType.EAST);
					case SOUTH:
						return state.with(FACING, LeverBlock.LeverType.NORTH);
					case NORTH:
						return state.with(FACING, LeverBlock.LeverType.SOUTH);
					default:
						return state;
				}
			case COUNTERCLOCKWISE_90:
				switch ((LeverBlock.LeverType)state.get(FACING)) {
					case EAST:
						return state.with(FACING, LeverBlock.LeverType.NORTH);
					case WEST:
						return state.with(FACING, LeverBlock.LeverType.SOUTH);
					case SOUTH:
						return state.with(FACING, LeverBlock.LeverType.EAST);
					case NORTH:
						return state.with(FACING, LeverBlock.LeverType.WEST);
					case UP_Z:
						return state.with(FACING, LeverBlock.LeverType.UP_X);
					case UP_X:
						return state.with(FACING, LeverBlock.LeverType.UP_Z);
					case DOWN_X:
						return state.with(FACING, LeverBlock.LeverType.DOWN_Z);
					case DOWN_Z:
						return state.with(FACING, LeverBlock.LeverType.DOWN_X);
				}
			case CLOCKWISE_90:
				switch ((LeverBlock.LeverType)state.get(FACING)) {
					case EAST:
						return state.with(FACING, LeverBlock.LeverType.SOUTH);
					case WEST:
						return state.with(FACING, LeverBlock.LeverType.NORTH);
					case SOUTH:
						return state.with(FACING, LeverBlock.LeverType.WEST);
					case NORTH:
						return state.with(FACING, LeverBlock.LeverType.EAST);
					case UP_Z:
						return state.with(FACING, LeverBlock.LeverType.UP_X);
					case UP_X:
						return state.with(FACING, LeverBlock.LeverType.UP_Z);
					case DOWN_X:
						return state.with(FACING, LeverBlock.LeverType.DOWN_Z);
					case DOWN_Z:
						return state.with(FACING, LeverBlock.LeverType.DOWN_X);
				}
			default:
				return state;
		}
	}

	@Override
	public BlockState withMirror(BlockState state, BlockMirror mirror) {
		return state.withRotation(mirror.getRotation(((LeverBlock.LeverType)state.get(FACING)).getDirection()));
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
