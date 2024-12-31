package net.minecraft.block;

import java.util.List;
import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.AbstractArrowEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.itemgroup.ItemGroup;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

public abstract class AbstractButtonBlock extends FacingBlock {
	public static final BooleanProperty POWERED = BooleanProperty.of("powered");
	protected static final Box field_12601 = new Box(0.3125, 0.875, 0.375, 0.6875, 1.0, 0.625);
	protected static final Box field_12602 = new Box(0.3125, 0.0, 0.375, 0.6875, 0.125, 0.625);
	protected static final Box field_12603 = new Box(0.3125, 0.375, 0.875, 0.6875, 0.625, 1.0);
	protected static final Box field_12604 = new Box(0.3125, 0.375, 0.0, 0.6875, 0.625, 0.125);
	protected static final Box field_12605 = new Box(0.875, 0.375, 0.3125, 1.0, 0.625, 0.6875);
	protected static final Box field_12606 = new Box(0.0, 0.375, 0.3125, 0.125, 0.625, 0.6875);
	protected static final Box field_12595 = new Box(0.3125, 0.9375, 0.375, 0.6875, 1.0, 0.625);
	protected static final Box field_12596 = new Box(0.3125, 0.0, 0.375, 0.6875, 0.0625, 0.625);
	protected static final Box field_12597 = new Box(0.3125, 0.375, 0.9375, 0.6875, 0.625, 1.0);
	protected static final Box field_12598 = new Box(0.3125, 0.375, 0.0, 0.6875, 0.625, 0.0625);
	protected static final Box field_12599 = new Box(0.9375, 0.375, 0.3125, 1.0, 0.625, 0.6875);
	protected static final Box field_12600 = new Box(0.0, 0.375, 0.3125, 0.0625, 0.625, 0.6875);
	private final boolean wooden;

	protected AbstractButtonBlock(boolean bl) {
		super(Material.DECORATION);
		this.setDefaultState(this.stateManager.getDefaultState().with(FACING, Direction.NORTH).with(POWERED, false));
		this.setTickRandomly(true);
		this.setItemGroup(ItemGroup.REDSTONE);
		this.wooden = bl;
	}

	@Nullable
	@Override
	public Box getCollisionBox(BlockState state, World world, BlockPos pos) {
		return EMPTY_BOX;
	}

	@Override
	public int getTickRate(World world) {
		return this.wooden ? 30 : 20;
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
		return canHoldButton(world, pos, direction.getOpposite());
	}

	@Override
	public boolean canBePlacedAtPos(World world, BlockPos pos) {
		for (Direction direction : Direction.values()) {
			if (canHoldButton(world, pos, direction)) {
				return true;
			}
		}

		return false;
	}

	protected static boolean canHoldButton(World world, BlockPos pos, Direction dir) {
		BlockPos blockPos = pos.offset(dir);
		return dir == Direction.DOWN ? world.getBlockState(blockPos).method_11739() : world.getBlockState(blockPos).method_11734();
	}

	@Override
	public BlockState getStateFromData(World world, BlockPos pos, Direction dir, float x, float y, float z, int id, LivingEntity entity) {
		return canHoldButton(world, pos, dir.getOpposite())
			? this.getDefaultState().with(FACING, dir).with(POWERED, false)
			: this.getDefaultState().with(FACING, Direction.DOWN).with(POWERED, false);
	}

	@Override
	public void method_8641(BlockState blockState, World world, BlockPos blockPos, Block block) {
		if (this.isButtonPlacementValid(world, blockPos, blockState) && !canHoldButton(world, blockPos, ((Direction)blockState.get(FACING)).getOpposite())) {
			this.dropAsItem(world, blockPos, blockState, 0);
			world.setAir(blockPos);
		}
	}

	private boolean isButtonPlacementValid(World world, BlockPos pos, BlockState state) {
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
		Direction direction = state.get(FACING);
		boolean bl = (Boolean)state.get(POWERED);
		switch (direction) {
			case EAST:
				return bl ? field_12600 : field_12606;
			case WEST:
				return bl ? field_12599 : field_12605;
			case SOUTH:
				return bl ? field_12598 : field_12604;
			case NORTH:
			default:
				return bl ? field_12597 : field_12603;
			case UP:
				return bl ? field_12596 : field_12602;
			case DOWN:
				return bl ? field_12595 : field_12601;
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
		if ((Boolean)blockState.get(POWERED)) {
			return true;
		} else {
			world.setBlockState(blockPos, blockState.with(POWERED, true), 3);
			world.onRenderRegionUpdate(blockPos, blockPos);
			this.method_11580(playerEntity, world, blockPos);
			this.updateNeighborsAfterActivation(world, blockPos, blockState.get(FACING));
			world.createAndScheduleBlockTick(blockPos, this, this.getTickRate(world));
			return true;
		}
	}

	protected abstract void method_11580(@Nullable PlayerEntity playerEntity, World world, BlockPos blockPos);

	protected abstract void method_11581(World world, BlockPos blockPos);

	@Override
	public void onBreaking(World world, BlockPos pos, BlockState state) {
		if ((Boolean)state.get(POWERED)) {
			this.updateNeighborsAfterActivation(world, pos, state.get(FACING));
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
			return state.get(FACING) == direction ? 15 : 0;
		}
	}

	@Override
	public boolean emitsRedstonePower(BlockState state) {
		return true;
	}

	@Override
	public void onRandomTick(World world, BlockPos pos, BlockState state, Random rand) {
	}

	@Override
	public void onScheduledTick(World world, BlockPos pos, BlockState state, Random rand) {
		if (!world.isClient) {
			if ((Boolean)state.get(POWERED)) {
				if (this.wooden) {
					this.tryPowerWithProjectiles(state, world, pos);
				} else {
					world.setBlockState(pos, state.with(POWERED, false));
					this.updateNeighborsAfterActivation(world, pos, state.get(FACING));
					this.method_11581(world, pos);
					world.onRenderRegionUpdate(pos, pos);
				}
			}
		}
	}

	@Override
	public void onEntityCollision(World world, BlockPos pos, BlockState state, Entity entity) {
		if (!world.isClient) {
			if (this.wooden) {
				if (!(Boolean)state.get(POWERED)) {
					this.tryPowerWithProjectiles(state, world, pos);
				}
			}
		}
	}

	private void tryPowerWithProjectiles(BlockState state, World world, BlockPos pos) {
		List<? extends Entity> list = world.getEntitiesInBox(AbstractArrowEntity.class, state.getCollisionBox((BlockView)world, pos).offset(pos));
		boolean bl = !list.isEmpty();
		boolean bl2 = (Boolean)state.get(POWERED);
		if (bl && !bl2) {
			world.setBlockState(pos, state.with(POWERED, true));
			this.updateNeighborsAfterActivation(world, pos, state.get(FACING));
			world.onRenderRegionUpdate(pos, pos);
			this.method_11580(null, world, pos);
		}

		if (!bl && bl2) {
			world.setBlockState(pos, state.with(POWERED, false));
			this.updateNeighborsAfterActivation(world, pos, state.get(FACING));
			world.onRenderRegionUpdate(pos, pos);
			this.method_11581(world, pos);
		}

		if (bl) {
			world.createAndScheduleBlockTick(new BlockPos(pos), this, this.getTickRate(world));
		}
	}

	private void updateNeighborsAfterActivation(World world, BlockPos pos, Direction dir) {
		world.updateNeighborsAlways(pos, this);
		world.updateNeighborsAlways(pos.offset(dir.getOpposite()), this);
	}

	@Override
	public BlockState stateFromData(int data) {
		Direction direction;
		switch (data & 7) {
			case 0:
				direction = Direction.DOWN;
				break;
			case 1:
				direction = Direction.EAST;
				break;
			case 2:
				direction = Direction.WEST;
				break;
			case 3:
				direction = Direction.SOUTH;
				break;
			case 4:
				direction = Direction.NORTH;
				break;
			case 5:
			default:
				direction = Direction.UP;
		}

		return this.getDefaultState().with(FACING, direction).with(POWERED, (data & 8) > 0);
	}

	@Override
	public int getData(BlockState state) {
		int i;
		switch ((Direction)state.get(FACING)) {
			case EAST:
				i = 1;
				break;
			case WEST:
				i = 2;
				break;
			case SOUTH:
				i = 3;
				break;
			case NORTH:
				i = 4;
				break;
			case UP:
			default:
				i = 5;
				break;
			case DOWN:
				i = 0;
		}

		if ((Boolean)state.get(POWERED)) {
			i |= 8;
		}

		return i;
	}

	@Override
	public BlockState withRotation(BlockState state, BlockRotation rotation) {
		return state.with(FACING, rotation.rotate(state.get(FACING)));
	}

	@Override
	public BlockState withMirror(BlockState state, BlockMirror mirror) {
		return state.withRotation(mirror.getRotation(state.get(FACING)));
	}

	@Override
	protected StateManager appendProperties() {
		return new StateManager(this, FACING, POWERED);
	}
}
