package net.minecraft.block;

import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.IntProperty;
import net.minecraft.states.property.Properties;
import net.minecraft.tag.BlockTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shapes.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.IWorld;
import net.minecraft.world.RenderBlockView;
import net.minecraft.world.World;

public class CocoaBlock extends HorizontalFacingBlock implements Growable {
	public static final IntProperty AGE = Properties.AGE_2;
	protected static final VoxelShape[] AGE_TO_EAST_SHAPE = new VoxelShape[]{
		Block.createCuboidShape(11.0, 7.0, 6.0, 15.0, 12.0, 10.0),
		Block.createCuboidShape(9.0, 5.0, 5.0, 15.0, 12.0, 11.0),
		Block.createCuboidShape(7.0, 3.0, 4.0, 15.0, 12.0, 12.0)
	};
	protected static final VoxelShape[] AGE_TO_WEST_SHAPE = new VoxelShape[]{
		Block.createCuboidShape(1.0, 7.0, 6.0, 5.0, 12.0, 10.0),
		Block.createCuboidShape(1.0, 5.0, 5.0, 7.0, 12.0, 11.0),
		Block.createCuboidShape(1.0, 3.0, 4.0, 9.0, 12.0, 12.0)
	};
	protected static final VoxelShape[] AGE_TO_NORTH_SHAPE = new VoxelShape[]{
		Block.createCuboidShape(6.0, 7.0, 1.0, 10.0, 12.0, 5.0),
		Block.createCuboidShape(5.0, 5.0, 1.0, 11.0, 12.0, 7.0),
		Block.createCuboidShape(4.0, 3.0, 1.0, 12.0, 12.0, 9.0)
	};
	protected static final VoxelShape[] AGE_TO_SOUTH_SHAPE = new VoxelShape[]{
		Block.createCuboidShape(6.0, 7.0, 11.0, 10.0, 12.0, 15.0),
		Block.createCuboidShape(5.0, 5.0, 9.0, 11.0, 12.0, 15.0),
		Block.createCuboidShape(4.0, 3.0, 7.0, 12.0, 12.0, 15.0)
	};

	public CocoaBlock(Block.Builder builder) {
		super(builder);
		this.setDefaultState(this.stateManager.method_16923().withProperty(FACING, Direction.NORTH).withProperty(AGE, Integer.valueOf(0)));
	}

	@Override
	public void scheduledTick(BlockState state, World world, BlockPos pos, Random random) {
		if (world.random.nextInt(5) == 0) {
			int i = (Integer)state.getProperty(AGE);
			if (i < 2) {
				world.setBlockState(pos, state.withProperty(AGE, Integer.valueOf(i + 1)), 2);
			}
		}
	}

	@Override
	public boolean canPlaceAt(BlockState state, RenderBlockView world, BlockPos pos) {
		Block block = world.getBlockState(pos.offset(state.getProperty(FACING))).getBlock();
		return block.isIn(BlockTags.JUNGLE_LOGS);
	}

	@Override
	public boolean method_11562(BlockState state) {
		return false;
	}

	@Override
	public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos) {
		int i = (Integer)state.getProperty(AGE);
		switch ((Direction)state.getProperty(FACING)) {
			case SOUTH:
				return AGE_TO_SOUTH_SHAPE[i];
			case NORTH:
			default:
				return AGE_TO_NORTH_SHAPE[i];
			case WEST:
				return AGE_TO_WEST_SHAPE[i];
			case EAST:
				return AGE_TO_EAST_SHAPE[i];
		}
	}

	@Nullable
	@Override
	public BlockState getPlacementState(ItemPlacementContext context) {
		BlockState blockState = this.getDefaultState();
		RenderBlockView renderBlockView = context.getWorld();
		BlockPos blockPos = context.getBlockPos();

		for (Direction direction : context.method_16021()) {
			if (direction.getAxis().isHorizontal()) {
				blockState = blockState.withProperty(FACING, direction);
				if (blockState.canPlaceAt(renderBlockView, blockPos)) {
					return blockState;
				}
			}
		}

		return null;
	}

	@Override
	public BlockState getStateForNeighborUpdate(BlockState state, Direction direction, BlockState neighborState, IWorld world, BlockPos pos, BlockPos neighborPos) {
		return direction == state.getProperty(FACING) && !state.canPlaceAt(world, pos)
			? Blocks.AIR.getDefaultState()
			: super.getStateForNeighborUpdate(state, direction, neighborState, world, pos, neighborPos);
	}

	@Override
	public void method_410(BlockState blockState, World world, BlockPos blockPos, float f, int i) {
		int j = (Integer)blockState.getProperty(AGE);
		int k = 1;
		if (j >= 2) {
			k = 3;
		}

		for (int l = 0; l < k; l++) {
			onBlockBreak(world, blockPos, new ItemStack(Items.COCOA_BEANS));
		}
	}

	@Override
	public ItemStack getPickBlock(BlockView world, BlockPos pos, BlockState state) {
		return new ItemStack(Items.COCOA_BEANS);
	}

	@Override
	public boolean isFertilizable(BlockView world, BlockPos pos, BlockState state, boolean isClient) {
		return (Integer)state.getProperty(AGE) < 2;
	}

	@Override
	public boolean canBeFertilized(World world, Random random, BlockPos pos, BlockState state) {
		return true;
	}

	@Override
	public void grow(World world, Random random, BlockPos pos, BlockState state) {
		world.setBlockState(pos, state.withProperty(AGE, Integer.valueOf((Integer)state.getProperty(AGE) + 1)), 2);
	}

	@Override
	public RenderLayer getRenderLayerType() {
		return RenderLayer.CUTOUT;
	}

	@Override
	protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
		builder.method_16928(FACING, AGE);
	}

	@Override
	public BlockRenderLayer getRenderLayer(BlockView world, BlockState state, BlockPos pos, Direction direction) {
		return BlockRenderLayer.UNDEFINED;
	}
}
