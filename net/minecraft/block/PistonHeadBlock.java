package net.minecraft.block;

import java.util.Random;
import net.minecraft.block.enums.PistonType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.states.property.Properties;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shapes.VoxelShape;
import net.minecraft.util.shapes.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.IWorld;
import net.minecraft.world.RenderBlockView;
import net.minecraft.world.World;

public class PistonHeadBlock extends FacingBlock {
	public static final EnumProperty<PistonType> field_18667 = Properties.PISTON_TYPE;
	public static final BooleanProperty field_18668 = Properties.SHORT;
	protected static final VoxelShape field_18669 = Block.createCuboidShape(12.0, 0.0, 0.0, 16.0, 16.0, 16.0);
	protected static final VoxelShape field_18670 = Block.createCuboidShape(0.0, 0.0, 0.0, 4.0, 16.0, 16.0);
	protected static final VoxelShape field_18671 = Block.createCuboidShape(0.0, 0.0, 12.0, 16.0, 16.0, 16.0);
	protected static final VoxelShape field_18672 = Block.createCuboidShape(0.0, 0.0, 0.0, 16.0, 16.0, 4.0);
	protected static final VoxelShape field_18673 = Block.createCuboidShape(0.0, 12.0, 0.0, 16.0, 16.0, 16.0);
	protected static final VoxelShape field_18674 = Block.createCuboidShape(0.0, 0.0, 0.0, 16.0, 4.0, 16.0);
	protected static final VoxelShape field_18675 = Block.createCuboidShape(6.0, -4.0, 6.0, 10.0, 12.0, 10.0);
	protected static final VoxelShape field_18676 = Block.createCuboidShape(6.0, 4.0, 6.0, 10.0, 20.0, 10.0);
	protected static final VoxelShape field_18677 = Block.createCuboidShape(6.0, 6.0, -4.0, 10.0, 10.0, 12.0);
	protected static final VoxelShape field_18678 = Block.createCuboidShape(6.0, 6.0, 4.0, 10.0, 10.0, 20.0);
	protected static final VoxelShape field_18679 = Block.createCuboidShape(-4.0, 6.0, 6.0, 12.0, 10.0, 10.0);
	protected static final VoxelShape field_18680 = Block.createCuboidShape(4.0, 6.0, 6.0, 20.0, 10.0, 10.0);
	protected static final VoxelShape field_18661 = Block.createCuboidShape(6.0, 0.0, 6.0, 10.0, 12.0, 10.0);
	protected static final VoxelShape field_18662 = Block.createCuboidShape(6.0, 4.0, 6.0, 10.0, 16.0, 10.0);
	protected static final VoxelShape field_18663 = Block.createCuboidShape(6.0, 6.0, 0.0, 10.0, 10.0, 12.0);
	protected static final VoxelShape field_18664 = Block.createCuboidShape(6.0, 6.0, 4.0, 10.0, 10.0, 16.0);
	protected static final VoxelShape field_18665 = Block.createCuboidShape(0.0, 6.0, 6.0, 12.0, 10.0, 10.0);
	protected static final VoxelShape field_18666 = Block.createCuboidShape(4.0, 6.0, 6.0, 16.0, 10.0, 10.0);

	public PistonHeadBlock(Block.Builder builder) {
		super(builder);
		this.setDefaultState(
			this.stateManager
				.method_16923()
				.withProperty(FACING, Direction.NORTH)
				.withProperty(field_18667, PistonType.DEFAULT)
				.withProperty(field_18668, Boolean.valueOf(false))
		);
	}

	private VoxelShape method_16851(BlockState blockState) {
		switch ((Direction)blockState.getProperty(FACING)) {
			case DOWN:
			default:
				return field_18674;
			case UP:
				return field_18673;
			case NORTH:
				return field_18672;
			case SOUTH:
				return field_18671;
			case WEST:
				return field_18670;
			case EAST:
				return field_18669;
		}
	}

	@Override
	public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos) {
		return VoxelShapes.union(this.method_16851(state), this.method_16852(state));
	}

	private VoxelShape method_16852(BlockState blockState) {
		boolean bl = (Boolean)blockState.getProperty(field_18668);
		switch ((Direction)blockState.getProperty(FACING)) {
			case DOWN:
			default:
				return bl ? field_18662 : field_18676;
			case UP:
				return bl ? field_18661 : field_18675;
			case NORTH:
				return bl ? field_18664 : field_18678;
			case SOUTH:
				return bl ? field_18663 : field_18677;
			case WEST:
				return bl ? field_18666 : field_18680;
			case EAST:
				return bl ? field_18665 : field_18679;
		}
	}

	@Override
	public boolean method_11568(BlockState state) {
		return state.getProperty(FACING) == Direction.UP;
	}

	@Override
	public void onBreakByPlayer(World world, BlockPos pos, BlockState state, PlayerEntity player) {
		if (!world.isClient && player.abilities.creativeMode) {
			BlockPos blockPos = pos.offset(((Direction)state.getProperty(FACING)).getOpposite());
			Block block = world.getBlockState(blockPos).getBlock();
			if (block == Blocks.PISTON || block == Blocks.STICKY_PISTON) {
				world.method_8553(blockPos);
			}
		}

		super.onBreakByPlayer(world, pos, state, player);
	}

	@Override
	public void onStateReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean moved) {
		if (state.getBlock() != newState.getBlock()) {
			super.onStateReplaced(state, world, pos, newState, moved);
			Direction direction = ((Direction)state.getProperty(FACING)).getOpposite();
			pos = pos.offset(direction);
			BlockState blockState = world.getBlockState(pos);
			if ((blockState.getBlock() == Blocks.PISTON || blockState.getBlock() == Blocks.STICKY_PISTON) && (Boolean)blockState.getProperty(PistonBlock.field_18654)) {
				blockState.method_16867(world, pos, 0);
				world.method_8553(pos);
			}
		}
	}

	@Override
	public boolean method_11562(BlockState state) {
		return false;
	}

	@Override
	public int getDropCount(BlockState state, Random random) {
		return 0;
	}

	@Override
	public BlockState getStateForNeighborUpdate(BlockState state, Direction direction, BlockState neighborState, IWorld world, BlockPos pos, BlockPos neighborPos) {
		return direction.getOpposite() == state.getProperty(FACING) && !state.canPlaceAt(world, pos)
			? Blocks.AIR.getDefaultState()
			: super.getStateForNeighborUpdate(state, direction, neighborState, world, pos, neighborPos);
	}

	@Override
	public boolean canPlaceAt(BlockState state, RenderBlockView world, BlockPos pos) {
		Block block = world.getBlockState(pos.offset(((Direction)state.getProperty(FACING)).getOpposite())).getBlock();
		return block == Blocks.PISTON || block == Blocks.STICKY_PISTON || block == Blocks.MOVING_PISTON;
	}

	@Override
	public void neighborUpdate(BlockState state, World world, BlockPos pos, Block block, BlockPos neighborPos) {
		if (state.canPlaceAt(world, pos)) {
			BlockPos blockPos = pos.offset(((Direction)state.getProperty(FACING)).getOpposite());
			world.getBlockState(blockPos).neighborUpdate(world, blockPos, block, neighborPos);
		}
	}

	@Override
	public ItemStack getPickBlock(BlockView world, BlockPos pos, BlockState state) {
		return new ItemStack(state.getProperty(field_18667) == PistonType.STICKY ? Blocks.STICKY_PISTON : Blocks.PISTON);
	}

	@Override
	public BlockState withRotation(BlockState state, BlockRotation rotation) {
		return state.withProperty(FACING, rotation.rotate(state.getProperty(FACING)));
	}

	@Override
	public BlockState withMirror(BlockState state, BlockMirror mirror) {
		return state.rotate(mirror.getRotation(state.getProperty(FACING)));
	}

	@Override
	protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
		builder.method_16928(FACING, field_18667, field_18668);
	}

	@Override
	public BlockRenderLayer getRenderLayer(BlockView world, BlockState state, BlockPos pos, Direction direction) {
		return direction == state.getProperty(FACING) ? BlockRenderLayer.SOLID : BlockRenderLayer.UNDEFINED;
	}

	@Override
	public boolean canPlaceAtSide(BlockState state, BlockView world, BlockPos pos, BlockPlacementEnvironment environment) {
		return false;
	}
}
