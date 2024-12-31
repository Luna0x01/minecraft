package net.minecraft;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import java.util.List;
import java.util.Map;
import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.block.BlockPlacementEnvironment;
import net.minecraft.block.BlockRenderLayer;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.FluidDrainable;
import net.minecraft.client.sound.SoundCategory;
import net.minecraft.fluid.FlowableFluid;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.Itemable;
import net.minecraft.item.Items;
import net.minecraft.sound.Sounds;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.IntProperty;
import net.minecraft.states.property.Properties;
import net.minecraft.tag.FluidTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shapes.VoxelShape;
import net.minecraft.util.shapes.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.IWorld;
import net.minecraft.world.RenderBlockView;
import net.minecraft.world.World;

public class class_3710 extends Block implements FluidDrainable {
	public static final IntProperty field_18402 = Properties.LEVEL_15;
	protected final FlowableFluid field_18403;
	private final List<FluidState> field_18404;
	private final Map<BlockState, VoxelShape> field_18405 = Maps.newIdentityHashMap();

	protected class_3710(FlowableFluid flowableFluid, Block.Builder builder) {
		super(builder);
		this.field_18403 = flowableFluid;
		this.field_18404 = Lists.newArrayList();
		this.field_18404.add(flowableFluid.getStill(false));

		for (int i = 1; i < 8; i++) {
			this.field_18404.add(flowableFluid.method_17744(8 - i, false));
		}

		this.field_18404.add(flowableFluid.method_17744(8, true));
		this.setDefaultState(this.stateManager.method_16923().withProperty(field_18402, Integer.valueOf(0)));
	}

	@Override
	public void method_16582(BlockState blockState, World world, BlockPos blockPos, Random random) {
		world.getFluidState(blockPos).method_17806(world, blockPos, random);
	}

	@Override
	public boolean isTranslucent(BlockState state, BlockView world, BlockPos pos) {
		return false;
	}

	@Override
	public boolean canPlaceAtSide(BlockState state, BlockView world, BlockPos pos, BlockPlacementEnvironment environment) {
		return !this.field_18403.method_17786(FluidTags.LAVA);
	}

	@Override
	public FluidState getFluidState(BlockState state) {
		int i = (Integer)state.getProperty(field_18402);
		return (FluidState)this.field_18404.get(Math.min(i, 8));
	}

	@Override
	public boolean method_11562(BlockState state) {
		return false;
	}

	@Override
	public boolean method_400(BlockState blockState) {
		return false;
	}

	@Override
	public boolean method_16573(BlockState blockState, BlockState blockState2, Direction direction) {
		return blockState2.getFluidState().getFluid().method_17781(this.field_18403) ? true : super.isFullBoundsCubeForCulling(blockState);
	}

	@Override
	public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos) {
		FluidState fluidState = world.getFluidState(pos.up());
		return fluidState.getFluid().method_17781(this.field_18403)
			? VoxelShapes.matchesAnywhere()
			: (VoxelShape)this.field_18405.computeIfAbsent(state, blockState -> {
				FluidState fluidStatex = blockState.getFluidState();
				return VoxelShapes.cuboid(0.0, 0.0, 0.0, 1.0, (double)fluidStatex.method_17810(), 1.0);
			});
	}

	@Override
	public BlockRenderType getRenderType(BlockState state) {
		return BlockRenderType.INVISIBLE;
	}

	@Override
	public Itemable getDroppedItem(BlockState state, World world, BlockPos pos, int fortuneLevel) {
		return Items.AIR;
	}

	@Override
	public int getTickDelay(RenderBlockView world) {
		return this.field_18403.method_17778(world);
	}

	@Override
	public void onBlockAdded(BlockState state, World world, BlockPos pos, BlockState oldState) {
		if (this.method_16697(world, pos, state)) {
			world.method_16340().schedule(pos, state.getFluidState().getFluid(), this.getTickDelay(world));
		}
	}

	@Override
	public BlockState getStateForNeighborUpdate(BlockState state, Direction direction, BlockState neighborState, IWorld world, BlockPos pos, BlockPos neighborPos) {
		if (state.getFluidState().isStill() || neighborState.getFluidState().isStill()) {
			world.method_16340().schedule(pos, state.getFluidState().getFluid(), this.getTickDelay(world));
		}

		return super.getStateForNeighborUpdate(state, direction, neighborState, world, pos, neighborPos);
	}

	@Override
	public void neighborUpdate(BlockState state, World world, BlockPos pos, Block block, BlockPos neighborPos) {
		if (this.method_16697(world, pos, state)) {
			world.method_16340().schedule(pos, state.getFluidState().getFluid(), this.getTickDelay(world));
		}
	}

	public boolean method_16697(World world, BlockPos blockPos, BlockState blockState) {
		if (this.field_18403.method_17786(FluidTags.LAVA)) {
			boolean bl = false;

			for (Direction direction : Direction.values()) {
				if (direction != Direction.DOWN && world.getFluidState(blockPos.offset(direction)).matches(FluidTags.WATER)) {
					bl = true;
					break;
				}
			}

			if (bl) {
				FluidState fluidState = world.getFluidState(blockPos);
				if (fluidState.isStill()) {
					world.setBlockState(blockPos, Blocks.OBSIDIAN.getDefaultState());
					this.method_16698(world, blockPos);
					return false;
				}

				if (fluidState.method_17810() >= 0.44444445F) {
					world.setBlockState(blockPos, Blocks.COBBLESTONE.getDefaultState());
					this.method_16698(world, blockPos);
					return false;
				}
			}
		}

		return true;
	}

	protected void method_16698(IWorld iWorld, BlockPos blockPos) {
		double d = (double)blockPos.getX();
		double e = (double)blockPos.getY();
		double f = (double)blockPos.getZ();
		iWorld.playSound(
			null, blockPos, Sounds.BLOCK_LAVA_EXTINGUISH, SoundCategory.BLOCKS, 0.5F, 2.6F + (iWorld.getRandom().nextFloat() - iWorld.getRandom().nextFloat()) * 0.8F
		);

		for (int i = 0; i < 8; i++) {
			iWorld.method_16343(class_4342.field_21356, d + Math.random(), e + 1.2, f + Math.random(), 0.0, 0.0, 0.0);
		}
	}

	@Override
	protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
		builder.method_16928(field_18402);
	}

	@Override
	public BlockRenderLayer getRenderLayer(BlockView world, BlockState state, BlockPos pos, Direction direction) {
		return BlockRenderLayer.UNDEFINED;
	}

	@Override
	public Fluid tryDrainFluid(IWorld world, BlockPos pos, BlockState state) {
		if ((Integer)state.getProperty(field_18402) == 0) {
			world.setBlockState(pos, Blocks.AIR.getDefaultState(), 11);
			return this.field_18403;
		} else {
			return Fluids.EMPTY;
		}
	}
}
