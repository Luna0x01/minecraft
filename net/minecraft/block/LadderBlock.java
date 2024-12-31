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
	public boolean canBePlacedAdjacent(World world, BlockPos pos, Direction direction) {
		if (this.method_14333(world, pos.west(), direction)) {
			return true;
		} else if (this.method_14333(world, pos.east(), direction)) {
			return true;
		} else {
			return this.method_14333(world, pos.north(), direction) ? true : this.method_14333(world, pos.south(), direction);
		}
	}

	private boolean method_14333(World world, BlockPos blockPos, Direction direction) {
		BlockState blockState = world.getBlockState(blockPos);
		boolean bl = method_14309(blockState.getBlock());
		return !bl && blockState.getRenderLayer(world, blockPos, direction) == BlockRenderLayer.SOLID && !blockState.emitsRedstonePower();
	}

	@Override
	public BlockState getStateFromData(World world, BlockPos pos, Direction dir, float x, float y, float z, int id, LivingEntity entity) {
		if (dir.getAxis().isHorizontal() && this.method_14333(world, pos.offset(dir.getOpposite()), dir)) {
			return this.getDefaultState().with(FACING, dir);
		} else {
			for (Direction direction : Direction.DirectionType.HORIZONTAL) {
				if (this.method_14333(world, pos.offset(direction.getOpposite()), direction)) {
					return this.getDefaultState().with(FACING, direction);
				}
			}

			return this.getDefaultState();
		}
	}

	@Override
	public void neighborUpdate(BlockState state, World world, BlockPos pos, Block block, BlockPos neighborPos) {
		Direction direction = state.get(FACING);
		if (!this.method_14333(world, pos.offset(direction.getOpposite()), direction)) {
			this.dropAsItem(world, pos, state, 0);
			world.setAir(pos);
		}

		super.neighborUpdate(state, world, pos, block, neighborPos);
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

	@Override
	public BlockRenderLayer getRenderLayer(BlockView world, BlockState state, BlockPos pos, Direction direction) {
		return BlockRenderLayer.UNDEFINED;
	}
}
