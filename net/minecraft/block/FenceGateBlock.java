package net.minecraft.block;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.states.property.Properties;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shapes.VoxelShape;
import net.minecraft.util.shapes.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;

public class FenceGateBlock extends HorizontalFacingBlock {
	public static final BooleanProperty field_18320 = Properties.OPEN;
	public static final BooleanProperty field_18321 = Properties.POWERED;
	public static final BooleanProperty field_18322 = Properties.IN_WALL;
	protected static final VoxelShape field_18323 = Block.createCuboidShape(0.0, 0.0, 6.0, 16.0, 16.0, 10.0);
	protected static final VoxelShape field_18324 = Block.createCuboidShape(6.0, 0.0, 0.0, 10.0, 16.0, 16.0);
	protected static final VoxelShape field_18325 = Block.createCuboidShape(0.0, 0.0, 6.0, 16.0, 13.0, 10.0);
	protected static final VoxelShape field_18326 = Block.createCuboidShape(6.0, 0.0, 0.0, 10.0, 13.0, 16.0);
	protected static final VoxelShape field_18327 = Block.createCuboidShape(0.0, 0.0, 6.0, 16.0, 24.0, 10.0);
	protected static final VoxelShape field_18328 = Block.createCuboidShape(6.0, 0.0, 0.0, 10.0, 24.0, 16.0);
	protected static final VoxelShape field_18329 = VoxelShapes.union(
		Block.createCuboidShape(0.0, 5.0, 7.0, 2.0, 16.0, 9.0), Block.createCuboidShape(14.0, 5.0, 7.0, 16.0, 16.0, 9.0)
	);
	protected static final VoxelShape field_18330 = VoxelShapes.union(
		Block.createCuboidShape(7.0, 5.0, 0.0, 9.0, 16.0, 2.0), Block.createCuboidShape(7.0, 5.0, 14.0, 9.0, 16.0, 16.0)
	);
	protected static final VoxelShape field_18331 = VoxelShapes.union(
		Block.createCuboidShape(0.0, 2.0, 7.0, 2.0, 13.0, 9.0), Block.createCuboidShape(14.0, 2.0, 7.0, 16.0, 13.0, 9.0)
	);
	protected static final VoxelShape field_18332 = VoxelShapes.union(
		Block.createCuboidShape(7.0, 2.0, 0.0, 9.0, 13.0, 2.0), Block.createCuboidShape(7.0, 2.0, 14.0, 9.0, 13.0, 16.0)
	);

	public FenceGateBlock(Block.Builder builder) {
		super(builder);
		this.setDefaultState(
			this.stateManager
				.method_16923()
				.withProperty(field_18320, Boolean.valueOf(false))
				.withProperty(field_18321, Boolean.valueOf(false))
				.withProperty(field_18322, Boolean.valueOf(false))
		);
	}

	@Override
	public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos) {
		if ((Boolean)state.getProperty(field_18322)) {
			return ((Direction)state.getProperty(FACING)).getAxis() == Direction.Axis.X ? field_18326 : field_18325;
		} else {
			return ((Direction)state.getProperty(FACING)).getAxis() == Direction.Axis.X ? field_18324 : field_18323;
		}
	}

	@Override
	public BlockState getStateForNeighborUpdate(BlockState state, Direction direction, BlockState neighborState, IWorld world, BlockPos pos, BlockPos neighborPos) {
		Direction.Axis axis = direction.getAxis();
		if (((Direction)state.getProperty(FACING)).rotateYClockwise().getAxis() != axis) {
			return super.getStateForNeighborUpdate(state, direction, neighborState, world, pos, neighborPos);
		} else {
			boolean bl = this.method_16677(neighborState) || this.method_16677(world.getBlockState(pos.offset(direction.getOpposite())));
			return state.withProperty(field_18322, Boolean.valueOf(bl));
		}
	}

	@Override
	public VoxelShape getCollisionShape(BlockState state, BlockView world, BlockPos pos) {
		if ((Boolean)state.getProperty(field_18320)) {
			return VoxelShapes.empty();
		} else {
			return ((Direction)state.getProperty(FACING)).getAxis() == Direction.Axis.Z ? field_18327 : field_18328;
		}
	}

	@Override
	public VoxelShape method_16593(BlockState state, BlockView world, BlockPos pos) {
		if ((Boolean)state.getProperty(field_18322)) {
			return ((Direction)state.getProperty(FACING)).getAxis() == Direction.Axis.X ? field_18332 : field_18331;
		} else {
			return ((Direction)state.getProperty(FACING)).getAxis() == Direction.Axis.X ? field_18330 : field_18329;
		}
	}

	@Override
	public boolean method_11562(BlockState state) {
		return false;
	}

	@Override
	public boolean canPlaceAtSide(BlockState state, BlockView world, BlockPos pos, BlockPlacementEnvironment environment) {
		switch (environment) {
			case LAND:
				return (Boolean)state.getProperty(field_18320);
			case WATER:
				return false;
			case AIR:
				return (Boolean)state.getProperty(field_18320);
			default:
				return false;
		}
	}

	@Override
	public BlockState getPlacementState(ItemPlacementContext context) {
		World world = context.getWorld();
		BlockPos blockPos = context.getBlockPos();
		boolean bl = world.isReceivingRedstonePower(blockPos);
		Direction direction = context.method_16145();
		Direction.Axis axis = direction.getAxis();
		boolean bl2 = axis == Direction.Axis.Z
				&& (this.method_16677(world.getBlockState(blockPos.west())) || this.method_16677(world.getBlockState(blockPos.east())))
			|| axis == Direction.Axis.X && (this.method_16677(world.getBlockState(blockPos.north())) || this.method_16677(world.getBlockState(blockPos.south())));
		return this.getDefaultState()
			.withProperty(FACING, direction)
			.withProperty(field_18320, Boolean.valueOf(bl))
			.withProperty(field_18321, Boolean.valueOf(bl))
			.withProperty(field_18322, Boolean.valueOf(bl2));
	}

	private boolean method_16677(BlockState blockState) {
		return blockState.getBlock() == Blocks.COBBLESTONE_WALL || blockState.getBlock() == Blocks.MOSSY_COBBLESTONE_WALL;
	}

	@Override
	public boolean onUse(
		BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, Direction direction, float distanceX, float distanceY, float distanceZ
	) {
		if ((Boolean)state.getProperty(field_18320)) {
			state = state.withProperty(field_18320, Boolean.valueOf(false));
			world.setBlockState(pos, state, 10);
		} else {
			Direction direction2 = player.getHorizontalDirection();
			if (state.getProperty(FACING) == direction2.getOpposite()) {
				state = state.withProperty(FACING, direction2);
			}

			state = state.withProperty(field_18320, Boolean.valueOf(true));
			world.setBlockState(pos, state, 10);
		}

		world.syncWorldEvent(player, state.getProperty(field_18320) ? 1008 : 1014, pos, 0);
		return true;
	}

	@Override
	public void neighborUpdate(BlockState state, World world, BlockPos pos, Block block, BlockPos neighborPos) {
		if (!world.isClient) {
			boolean bl = world.isReceivingRedstonePower(pos);
			if ((Boolean)state.getProperty(field_18321) != bl) {
				world.setBlockState(pos, state.withProperty(field_18321, Boolean.valueOf(bl)).withProperty(field_18320, Boolean.valueOf(bl)), 2);
				if ((Boolean)state.getProperty(field_18320) != bl) {
					world.syncWorldEvent(null, bl ? 1008 : 1014, pos, 0);
				}
			}
		}
	}

	@Override
	protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
		builder.method_16928(FACING, field_18320, field_18321, field_18322);
	}

	@Override
	public BlockRenderLayer getRenderLayer(BlockView world, BlockState state, BlockPos pos, Direction direction) {
		if (direction != Direction.UP && direction != Direction.DOWN) {
			return ((Direction)state.getProperty(FACING)).getAxis() == direction.rotateYClockwise().getAxis()
				? BlockRenderLayer.MIDDLE_POLE
				: BlockRenderLayer.UNDEFINED;
		} else {
			return BlockRenderLayer.UNDEFINED;
		}
	}
}
