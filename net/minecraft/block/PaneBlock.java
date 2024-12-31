package net.minecraft.block;

import net.minecraft.class_3703;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.Property;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockView;
import net.minecraft.world.IWorld;

public class PaneBlock extends class_3703 {
	protected PaneBlock(Block.Builder builder) {
		super(1.0F, 1.0F, 16.0F, 16.0F, 16.0F, builder);
		this.setDefaultState(
			this.stateManager
				.method_16923()
				.withProperty(field_18265, Boolean.valueOf(false))
				.withProperty(field_18266, Boolean.valueOf(false))
				.withProperty(field_18267, Boolean.valueOf(false))
				.withProperty(field_18268, Boolean.valueOf(false))
				.withProperty(field_18269, Boolean.valueOf(false))
		);
	}

	@Override
	public BlockState getPlacementState(ItemPlacementContext context) {
		BlockView blockView = context.getWorld();
		BlockPos blockPos = context.getBlockPos();
		FluidState fluidState = context.getWorld().getFluidState(context.getBlockPos());
		BlockPos blockPos2 = blockPos.north();
		BlockPos blockPos3 = blockPos.south();
		BlockPos blockPos4 = blockPos.west();
		BlockPos blockPos5 = blockPos.east();
		BlockState blockState = blockView.getBlockState(blockPos2);
		BlockState blockState2 = blockView.getBlockState(blockPos3);
		BlockState blockState3 = blockView.getBlockState(blockPos4);
		BlockState blockState4 = blockView.getBlockState(blockPos5);
		return this.getDefaultState()
			.withProperty(field_18265, Boolean.valueOf(this.method_16688(blockState, blockState.getRenderLayer(blockView, blockPos2, Direction.SOUTH))))
			.withProperty(field_18267, Boolean.valueOf(this.method_16688(blockState2, blockState2.getRenderLayer(blockView, blockPos3, Direction.NORTH))))
			.withProperty(field_18268, Boolean.valueOf(this.method_16688(blockState3, blockState3.getRenderLayer(blockView, blockPos4, Direction.EAST))))
			.withProperty(field_18266, Boolean.valueOf(this.method_16688(blockState4, blockState4.getRenderLayer(blockView, blockPos5, Direction.WEST))))
			.withProperty(field_18269, Boolean.valueOf(fluidState.getFluid() == Fluids.WATER));
	}

	@Override
	public BlockState getStateForNeighborUpdate(BlockState state, Direction direction, BlockState neighborState, IWorld world, BlockPos pos, BlockPos neighborPos) {
		if ((Boolean)state.getProperty(field_18269)) {
			world.method_16340().schedule(pos, Fluids.WATER, Fluids.WATER.method_17778(world));
		}

		return direction.getAxis().isHorizontal()
			? state.withProperty(
				(Property)field_18270.get(direction),
				Boolean.valueOf(this.method_16688(neighborState, neighborState.getRenderLayer(world, neighborPos, direction.getOpposite())))
			)
			: super.getStateForNeighborUpdate(state, direction, neighborState, world, pos, neighborPos);
	}

	@Override
	public boolean method_11562(BlockState state) {
		return false;
	}

	@Override
	public boolean method_16573(BlockState blockState, BlockState blockState2, Direction direction) {
		if (blockState2.getBlock() == this) {
			if (!direction.getAxis().isHorizontal()) {
				return true;
			}

			if ((Boolean)blockState.getProperty((Property)field_18270.get(direction))
				&& (Boolean)blockState2.getProperty((Property)field_18270.get(direction.getOpposite()))) {
				return true;
			}
		}

		return super.method_16573(blockState, blockState2, direction);
	}

	public final boolean method_16688(BlockState blockState, BlockRenderLayer blockRenderLayer) {
		Block block = blockState.getBlock();
		return !method_14348(block) && blockRenderLayer == BlockRenderLayer.SOLID || blockRenderLayer == BlockRenderLayer.MIDDLE_POLE_THIN;
	}

	public static boolean method_14348(Block block) {
		return block instanceof ShulkerBoxBlock
			|| block instanceof LeavesBlock
			|| block == Blocks.BEACON
			|| block == Blocks.CAULDRON
			|| block == Blocks.GLOWSTONE
			|| block == Blocks.ICE
			|| block == Blocks.SEA_LANTERN
			|| block == Blocks.PISTON
			|| block == Blocks.STICKY_PISTON
			|| block == Blocks.PISTON_HEAD
			|| block == Blocks.MELON_BLOCK
			|| block == Blocks.PUMPKIN
			|| block == Blocks.CARVED_PUMPKIN
			|| block == Blocks.JACK_O_LANTERN
			|| block == Blocks.BARRIER;
	}

	@Override
	protected boolean requiresSilkTouch() {
		return true;
	}

	@Override
	public RenderLayer getRenderLayerType() {
		return RenderLayer.CUTOUT_MIPPED;
	}

	@Override
	protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
		builder.method_16928(field_18265, field_18266, field_18268, field_18267, field_18269);
	}

	@Override
	public BlockRenderLayer getRenderLayer(BlockView world, BlockState state, BlockPos pos, Direction direction) {
		return direction != Direction.UP && direction != Direction.DOWN ? BlockRenderLayer.MIDDLE_POLE_THIN : BlockRenderLayer.CENTER_SMALL;
	}
}
