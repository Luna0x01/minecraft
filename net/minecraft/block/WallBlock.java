package net.minecraft.block;

import net.minecraft.class_3703;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.states.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shapes.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.IWorld;
import net.minecraft.world.RenderBlockView;

public class WallBlock extends class_3703 {
	public static final BooleanProperty field_18576 = Properties.UP;
	private final VoxelShape[] field_18577;
	private final VoxelShape[] field_18578;

	public WallBlock(Block.Builder builder) {
		super(0.0F, 3.0F, 0.0F, 14.0F, 24.0F, builder);
		this.setDefaultState(
			this.stateManager
				.method_16923()
				.withProperty(field_18576, Boolean.valueOf(true))
				.withProperty(field_18265, Boolean.valueOf(false))
				.withProperty(field_18266, Boolean.valueOf(false))
				.withProperty(field_18267, Boolean.valueOf(false))
				.withProperty(field_18268, Boolean.valueOf(false))
				.withProperty(field_18269, Boolean.valueOf(false))
		);
		this.field_18577 = this.method_16656(4.0F, 3.0F, 16.0F, 0.0F, 14.0F);
		this.field_18578 = this.method_16656(4.0F, 3.0F, 24.0F, 0.0F, 24.0F);
	}

	@Override
	public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos) {
		return state.getProperty(field_18576) ? this.field_18577[this.method_16659(state)] : super.getOutlineShape(state, world, pos);
	}

	@Override
	public VoxelShape getCollisionShape(BlockState state, BlockView world, BlockPos pos) {
		return state.getProperty(field_18576) ? this.field_18578[this.method_16659(state)] : super.getCollisionShape(state, world, pos);
	}

	@Override
	public boolean method_11562(BlockState state) {
		return false;
	}

	@Override
	public boolean canPlaceAtSide(BlockState state, BlockView world, BlockPos pos, BlockPlacementEnvironment environment) {
		return false;
	}

	private boolean method_16768(BlockState blockState, BlockRenderLayer blockRenderLayer) {
		Block block = blockState.getBlock();
		boolean bl = blockRenderLayer == BlockRenderLayer.MIDDLE_POLE_THICK || blockRenderLayer == BlockRenderLayer.MIDDLE_POLE && block instanceof FenceGateBlock;
		return !method_14353(block) && blockRenderLayer == BlockRenderLayer.SOLID || bl;
	}

	public static boolean method_14353(Block block) {
		return Block.method_14309(block)
			|| block == Blocks.BARRIER
			|| block == Blocks.MELON_BLOCK
			|| block == Blocks.PUMPKIN
			|| block == Blocks.CARVED_PUMPKIN
			|| block == Blocks.JACK_O_LANTERN
			|| block == Blocks.FROSTED_ICE
			|| block == Blocks.TNT;
	}

	@Override
	public BlockState getPlacementState(ItemPlacementContext context) {
		RenderBlockView renderBlockView = context.getWorld();
		BlockPos blockPos = context.getBlockPos();
		FluidState fluidState = context.getWorld().getFluidState(context.getBlockPos());
		BlockPos blockPos2 = blockPos.north();
		BlockPos blockPos3 = blockPos.east();
		BlockPos blockPos4 = blockPos.south();
		BlockPos blockPos5 = blockPos.west();
		BlockState blockState = renderBlockView.getBlockState(blockPos2);
		BlockState blockState2 = renderBlockView.getBlockState(blockPos3);
		BlockState blockState3 = renderBlockView.getBlockState(blockPos4);
		BlockState blockState4 = renderBlockView.getBlockState(blockPos5);
		boolean bl = this.method_16768(blockState, blockState.getRenderLayer(renderBlockView, blockPos2, Direction.SOUTH));
		boolean bl2 = this.method_16768(blockState2, blockState2.getRenderLayer(renderBlockView, blockPos3, Direction.WEST));
		boolean bl3 = this.method_16768(blockState3, blockState3.getRenderLayer(renderBlockView, blockPos4, Direction.NORTH));
		boolean bl4 = this.method_16768(blockState4, blockState4.getRenderLayer(renderBlockView, blockPos5, Direction.EAST));
		boolean bl5 = (!bl || bl2 || !bl3 || bl4) && (bl || !bl2 || bl3 || !bl4);
		return this.getDefaultState()
			.withProperty(field_18576, Boolean.valueOf(bl5 || !renderBlockView.method_8579(blockPos.up())))
			.withProperty(field_18265, Boolean.valueOf(bl))
			.withProperty(field_18266, Boolean.valueOf(bl2))
			.withProperty(field_18267, Boolean.valueOf(bl3))
			.withProperty(field_18268, Boolean.valueOf(bl4))
			.withProperty(field_18269, Boolean.valueOf(fluidState.getFluid() == Fluids.WATER));
	}

	@Override
	public BlockState getStateForNeighborUpdate(BlockState state, Direction direction, BlockState neighborState, IWorld world, BlockPos pos, BlockPos neighborPos) {
		if ((Boolean)state.getProperty(field_18269)) {
			world.method_16340().schedule(pos, Fluids.WATER, Fluids.WATER.method_17778(world));
		}

		if (direction == Direction.DOWN) {
			return super.getStateForNeighborUpdate(state, direction, neighborState, world, pos, neighborPos);
		} else {
			boolean bl = direction == Direction.NORTH
				? this.method_16768(neighborState, neighborState.getRenderLayer(world, neighborPos, direction.getOpposite()))
				: (Boolean)state.getProperty(field_18265);
			boolean bl2 = direction == Direction.EAST
				? this.method_16768(neighborState, neighborState.getRenderLayer(world, neighborPos, direction.getOpposite()))
				: (Boolean)state.getProperty(field_18266);
			boolean bl3 = direction == Direction.SOUTH
				? this.method_16768(neighborState, neighborState.getRenderLayer(world, neighborPos, direction.getOpposite()))
				: (Boolean)state.getProperty(field_18267);
			boolean bl4 = direction == Direction.WEST
				? this.method_16768(neighborState, neighborState.getRenderLayer(world, neighborPos, direction.getOpposite()))
				: (Boolean)state.getProperty(field_18268);
			boolean bl5 = (!bl || bl2 || !bl3 || bl4) && (bl || !bl2 || bl3 || !bl4);
			return state.withProperty(field_18576, Boolean.valueOf(bl5 || !world.method_8579(pos.up())))
				.withProperty(field_18265, Boolean.valueOf(bl))
				.withProperty(field_18266, Boolean.valueOf(bl2))
				.withProperty(field_18267, Boolean.valueOf(bl3))
				.withProperty(field_18268, Boolean.valueOf(bl4));
		}
	}

	@Override
	protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
		builder.method_16928(field_18576, field_18265, field_18266, field_18268, field_18267, field_18269);
	}

	@Override
	public BlockRenderLayer getRenderLayer(BlockView world, BlockState state, BlockPos pos, Direction direction) {
		return direction != Direction.UP && direction != Direction.DOWN ? BlockRenderLayer.MIDDLE_POLE_THICK : BlockRenderLayer.CENTER_BIG;
	}
}
