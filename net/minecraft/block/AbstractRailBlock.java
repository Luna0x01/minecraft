package net.minecraft.block;

import net.minecraft.class_3716;
import net.minecraft.block.enums.RailShape;
import net.minecraft.block.piston.PistonBehavior;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.state.property.Property;
import net.minecraft.tag.BlockTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shapes.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.RenderBlockView;
import net.minecraft.world.World;

public abstract class AbstractRailBlock extends Block {
	protected static final VoxelShape STRAIGHT_SHAPE = Block.createCuboidShape(0.0, 0.0, 0.0, 16.0, 2.0, 16.0);
	protected static final VoxelShape ASCENDING_SHAPE = Block.createCuboidShape(0.0, 0.0, 0.0, 16.0, 8.0, 16.0);
	private final boolean forbidCurves;

	public static boolean isRail(World world, BlockPos pos) {
		return isRail(world.getBlockState(pos));
	}

	public static boolean isRail(BlockState state) {
		return state.isIn(BlockTags.RAILS);
	}

	protected AbstractRailBlock(boolean bl, Block.Builder builder) {
		super(builder);
		this.forbidCurves = bl;
	}

	public boolean cannotMakeCurves() {
		return this.forbidCurves;
	}

	@Override
	public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos) {
		RailShape railShape = state.getBlock() == this ? state.getProperty(this.getShapeProperty()) : null;
		return railShape != null && railShape.isAscending() ? ASCENDING_SHAPE : STRAIGHT_SHAPE;
	}

	@Override
	public BlockRenderLayer getRenderLayer(BlockView world, BlockState state, BlockPos pos, Direction direction) {
		return BlockRenderLayer.UNDEFINED;
	}

	@Override
	public boolean method_11562(BlockState state) {
		return false;
	}

	@Override
	public boolean canPlaceAt(BlockState state, RenderBlockView world, BlockPos pos) {
		return world.getBlockState(pos.down()).method_16913();
	}

	@Override
	public void onBlockAdded(BlockState state, World world, BlockPos pos, BlockState oldState) {
		if (oldState.getBlock() != state.getBlock()) {
			if (!world.isClient) {
				state = this.updateBlockState(world, pos, state, true);
				if (this.forbidCurves) {
					state.neighborUpdate(world, pos, this, pos);
				}
			}
		}
	}

	@Override
	public void neighborUpdate(BlockState state, World world, BlockPos pos, Block block, BlockPos neighborPos) {
		if (!world.isClient) {
			RailShape railShape = state.getProperty(this.getShapeProperty());
			boolean bl = false;
			if (!world.getBlockState(pos.down()).method_16913()) {
				bl = true;
			}

			if (railShape == RailShape.ASCENDING_EAST && !world.getBlockState(pos.east()).method_16913()) {
				bl = true;
			} else if (railShape == RailShape.ASCENDING_WEST && !world.getBlockState(pos.west()).method_16913()) {
				bl = true;
			} else if (railShape == RailShape.ASCENDING_NORTH && !world.getBlockState(pos.north()).method_16913()) {
				bl = true;
			} else if (railShape == RailShape.ASCENDING_SOUTH && !world.getBlockState(pos.south()).method_16913()) {
				bl = true;
			}

			if (bl && !world.method_8579(pos)) {
				state.method_16866(world, pos, 1.0F, 0);
				world.method_8553(pos);
			} else {
				this.updateBlockState(state, world, pos, block);
			}
		}
	}

	protected void updateBlockState(BlockState state, World world, BlockPos pos, Block neighbor) {
	}

	protected BlockState updateBlockState(World world, BlockPos pos, BlockState state, boolean forceUpdate) {
		return world.isClient ? state : new class_3716(world, pos, state).method_16718(world.isReceivingRedstonePower(pos), forceUpdate).method_16722();
	}

	@Override
	public PistonBehavior getPistonBehavior(BlockState state) {
		return PistonBehavior.NORMAL;
	}

	@Override
	public RenderLayer getRenderLayerType() {
		return RenderLayer.CUTOUT;
	}

	@Override
	public void onStateReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean moved) {
		if (!moved) {
			super.onStateReplaced(state, world, pos, newState, moved);
			if (((RailShape)state.getProperty(this.getShapeProperty())).isAscending()) {
				world.updateNeighborsAlways(pos.up(), this);
			}

			if (this.forbidCurves) {
				world.updateNeighborsAlways(pos, this);
				world.updateNeighborsAlways(pos.down(), this);
			}
		}
	}

	public abstract Property<RailShape> getShapeProperty();
}
