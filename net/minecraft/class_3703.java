package net.minecraft;

import java.util.Map;
import net.minecraft.block.Block;
import net.minecraft.block.BlockPlacementEnvironment;
import net.minecraft.block.BlockState;
import net.minecraft.block.ConnectingBlock;
import net.minecraft.block.FluidDrainable;
import net.minecraft.block.FluidFillable;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.states.property.Properties;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shapes.VoxelShape;
import net.minecraft.util.shapes.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.IWorld;

public class class_3703 extends Block implements FluidDrainable, FluidFillable {
	public static final BooleanProperty field_18265 = ConnectingBlock.NORTH;
	public static final BooleanProperty field_18266 = ConnectingBlock.EAST;
	public static final BooleanProperty field_18267 = ConnectingBlock.SOUTH;
	public static final BooleanProperty field_18268 = ConnectingBlock.WEST;
	public static final BooleanProperty field_18269 = Properties.WATERLOGGED;
	protected static final Map<Direction, BooleanProperty> field_18270 = (Map<Direction, BooleanProperty>)ConnectingBlock.FACING_TO_PROPERTY
		.entrySet()
		.stream()
		.filter(entry -> ((Direction)entry.getKey()).getAxis().isHorizontal())
		.collect(Util.method_20218());
	protected final VoxelShape[] field_18271;
	protected final VoxelShape[] field_18272;

	protected class_3703(float f, float g, float h, float i, float j, Block.Builder builder) {
		super(builder);
		this.field_18271 = this.method_16656(f, g, j, 0.0F, j);
		this.field_18272 = this.method_16656(f, g, h, 0.0F, i);
	}

	protected VoxelShape[] method_16656(float f, float g, float h, float i, float j) {
		float k = 8.0F - f;
		float l = 8.0F + f;
		float m = 8.0F - g;
		float n = 8.0F + g;
		VoxelShape voxelShape = Block.createCuboidShape((double)k, 0.0, (double)k, (double)l, (double)h, (double)l);
		VoxelShape voxelShape2 = Block.createCuboidShape((double)m, (double)i, 0.0, (double)n, (double)j, (double)n);
		VoxelShape voxelShape3 = Block.createCuboidShape((double)m, (double)i, (double)m, (double)n, (double)j, 16.0);
		VoxelShape voxelShape4 = Block.createCuboidShape(0.0, (double)i, (double)m, (double)n, (double)j, (double)n);
		VoxelShape voxelShape5 = Block.createCuboidShape((double)m, (double)i, (double)m, 16.0, (double)j, (double)n);
		VoxelShape voxelShape6 = VoxelShapes.union(voxelShape2, voxelShape5);
		VoxelShape voxelShape7 = VoxelShapes.union(voxelShape3, voxelShape4);
		VoxelShape[] voxelShapes = new VoxelShape[]{
			VoxelShapes.empty(),
			voxelShape3,
			voxelShape4,
			voxelShape7,
			voxelShape2,
			VoxelShapes.union(voxelShape3, voxelShape2),
			VoxelShapes.union(voxelShape4, voxelShape2),
			VoxelShapes.union(voxelShape7, voxelShape2),
			voxelShape5,
			VoxelShapes.union(voxelShape3, voxelShape5),
			VoxelShapes.union(voxelShape4, voxelShape5),
			VoxelShapes.union(voxelShape7, voxelShape5),
			voxelShape6,
			VoxelShapes.union(voxelShape3, voxelShape6),
			VoxelShapes.union(voxelShape4, voxelShape6),
			VoxelShapes.union(voxelShape7, voxelShape6)
		};

		for (int o = 0; o < 16; o++) {
			voxelShapes[o] = VoxelShapes.union(voxelShape, voxelShapes[o]);
		}

		return voxelShapes;
	}

	@Override
	public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos) {
		return this.field_18272[this.method_16659(state)];
	}

	@Override
	public VoxelShape getCollisionShape(BlockState state, BlockView world, BlockPos pos) {
		return this.field_18271[this.method_16659(state)];
	}

	private static int method_16657(Direction direction) {
		return 1 << direction.getHorizontal();
	}

	protected int method_16659(BlockState blockState) {
		int i = 0;
		if ((Boolean)blockState.getProperty(field_18265)) {
			i |= method_16657(Direction.NORTH);
		}

		if ((Boolean)blockState.getProperty(field_18266)) {
			i |= method_16657(Direction.EAST);
		}

		if ((Boolean)blockState.getProperty(field_18267)) {
			i |= method_16657(Direction.SOUTH);
		}

		if ((Boolean)blockState.getProperty(field_18268)) {
			i |= method_16657(Direction.WEST);
		}

		return i;
	}

	@Override
	public Fluid tryDrainFluid(IWorld world, BlockPos pos, BlockState state) {
		if ((Boolean)state.getProperty(field_18269)) {
			world.setBlockState(pos, state.withProperty(field_18269, Boolean.valueOf(false)), 3);
			return Fluids.WATER;
		} else {
			return Fluids.EMPTY;
		}
	}

	@Override
	public FluidState getFluidState(BlockState state) {
		return state.getProperty(field_18269) ? Fluids.WATER.getStill(false) : super.getFluidState(state);
	}

	@Override
	public boolean canFillWithFluid(BlockView world, BlockPos pos, BlockState state, Fluid fluid) {
		return !(Boolean)state.getProperty(field_18269) && fluid == Fluids.WATER;
	}

	@Override
	public boolean tryFillWithFluid(IWorld world, BlockPos pos, BlockState state, FluidState fluidState) {
		if (!(Boolean)state.getProperty(field_18269) && fluidState.getFluid() == Fluids.WATER) {
			if (!world.method_16390()) {
				world.setBlockState(pos, state.withProperty(field_18269, Boolean.valueOf(true)), 3);
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

	@Override
	public BlockState withRotation(BlockState state, BlockRotation rotation) {
		switch (rotation) {
			case CLOCKWISE_180:
				return state.withProperty(field_18265, state.getProperty(field_18267))
					.withProperty(field_18266, state.getProperty(field_18268))
					.withProperty(field_18267, state.getProperty(field_18265))
					.withProperty(field_18268, state.getProperty(field_18266));
			case COUNTERCLOCKWISE_90:
				return state.withProperty(field_18265, state.getProperty(field_18266))
					.withProperty(field_18266, state.getProperty(field_18267))
					.withProperty(field_18267, state.getProperty(field_18268))
					.withProperty(field_18268, state.getProperty(field_18265));
			case CLOCKWISE_90:
				return state.withProperty(field_18265, state.getProperty(field_18268))
					.withProperty(field_18266, state.getProperty(field_18265))
					.withProperty(field_18267, state.getProperty(field_18266))
					.withProperty(field_18268, state.getProperty(field_18267));
			default:
				return state;
		}
	}

	@Override
	public BlockState withMirror(BlockState state, BlockMirror mirror) {
		switch (mirror) {
			case LEFT_RIGHT:
				return state.withProperty(field_18265, state.getProperty(field_18267)).withProperty(field_18267, state.getProperty(field_18265));
			case FRONT_BACK:
				return state.withProperty(field_18266, state.getProperty(field_18268)).withProperty(field_18268, state.getProperty(field_18266));
			default:
				return super.withMirror(state, mirror);
		}
	}
}
