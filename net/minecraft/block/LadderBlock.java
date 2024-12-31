package net.minecraft.block;

import net.minecraft.block.material.Material;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.itemgroup.ItemGroup;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

public class LadderBlock extends Block {
	public static final DirectionProperty FACING = HorizontalFacingBlock.DIRECTION;
	protected static final Box field_12693 = new Box(0.0, 0.0, 0.0, 0.1875, 1.0, 1.0);
	protected static final Box field_12694 = new Box(0.8125, 0.0, 0.0, 1.0, 1.0, 1.0);
	protected static final Box field_12695 = new Box(0.0, 0.0, 0.0, 1.0, 1.0, 0.1875);
	protected static final Box field_12696 = new Box(0.0, 0.0, 0.8125, 1.0, 1.0, 1.0);

	protected LadderBlock() {
		super(Material.DECORATION);
		this.setDefaultState(this.stateManager.getDefaultState().with(FACING, Direction.NORTH));
		this.setItemGroup(ItemGroup.DECORATIONS);
	}

	@Override
	public Box getCollisionBox(BlockState state, BlockView view, BlockPos pos) {
		switch ((Direction)state.get(FACING)) {
			case NORTH:
				return field_12696;
			case SOUTH:
				return field_12695;
			case WEST:
				return field_12694;
			case EAST:
			default:
				return field_12693;
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
	public boolean canBePlacedAtPos(World world, BlockPos pos) {
		if (world.getBlockState(pos.west()).method_11734()) {
			return true;
		} else if (world.getBlockState(pos.east()).method_11734()) {
			return true;
		} else {
			return world.getBlockState(pos.north()).method_11734() ? true : world.getBlockState(pos.south()).method_11734();
		}
	}

	@Override
	public BlockState getStateFromData(World world, BlockPos pos, Direction dir, float x, float y, float z, int id, LivingEntity entity) {
		if (dir.getAxis().isHorizontal() && this.isOppositeFull(world, pos, dir)) {
			return this.getDefaultState().with(FACING, dir);
		} else {
			for (Direction direction : Direction.DirectionType.HORIZONTAL) {
				if (this.isOppositeFull(world, pos, direction)) {
					return this.getDefaultState().with(FACING, direction);
				}
			}

			return this.getDefaultState();
		}
	}

	@Override
	public void method_8641(BlockState blockState, World world, BlockPos blockPos, Block block) {
		Direction direction = blockState.get(FACING);
		if (!this.isOppositeFull(world, blockPos, direction)) {
			this.dropAsItem(world, blockPos, blockState, 0);
			world.setAir(blockPos);
		}

		super.method_8641(blockState, world, blockPos, block);
	}

	protected boolean isOppositeFull(World world, BlockPos pos, Direction dir) {
		return world.getBlockState(pos.offset(dir.getOpposite())).method_11734();
	}

	@Override
	public RenderLayer getRenderLayerType() {
		return RenderLayer.CUTOUT;
	}

	@Override
	public BlockState stateFromData(int data) {
		Direction direction = Direction.getById(data);
		if (direction.getAxis() == Direction.Axis.Y) {
			direction = Direction.NORTH;
		}

		return this.getDefaultState().with(FACING, direction);
	}

	@Override
	public int getData(BlockState state) {
		return ((Direction)state.get(FACING)).getId();
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
		return new StateManager(this, FACING);
	}
}
