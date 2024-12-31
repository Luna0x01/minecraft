package net.minecraft.block;

import java.util.Random;
import java.util.stream.IntStream;
import net.minecraft.class_3600;
import net.minecraft.block.enums.BlockHalf;
import net.minecraft.block.enums.StairShape;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.states.property.Properties;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shapes.VoxelShape;
import net.minecraft.util.shapes.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.IWorld;
import net.minecraft.world.RenderBlockView;
import net.minecraft.world.World;
import net.minecraft.world.explosion.Explosion;

public class StairsBlock extends Block implements FluidDrainable, FluidFillable {
	public static final DirectionProperty FACING = HorizontalFacingBlock.FACING;
	public static final EnumProperty<BlockHalf> field_18503 = Properties.BLOCK_HALF;
	public static final EnumProperty<StairShape> field_18504 = Properties.STAIR_SHAPE;
	public static final BooleanProperty field_18505 = Properties.WATERLOGGED;
	protected static final VoxelShape field_18506 = SlabBlock.field_18489;
	protected static final VoxelShape field_18507 = SlabBlock.field_18488;
	protected static final VoxelShape field_18508 = Block.createCuboidShape(0.0, 0.0, 0.0, 8.0, 8.0, 8.0);
	protected static final VoxelShape field_18509 = Block.createCuboidShape(0.0, 0.0, 8.0, 8.0, 8.0, 16.0);
	protected static final VoxelShape field_18510 = Block.createCuboidShape(0.0, 8.0, 0.0, 8.0, 16.0, 8.0);
	protected static final VoxelShape field_18511 = Block.createCuboidShape(0.0, 8.0, 8.0, 8.0, 16.0, 16.0);
	protected static final VoxelShape field_18512 = Block.createCuboidShape(8.0, 0.0, 0.0, 16.0, 8.0, 8.0);
	protected static final VoxelShape field_18513 = Block.createCuboidShape(8.0, 0.0, 8.0, 16.0, 8.0, 16.0);
	protected static final VoxelShape field_18514 = Block.createCuboidShape(8.0, 8.0, 0.0, 16.0, 16.0, 8.0);
	protected static final VoxelShape field_18515 = Block.createCuboidShape(8.0, 8.0, 8.0, 16.0, 16.0, 16.0);
	protected static final VoxelShape[] field_18516 = method_16742(field_18506, field_18508, field_18512, field_18509, field_18513);
	protected static final VoxelShape[] field_18501 = method_16742(field_18507, field_18510, field_18514, field_18511, field_18515);
	private static final int[] field_18502 = new int[]{12, 5, 3, 10, 14, 13, 7, 11, 13, 7, 11, 14, 8, 4, 1, 2, 4, 1, 2, 8};
	private final Block block;
	private final BlockState state;

	private static VoxelShape[] method_16742(VoxelShape voxelShape, VoxelShape voxelShape2, VoxelShape voxelShape3, VoxelShape voxelShape4, VoxelShape voxelShape5) {
		return (VoxelShape[])IntStream.range(0, 16)
			.mapToObj(i -> method_16741(i, voxelShape, voxelShape2, voxelShape3, voxelShape4, voxelShape5))
			.toArray(VoxelShape[]::new);
	}

	private static VoxelShape method_16741(
		int i, VoxelShape voxelShape, VoxelShape voxelShape2, VoxelShape voxelShape3, VoxelShape voxelShape4, VoxelShape voxelShape5
	) {
		VoxelShape voxelShape6 = voxelShape;
		if ((i & 1) != 0) {
			voxelShape6 = VoxelShapes.union(voxelShape, voxelShape2);
		}

		if ((i & 2) != 0) {
			voxelShape6 = VoxelShapes.union(voxelShape6, voxelShape3);
		}

		if ((i & 4) != 0) {
			voxelShape6 = VoxelShapes.union(voxelShape6, voxelShape4);
		}

		if ((i & 8) != 0) {
			voxelShape6 = VoxelShapes.union(voxelShape6, voxelShape5);
		}

		return voxelShape6;
	}

	protected StairsBlock(BlockState blockState, Block.Builder builder) {
		super(builder);
		this.setDefaultState(
			this.stateManager
				.method_16923()
				.withProperty(FACING, Direction.NORTH)
				.withProperty(field_18503, BlockHalf.BOTTOM)
				.withProperty(field_18504, StairShape.STRAIGHT)
				.withProperty(field_18505, Boolean.valueOf(false))
		);
		this.block = blockState.getBlock();
		this.state = blockState;
	}

	@Override
	public int getLightSubtracted(BlockState state, BlockView world, BlockPos pos) {
		return world.getMaxLightLevel();
	}

	@Override
	public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos) {
		return (state.getProperty(field_18503) == BlockHalf.TOP ? field_18516 : field_18501)[field_18502[this.method_16745(state)]];
	}

	private int method_16745(BlockState blockState) {
		return ((StairShape)blockState.getProperty(field_18504)).ordinal() * 4 + ((Direction)blockState.getProperty(FACING)).getHorizontal();
	}

	@Override
	public BlockRenderLayer getRenderLayer(BlockView world, BlockState state, BlockPos pos, Direction direction) {
		if (direction.getAxis() == Direction.Axis.Y) {
			return direction == Direction.UP == (state.getProperty(field_18503) == BlockHalf.TOP) ? BlockRenderLayer.SOLID : BlockRenderLayer.UNDEFINED;
		} else {
			StairShape stairShape = state.getProperty(field_18504);
			if (stairShape != StairShape.OUTER_LEFT && stairShape != StairShape.OUTER_RIGHT) {
				Direction direction2 = state.getProperty(FACING);
				switch (stairShape) {
					case STRAIGHT:
						return direction2 == direction ? BlockRenderLayer.SOLID : BlockRenderLayer.UNDEFINED;
					case INNER_LEFT:
						return direction2 != direction && direction2 != direction.rotateYClockwise() ? BlockRenderLayer.UNDEFINED : BlockRenderLayer.SOLID;
					case INNER_RIGHT:
						return direction2 != direction && direction2 != direction.rotateYCounterclockwise() ? BlockRenderLayer.UNDEFINED : BlockRenderLayer.SOLID;
					default:
						return BlockRenderLayer.UNDEFINED;
				}
			} else {
				return BlockRenderLayer.UNDEFINED;
			}
		}
	}

	@Override
	public boolean method_11562(BlockState state) {
		return false;
	}

	@Override
	public void randomDisplayTick(BlockState state, World world, BlockPos pos, Random random) {
		this.block.randomDisplayTick(state, world, pos, random);
	}

	@Override
	public void method_420(BlockState blockState, World world, BlockPos blockPos, PlayerEntity playerEntity) {
		this.state.method_16870(world, blockPos, playerEntity);
	}

	@Override
	public void method_8674(IWorld iWorld, BlockPos blockPos, BlockState blockState) {
		this.block.method_8674(iWorld, blockPos, blockState);
	}

	@Override
	public int method_11564(BlockState blockState, class_3600 arg, BlockPos blockPos) {
		return this.state.method_16878(arg, blockPos);
	}

	@Override
	public float getBlastResistance() {
		return this.block.getBlastResistance();
	}

	@Override
	public RenderLayer getRenderLayerType() {
		return this.block.getRenderLayerType();
	}

	@Override
	public int getTickDelay(RenderBlockView world) {
		return this.block.getTickDelay(world);
	}

	@Override
	public boolean hasCollision() {
		return this.block.hasCollision();
	}

	@Override
	public boolean method_400(BlockState blockState) {
		return this.block.method_400(blockState);
	}

	@Override
	public void onBlockAdded(BlockState state, World world, BlockPos pos, BlockState oldState) {
		if (state.getBlock() != state.getBlock()) {
			this.state.neighborUpdate(world, pos, Blocks.AIR, pos);
			this.block.onBlockAdded(this.state, world, pos, oldState);
		}
	}

	@Override
	public void onStateReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean moved) {
		if (state.getBlock() != newState.getBlock()) {
			this.state.onStateReplaced(world, pos, newState, moved);
		}
	}

	@Override
	public void onSteppedOn(World world, BlockPos pos, Entity entity) {
		this.block.onSteppedOn(world, pos, entity);
	}

	@Override
	public void scheduledTick(BlockState state, World world, BlockPos pos, Random random) {
		this.block.scheduledTick(state, world, pos, random);
	}

	@Override
	public boolean onUse(
		BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, Direction direction, float distanceX, float distanceY, float distanceZ
	) {
		return this.state.onUse(world, pos, player, hand, Direction.DOWN, 0.0F, 0.0F, 0.0F);
	}

	@Override
	public void onDestroyedByExplosion(World world, BlockPos pos, Explosion explosion) {
		this.block.onDestroyedByExplosion(world, pos, explosion);
	}

	@Override
	public boolean method_11568(BlockState state) {
		return state.getProperty(field_18503) == BlockHalf.TOP;
	}

	@Override
	public BlockState getPlacementState(ItemPlacementContext context) {
		Direction direction = context.method_16151();
		FluidState fluidState = context.getWorld().getFluidState(context.getBlockPos());
		BlockState blockState = this.getDefaultState()
			.withProperty(FACING, context.method_16145())
			.withProperty(
				field_18503, direction != Direction.DOWN && (direction == Direction.UP || !((double)context.method_16153() > 0.5)) ? BlockHalf.BOTTOM : BlockHalf.TOP
			)
			.withProperty(field_18505, Boolean.valueOf(fluidState.getFluid() == Fluids.WATER));
		return blockState.withProperty(field_18504, method_11631(blockState, context.getWorld(), context.getBlockPos()));
	}

	@Override
	public BlockState getStateForNeighborUpdate(BlockState state, Direction direction, BlockState neighborState, IWorld world, BlockPos pos, BlockPos neighborPos) {
		if ((Boolean)state.getProperty(field_18505)) {
			world.method_16340().schedule(pos, Fluids.WATER, Fluids.WATER.method_17778(world));
		}

		return direction.getAxis().isHorizontal()
			? state.withProperty(field_18504, method_11631(state, world, pos))
			: super.getStateForNeighborUpdate(state, direction, neighborState, world, pos, neighborPos);
	}

	private static StairShape method_11631(BlockState blockState, BlockView blockView, BlockPos blockPos) {
		Direction direction = blockState.getProperty(FACING);
		BlockState blockState2 = blockView.getBlockState(blockPos.offset(direction));
		if (method_11633(blockState2) && blockState.getProperty(field_18503) == blockState2.getProperty(field_18503)) {
			Direction direction2 = blockState2.getProperty(FACING);
			if (direction2.getAxis() != ((Direction)blockState.getProperty(FACING)).getAxis() && method_11632(blockState, blockView, blockPos, direction2.getOpposite())
				)
			 {
				if (direction2 == direction.rotateYCounterclockwise()) {
					return StairShape.OUTER_LEFT;
				}

				return StairShape.OUTER_RIGHT;
			}
		}

		BlockState blockState3 = blockView.getBlockState(blockPos.offset(direction.getOpposite()));
		if (method_11633(blockState3) && blockState.getProperty(field_18503) == blockState3.getProperty(field_18503)) {
			Direction direction3 = blockState3.getProperty(FACING);
			if (direction3.getAxis() != ((Direction)blockState.getProperty(FACING)).getAxis() && method_11632(blockState, blockView, blockPos, direction3)) {
				if (direction3 == direction.rotateYCounterclockwise()) {
					return StairShape.INNER_LEFT;
				}

				return StairShape.INNER_RIGHT;
			}
		}

		return StairShape.STRAIGHT;
	}

	private static boolean method_11632(BlockState blockState, BlockView blockView, BlockPos blockPos, Direction direction) {
		BlockState blockState2 = blockView.getBlockState(blockPos.offset(direction));
		return !method_11633(blockState2)
			|| blockState2.getProperty(FACING) != blockState.getProperty(FACING)
			|| blockState2.getProperty(field_18503) != blockState.getProperty(field_18503);
	}

	public static boolean method_11633(BlockState blockState) {
		return blockState.getBlock() instanceof StairsBlock;
	}

	@Override
	public BlockState withRotation(BlockState state, BlockRotation rotation) {
		return state.withProperty(FACING, rotation.rotate(state.getProperty(FACING)));
	}

	@Override
	public BlockState withMirror(BlockState state, BlockMirror mirror) {
		Direction direction = state.getProperty(FACING);
		StairShape stairShape = state.getProperty(field_18504);
		switch (mirror) {
			case LEFT_RIGHT:
				if (direction.getAxis() == Direction.Axis.Z) {
					switch (stairShape) {
						case INNER_LEFT:
							return state.rotate(BlockRotation.CLOCKWISE_180).withProperty(field_18504, StairShape.INNER_RIGHT);
						case INNER_RIGHT:
							return state.rotate(BlockRotation.CLOCKWISE_180).withProperty(field_18504, StairShape.INNER_LEFT);
						case OUTER_LEFT:
							return state.rotate(BlockRotation.CLOCKWISE_180).withProperty(field_18504, StairShape.OUTER_RIGHT);
						case OUTER_RIGHT:
							return state.rotate(BlockRotation.CLOCKWISE_180).withProperty(field_18504, StairShape.OUTER_LEFT);
						default:
							return state.rotate(BlockRotation.CLOCKWISE_180);
					}
				}
				break;
			case FRONT_BACK:
				if (direction.getAxis() == Direction.Axis.X) {
					switch (stairShape) {
						case STRAIGHT:
							return state.rotate(BlockRotation.CLOCKWISE_180);
						case INNER_LEFT:
							return state.rotate(BlockRotation.CLOCKWISE_180).withProperty(field_18504, StairShape.INNER_LEFT);
						case INNER_RIGHT:
							return state.rotate(BlockRotation.CLOCKWISE_180).withProperty(field_18504, StairShape.INNER_RIGHT);
						case OUTER_LEFT:
							return state.rotate(BlockRotation.CLOCKWISE_180).withProperty(field_18504, StairShape.OUTER_RIGHT);
						case OUTER_RIGHT:
							return state.rotate(BlockRotation.CLOCKWISE_180).withProperty(field_18504, StairShape.OUTER_LEFT);
					}
				}
		}

		return super.withMirror(state, mirror);
	}

	@Override
	protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
		builder.method_16928(FACING, field_18503, field_18504, field_18505);
	}

	@Override
	public Fluid tryDrainFluid(IWorld world, BlockPos pos, BlockState state) {
		if ((Boolean)state.getProperty(field_18505)) {
			world.setBlockState(pos, state.withProperty(field_18505, Boolean.valueOf(false)), 3);
			return Fluids.WATER;
		} else {
			return Fluids.EMPTY;
		}
	}

	@Override
	public FluidState getFluidState(BlockState state) {
		return state.getProperty(field_18505) ? Fluids.WATER.getStill(false) : super.getFluidState(state);
	}

	@Override
	public boolean canFillWithFluid(BlockView world, BlockPos pos, BlockState state, Fluid fluid) {
		return !(Boolean)state.getProperty(field_18505) && fluid == Fluids.WATER;
	}

	@Override
	public boolean tryFillWithFluid(IWorld world, BlockPos pos, BlockState state, FluidState fluidState) {
		if (!(Boolean)state.getProperty(field_18505) && fluidState.getFluid() == Fluids.WATER) {
			if (!world.method_16390()) {
				world.setBlockState(pos, state.withProperty(field_18505, Boolean.valueOf(true)), 3);
				world.method_16340().schedule(pos, fluidState.getFluid(), fluidState.getFluid().method_17778(world));
			}

			return true;
		} else {
			return false;
		}
	}

	@Override
	public boolean canPlaceAtSide(BlockState state, BlockView world, BlockPos pos, BlockPlacementEnvironment environment) {
		return false;
	}
}
