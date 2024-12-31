package net.minecraft.block;

import java.util.Random;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.Itemable;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.IntProperty;
import net.minecraft.states.property.Properties;
import net.minecraft.tag.FluidTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shapes.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.IWorld;
import net.minecraft.world.RenderBlockView;
import net.minecraft.world.World;

public class FarmlandBlock extends Block {
	public static final IntProperty field_18317 = Properties.MOISTURE;
	protected static final VoxelShape field_18318 = Block.createCuboidShape(0.0, 0.0, 0.0, 16.0, 15.0, 16.0);

	protected FarmlandBlock(Block.Builder builder) {
		super(builder);
		this.setDefaultState(this.stateManager.method_16923().withProperty(field_18317, Integer.valueOf(0)));
	}

	@Override
	public BlockState getStateForNeighborUpdate(BlockState state, Direction direction, BlockState neighborState, IWorld world, BlockPos pos, BlockPos neighborPos) {
		if (direction == Direction.UP && !state.canPlaceAt(world, pos)) {
			world.getBlockTickScheduler().schedule(pos, this, 1);
		}

		return super.getStateForNeighborUpdate(state, direction, neighborState, world, pos, neighborPos);
	}

	@Override
	public boolean canPlaceAt(BlockState state, RenderBlockView world, BlockPos pos) {
		BlockState blockState = world.getBlockState(pos.up());
		return !blockState.getMaterial().isSolid() || blockState.getBlock() instanceof FenceGateBlock;
	}

	@Override
	public BlockState getPlacementState(ItemPlacementContext context) {
		return !this.getDefaultState().canPlaceAt(context.getWorld(), context.getBlockPos()) ? Blocks.DIRT.getDefaultState() : super.getPlacementState(context);
	}

	@Override
	public int getLightSubtracted(BlockState state, BlockView world, BlockPos pos) {
		return world.getMaxLightLevel();
	}

	@Override
	public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos) {
		return field_18318;
	}

	@Override
	public boolean method_11562(BlockState state) {
		return false;
	}

	@Override
	public void scheduledTick(BlockState state, World world, BlockPos pos, Random random) {
		if (!state.canPlaceAt(world, pos)) {
			method_16675(state, world, pos);
		} else {
			int i = (Integer)state.getProperty(field_18317);
			if (!method_16674(world, pos) && !world.hasRain(pos.up())) {
				if (i > 0) {
					world.setBlockState(pos, state.withProperty(field_18317, Integer.valueOf(i - 1)), 2);
				} else if (!method_16673(world, pos)) {
					method_16675(state, world, pos);
				}
			} else if (i < 7) {
				world.setBlockState(pos, state.withProperty(field_18317, Integer.valueOf(7)), 2);
			}
		}
	}

	@Override
	public void onLandedUpon(World world, BlockPos pos, Entity entity, float distance) {
		if (!world.isClient
			&& world.random.nextFloat() < distance - 0.5F
			&& entity instanceof LivingEntity
			&& (entity instanceof PlayerEntity || world.getGameRules().getBoolean("mobGriefing"))
			&& entity.width * entity.width * entity.height > 0.512F) {
			method_16675(world.getBlockState(pos), world, pos);
		}

		super.onLandedUpon(world, pos, entity, distance);
	}

	public static void method_16675(BlockState blockState, World world, BlockPos blockPos) {
		world.setBlockState(blockPos, pushEntitiesUpBeforeBlockChange(blockState, Blocks.DIRT.getDefaultState(), world, blockPos));
	}

	private static boolean method_16673(BlockView blockView, BlockPos blockPos) {
		Block block = blockView.getBlockState(blockPos.up()).getBlock();
		return block instanceof CropBlock || block instanceof StemBlock || block instanceof AttachedStemBlock;
	}

	private static boolean method_16674(RenderBlockView renderBlockView, BlockPos blockPos) {
		for (BlockPos.Mutable mutable : BlockPos.mutableIterate(blockPos.add(-4, 0, -4), blockPos.add(4, 1, 4))) {
			if (renderBlockView.getFluidState(mutable).matches(FluidTags.WATER)) {
				return true;
			}
		}

		return false;
	}

	@Override
	public Itemable getDroppedItem(BlockState state, World world, BlockPos pos, int fortuneLevel) {
		return Blocks.DIRT;
	}

	@Override
	protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
		builder.method_16928(field_18317);
	}

	@Override
	public BlockRenderLayer getRenderLayer(BlockView world, BlockState state, BlockPos pos, Direction direction) {
		return direction == Direction.DOWN ? BlockRenderLayer.SOLID : BlockRenderLayer.UNDEFINED;
	}

	@Override
	public boolean canPlaceAtSide(BlockState state, BlockView world, BlockPos pos, BlockPlacementEnvironment environment) {
		return false;
	}
}
