package net.minecraft.block;

import java.util.Random;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.particle.DustParticleEffect;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.IntProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.WorldView;

public class RepeaterBlock extends AbstractRedstoneGateBlock {
	public static final BooleanProperty LOCKED = Properties.LOCKED;
	public static final IntProperty DELAY = Properties.DELAY;

	protected RepeaterBlock(AbstractBlock.Settings settings) {
		super(settings);
		this.setDefaultState(
			this.stateManager
				.getDefaultState()
				.with(FACING, Direction.NORTH)
				.with(DELAY, Integer.valueOf(1))
				.with(LOCKED, Boolean.valueOf(false))
				.with(POWERED, Boolean.valueOf(false))
		);
	}

	@Override
	public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
		if (!player.getAbilities().allowModifyWorld) {
			return ActionResult.PASS;
		} else {
			world.setBlockState(pos, state.cycle(DELAY), 3);
			return ActionResult.success(world.isClient);
		}
	}

	@Override
	protected int getUpdateDelayInternal(BlockState state) {
		return (Integer)state.get(DELAY) * 2;
	}

	@Override
	public BlockState getPlacementState(ItemPlacementContext ctx) {
		BlockState blockState = super.getPlacementState(ctx);
		return blockState.with(LOCKED, Boolean.valueOf(this.isLocked(ctx.getWorld(), ctx.getBlockPos(), blockState)));
	}

	@Override
	public BlockState getStateForNeighborUpdate(
		BlockState state, Direction direction, BlockState neighborState, WorldAccess world, BlockPos pos, BlockPos neighborPos
	) {
		return !world.isClient() && direction.getAxis() != ((Direction)state.get(FACING)).getAxis()
			? state.with(LOCKED, Boolean.valueOf(this.isLocked(world, pos, state)))
			: super.getStateForNeighborUpdate(state, direction, neighborState, world, pos, neighborPos);
	}

	@Override
	public boolean isLocked(WorldView world, BlockPos pos, BlockState state) {
		return this.getMaxInputLevelSides(world, pos, state) > 0;
	}

	@Override
	protected boolean isValidInput(BlockState state) {
		return isRedstoneGate(state);
	}

	@Override
	public void randomDisplayTick(BlockState state, World world, BlockPos pos, Random random) {
		if ((Boolean)state.get(POWERED)) {
			Direction direction = state.get(FACING);
			double d = (double)pos.getX() + 0.5 + (random.nextDouble() - 0.5) * 0.2;
			double e = (double)pos.getY() + 0.4 + (random.nextDouble() - 0.5) * 0.2;
			double f = (double)pos.getZ() + 0.5 + (random.nextDouble() - 0.5) * 0.2;
			float g = -5.0F;
			if (random.nextBoolean()) {
				g = (float)((Integer)state.get(DELAY) * 2 - 1);
			}

			g /= 16.0F;
			double h = (double)(g * (float)direction.getOffsetX());
			double i = (double)(g * (float)direction.getOffsetZ());
			world.addParticle(DustParticleEffect.DEFAULT, d + h, e, f + i, 0.0, 0.0, 0.0);
		}
	}

	@Override
	protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
		builder.add(FACING, DELAY, LOCKED, POWERED);
	}
}
