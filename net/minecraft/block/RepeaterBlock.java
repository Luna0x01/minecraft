package net.minecraft.block;

import java.util.Random;
import net.minecraft.class_4338;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.IntProperty;
import net.minecraft.states.property.Properties;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.IWorld;
import net.minecraft.world.RenderBlockView;
import net.minecraft.world.World;

public class RepeaterBlock extends AbstractRedstoneGateBlock {
	public static final BooleanProperty field_18457 = Properties.LOCKED;
	public static final IntProperty field_18458 = Properties.DELAY;

	protected RepeaterBlock(Block.Builder builder) {
		super(builder);
		this.setDefaultState(
			this.stateManager
				.method_16923()
				.withProperty(FACING, Direction.NORTH)
				.withProperty(field_18458, Integer.valueOf(1))
				.withProperty(field_18457, Boolean.valueOf(false))
				.withProperty(POWERED, Boolean.valueOf(false))
		);
	}

	@Override
	public boolean onUse(
		BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, Direction direction, float distanceX, float distanceY, float distanceZ
	) {
		if (!player.abilities.allowModifyWorld) {
			return false;
		} else {
			world.setBlockState(pos, state.method_16930(field_18458), 3);
			return true;
		}
	}

	@Override
	protected int getUpdateDelayInternal(BlockState state) {
		return (Integer)state.getProperty(field_18458) * 2;
	}

	@Override
	public BlockState getPlacementState(ItemPlacementContext context) {
		BlockState blockState = super.getPlacementState(context);
		return blockState.withProperty(field_18457, Boolean.valueOf(this.isLocked(context.getWorld(), context.getBlockPos(), blockState)));
	}

	@Override
	public BlockState getStateForNeighborUpdate(BlockState state, Direction direction, BlockState neighborState, IWorld world, BlockPos pos, BlockPos neighborPos) {
		return !world.method_16390() && direction.getAxis() != ((Direction)state.getProperty(FACING)).getAxis()
			? state.withProperty(field_18457, Boolean.valueOf(this.isLocked(world, pos, state)))
			: super.getStateForNeighborUpdate(state, direction, neighborState, world, pos, neighborPos);
	}

	@Override
	public boolean isLocked(RenderBlockView world, BlockPos pos, BlockState state) {
		return this.getMaxInputLevelSides(world, pos, state) > 0;
	}

	@Override
	protected boolean stateEmitRedstonePower(BlockState state) {
		return isRedstoneGateBlock(state);
	}

	@Override
	public void randomDisplayTick(BlockState state, World world, BlockPos pos, Random random) {
		if ((Boolean)state.getProperty(POWERED)) {
			Direction direction = state.getProperty(FACING);
			double d = (double)((float)pos.getX() + 0.5F) + (double)(random.nextFloat() - 0.5F) * 0.2;
			double e = (double)((float)pos.getY() + 0.4F) + (double)(random.nextFloat() - 0.5F) * 0.2;
			double f = (double)((float)pos.getZ() + 0.5F) + (double)(random.nextFloat() - 0.5F) * 0.2;
			float g = -5.0F;
			if (random.nextBoolean()) {
				g = (float)((Integer)state.getProperty(field_18458) * 2 - 1);
			}

			g /= 16.0F;
			double h = (double)(g * (float)direction.getOffsetX());
			double i = (double)(g * (float)direction.getOffsetZ());
			world.method_16343(class_4338.field_21339, d + h, e, f + i, 0.0, 0.0, 0.0);
		}
	}

	@Override
	protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
		builder.method_16928(FACING, field_18458, field_18457, POWERED);
	}
}
