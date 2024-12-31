package net.minecraft.block;

import javax.annotation.Nullable;
import net.minecraft.block.material.Material;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
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

public class FenceGateBlock extends HorizontalFacingBlock {
	public static final BooleanProperty OPEN = BooleanProperty.of("open");
	public static final BooleanProperty POWERED = BooleanProperty.of("powered");
	public static final BooleanProperty IN_WALL = BooleanProperty.of("in_wall");
	protected static final Box field_12674 = new Box(0.0, 0.0, 0.375, 1.0, 1.0, 0.625);
	protected static final Box field_12675 = new Box(0.375, 0.0, 0.0, 0.625, 1.0, 1.0);
	protected static final Box field_12676 = new Box(0.0, 0.0, 0.375, 1.0, 0.8125, 0.625);
	protected static final Box field_12677 = new Box(0.375, 0.0, 0.0, 0.625, 0.8125, 1.0);
	protected static final Box field_12672 = new Box(0.0, 0.0, 0.375, 1.0, 1.5, 0.625);
	protected static final Box field_12673 = new Box(0.375, 0.0, 0.0, 0.625, 1.5, 1.0);

	public FenceGateBlock(PlanksBlock.WoodType woodType) {
		super(Material.WOOD, woodType.getMaterialColor());
		this.setDefaultState(this.stateManager.getDefaultState().with(OPEN, false).with(POWERED, false).with(IN_WALL, false));
		this.setItemGroup(ItemGroup.REDSTONE);
	}

	@Override
	public Box getCollisionBox(BlockState state, BlockView view, BlockPos pos) {
		state = this.getBlockState(state, view, pos);
		if ((Boolean)state.get(IN_WALL)) {
			return ((Direction)state.get(DIRECTION)).getAxis() == Direction.Axis.X ? field_12677 : field_12676;
		} else {
			return ((Direction)state.get(DIRECTION)).getAxis() == Direction.Axis.X ? field_12675 : field_12674;
		}
	}

	@Override
	public BlockState getBlockState(BlockState state, BlockView view, BlockPos pos) {
		Direction.Axis axis = ((Direction)state.get(DIRECTION)).getAxis();
		if (axis == Direction.Axis.Z
				&& (view.getBlockState(pos.west()).getBlock() == Blocks.COBBLESTONE_WALL || view.getBlockState(pos.east()).getBlock() == Blocks.COBBLESTONE_WALL)
			|| axis == Direction.Axis.X
				&& (view.getBlockState(pos.north()).getBlock() == Blocks.COBBLESTONE_WALL || view.getBlockState(pos.south()).getBlock() == Blocks.COBBLESTONE_WALL)) {
			state = state.with(IN_WALL, true);
		}

		return state;
	}

	@Override
	public BlockState withRotation(BlockState state, BlockRotation rotation) {
		return state.with(DIRECTION, rotation.rotate(state.get(DIRECTION)));
	}

	@Override
	public BlockState withMirror(BlockState state, BlockMirror mirror) {
		return state.withRotation(mirror.getRotation(state.get(DIRECTION)));
	}

	@Override
	public boolean canBePlacedAtPos(World world, BlockPos pos) {
		return world.getBlockState(pos.down()).getMaterial().isSolid() ? super.canBePlacedAtPos(world, pos) : false;
	}

	@Nullable
	@Override
	public Box getCollisionBox(BlockState state, World world, BlockPos pos) {
		if ((Boolean)state.get(OPEN)) {
			return EMPTY_BOX;
		} else {
			return ((Direction)state.get(DIRECTION)).getAxis() == Direction.Axis.Z ? field_12672 : field_12673;
		}
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
	public boolean blocksMovement(BlockView view, BlockPos pos) {
		return (Boolean)view.getBlockState(pos).get(OPEN);
	}

	@Override
	public BlockState getStateFromData(World world, BlockPos pos, Direction dir, float x, float y, float z, int id, LivingEntity entity) {
		return this.getDefaultState().with(DIRECTION, entity.getHorizontalDirection()).with(OPEN, false).with(POWERED, false).with(IN_WALL, false);
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
		if ((Boolean)blockState.get(OPEN)) {
			blockState = blockState.with(OPEN, false);
			world.setBlockState(blockPos, blockState, 10);
		} else {
			Direction direction2 = Direction.fromRotation((double)playerEntity.yaw);
			if (blockState.get(DIRECTION) == direction2.getOpposite()) {
				blockState = blockState.with(DIRECTION, direction2);
			}

			blockState = blockState.with(OPEN, true);
			world.setBlockState(blockPos, blockState, 10);
		}

		world.syncWorldEvent(playerEntity, blockState.get(OPEN) ? 1008 : 1014, blockPos, 0);
		return true;
	}

	@Override
	public void method_8641(BlockState blockState, World world, BlockPos blockPos, Block block) {
		if (!world.isClient) {
			boolean bl = world.isReceivingRedstonePower(blockPos);
			if (bl || block.getDefaultState().emitsRedstonePower()) {
				if (bl && !(Boolean)blockState.get(OPEN) && !(Boolean)blockState.get(POWERED)) {
					world.setBlockState(blockPos, blockState.with(OPEN, true).with(POWERED, true), 2);
					world.syncWorldEvent(null, 1008, blockPos, 0);
				} else if (!bl && (Boolean)blockState.get(OPEN) && (Boolean)blockState.get(POWERED)) {
					world.setBlockState(blockPos, blockState.with(OPEN, false).with(POWERED, false), 2);
					world.syncWorldEvent(null, 1014, blockPos, 0);
				} else if (bl != (Boolean)blockState.get(POWERED)) {
					world.setBlockState(blockPos, blockState.with(POWERED, bl), 2);
				}
			}
		}
	}

	@Override
	public boolean method_8654(BlockState state, BlockView view, BlockPos pos, Direction direction) {
		return true;
	}

	@Override
	public BlockState stateFromData(int data) {
		return this.getDefaultState().with(DIRECTION, Direction.fromHorizontal(data)).with(OPEN, (data & 4) != 0).with(POWERED, (data & 8) != 0);
	}

	@Override
	public int getData(BlockState state) {
		int i = 0;
		i |= ((Direction)state.get(DIRECTION)).getHorizontal();
		if ((Boolean)state.get(POWERED)) {
			i |= 8;
		}

		if ((Boolean)state.get(OPEN)) {
			i |= 4;
		}

		return i;
	}

	@Override
	protected StateManager appendProperties() {
		return new StateManager(this, DIRECTION, OPEN, POWERED, IN_WALL);
	}
}
