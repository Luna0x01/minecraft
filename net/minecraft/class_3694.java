package net.minecraft;

import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.block.BlockRenderLayer;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.FluidDrainable;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.sound.SoundCategory;
import net.minecraft.entity.Entity;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.Sounds;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.states.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockView;
import net.minecraft.world.IWorld;
import net.minecraft.world.RenderBlockView;
import net.minecraft.world.World;

public class class_3694 extends Block implements FluidDrainable {
	public static final BooleanProperty field_18200 = Properties.DRAG;

	public class_3694(Block.Builder builder) {
		super(builder);
		this.setDefaultState(this.stateManager.method_16923().withProperty(field_18200, Boolean.valueOf(true)));
	}

	@Override
	public boolean method_11562(BlockState state) {
		return false;
	}

	@Override
	public void onEntityCollision(BlockState state, World world, BlockPos pos, Entity entity) {
		BlockState blockState = world.getBlockState(pos.up());
		if (blockState.isAir()) {
			entity.method_15593((Boolean)state.getProperty(field_18200));
			if (!world.isClient) {
				ServerWorld serverWorld = (ServerWorld)world;

				for (int i = 0; i < 2; i++) {
					serverWorld.method_21261(
						class_4342.field_21368,
						(double)((float)pos.getX() + world.random.nextFloat()),
						(double)(pos.getY() + 1),
						(double)((float)pos.getZ() + world.random.nextFloat()),
						1,
						0.0,
						0.0,
						0.0,
						1.0
					);
					serverWorld.method_21261(
						class_4342.field_21379,
						(double)((float)pos.getX() + world.random.nextFloat()),
						(double)(pos.getY() + 1),
						(double)((float)pos.getZ() + world.random.nextFloat()),
						1,
						0.0,
						0.01,
						0.0,
						0.2
					);
				}
			}
		} else {
			entity.method_15594((Boolean)state.getProperty(field_18200));
		}
	}

	@Override
	public void onBlockAdded(BlockState state, World world, BlockPos pos, BlockState oldState) {
		method_16630(world, pos.up(), method_16628(world, pos.down()));
	}

	@Override
	public void scheduledTick(BlockState state, World world, BlockPos pos, Random random) {
		method_16630(world, pos.up(), method_16628(world, pos));
	}

	@Override
	public FluidState getFluidState(BlockState state) {
		return Fluids.WATER.getStill(false);
	}

	public static void method_16630(IWorld iWorld, BlockPos blockPos, boolean bl) {
		if (method_16629(iWorld, blockPos)) {
			iWorld.setBlockState(blockPos, Blocks.BUBBLE_COLUMN.getDefaultState().withProperty(field_18200, Boolean.valueOf(bl)), 2);
		}
	}

	public static boolean method_16629(IWorld iWorld, BlockPos blockPos) {
		FluidState fluidState = iWorld.getFluidState(blockPos);
		return iWorld.getBlockState(blockPos).getBlock() == Blocks.WATER && fluidState.method_17811() >= 8 && fluidState.isStill();
	}

	private static boolean method_16628(BlockView blockView, BlockPos blockPos) {
		BlockState blockState = blockView.getBlockState(blockPos);
		Block block = blockState.getBlock();
		return block == Blocks.BUBBLE_COLUMN ? (Boolean)blockState.getProperty(field_18200) : block != Blocks.SOULSAND;
	}

	@Override
	public int getTickDelay(RenderBlockView world) {
		return 5;
	}

	@Override
	public void randomDisplayTick(BlockState state, World world, BlockPos pos, Random random) {
		double d = (double)pos.getX();
		double e = (double)pos.getY();
		double f = (double)pos.getZ();
		if ((Boolean)state.getProperty(field_18200)) {
			world.method_16333(class_4342.field_21371, d + 0.5, e + 0.8, f, 0.0, 0.0, 0.0);
			if (random.nextInt(200) == 0) {
				world.playSound(
					d, e, f, Sounds.BLOCK_BUBBLE_COLUMN_WHIRLPOOL_AMBIENT, SoundCategory.BLOCKS, 0.2F + random.nextFloat() * 0.2F, 0.9F + random.nextFloat() * 0.15F, false
				);
			}
		} else {
			world.method_16333(class_4342.field_21380, d + 0.5, e, f + 0.5, 0.0, 0.04, 0.0);
			world.method_16333(class_4342.field_21380, d + (double)random.nextFloat(), e + (double)random.nextFloat(), f + (double)random.nextFloat(), 0.0, 0.04, 0.0);
			if (random.nextInt(200) == 0) {
				world.playSound(
					d, e, f, Sounds.BLOCK_BUBBLE_COLUMN_UPWARDS_AMBIENT, SoundCategory.BLOCKS, 0.2F + random.nextFloat() * 0.2F, 0.9F + random.nextFloat() * 0.15F, false
				);
			}
		}
	}

	@Override
	public BlockState getStateForNeighborUpdate(BlockState state, Direction direction, BlockState neighborState, IWorld world, BlockPos pos, BlockPos neighborPos) {
		if (!state.canPlaceAt(world, pos)) {
			return Blocks.WATER.getDefaultState();
		} else {
			if (direction == Direction.DOWN) {
				world.setBlockState(pos, Blocks.BUBBLE_COLUMN.getDefaultState().withProperty(field_18200, Boolean.valueOf(method_16628(world, neighborPos))), 2);
			} else if (direction == Direction.UP && neighborState.getBlock() != Blocks.BUBBLE_COLUMN && method_16629(world, neighborPos)) {
				world.getBlockTickScheduler().schedule(pos, this, this.getTickDelay(world));
			}

			world.method_16340().schedule(pos, Fluids.WATER, Fluids.WATER.method_17778(world));
			return super.getStateForNeighborUpdate(state, direction, neighborState, world, pos, neighborPos);
		}
	}

	@Override
	public boolean canPlaceAt(BlockState state, RenderBlockView world, BlockPos pos) {
		Block block = world.getBlockState(pos.down()).getBlock();
		return block == Blocks.BUBBLE_COLUMN || block == Blocks.MAGMA_BLOCK || block == Blocks.SOULSAND;
	}

	@Override
	public boolean hasCollision() {
		return false;
	}

	@Override
	public int getDropCount(BlockState state, Random random) {
		return 0;
	}

	@Override
	public RenderLayer getRenderLayerType() {
		return RenderLayer.TRANSLUCENT;
	}

	@Override
	public BlockRenderLayer getRenderLayer(BlockView world, BlockState state, BlockPos pos, Direction direction) {
		return BlockRenderLayer.UNDEFINED;
	}

	@Override
	public BlockRenderType getRenderType(BlockState state) {
		return BlockRenderType.INVISIBLE;
	}

	@Override
	protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
		builder.method_16928(field_18200);
	}

	@Override
	public Fluid tryDrainFluid(IWorld world, BlockPos pos, BlockState state) {
		world.setBlockState(pos, Blocks.AIR.getDefaultState(), 11);
		return Fluids.WATER;
	}
}
