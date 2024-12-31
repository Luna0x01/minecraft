package net.minecraft.block;

import java.util.Random;
import net.minecraft.client.particle.ParticleType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.IntProperty;
import net.minecraft.util.CommonI18n;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

public class RepeaterBlock extends AbstractRedstoneGateBlock {
	public static final BooleanProperty LOCKED = BooleanProperty.of("locked");
	public static final IntProperty DELAY = IntProperty.of("delay", 1, 4);

	protected RepeaterBlock(boolean bl) {
		super(bl);
		this.setDefaultState(this.stateManager.getDefaultState().with(FACING, Direction.NORTH).with(DELAY, 1).with(LOCKED, false));
	}

	@Override
	public String getTranslatedName() {
		return CommonI18n.translate("item.diode.name");
	}

	@Override
	public BlockState getBlockState(BlockState state, BlockView view, BlockPos pos) {
		return state.with(LOCKED, this.isLocked(view, pos, state));
	}

	@Override
	public boolean onUse(World world, BlockPos pos, BlockState state, PlayerEntity player, Direction direction, float posX, float posY, float posZ) {
		if (!player.abilities.allowModifyWorld) {
			return false;
		} else {
			world.setBlockState(pos, state.withDefaultValue(DELAY), 3);
			return true;
		}
	}

	@Override
	protected int getUpdateDelayInternal(BlockState state) {
		return (Integer)state.get(DELAY) * 2;
	}

	@Override
	protected BlockState getPoweredState(BlockState state) {
		Integer integer = state.get(DELAY);
		Boolean boolean_ = state.get(LOCKED);
		Direction direction = state.get(FACING);
		return Blocks.POWERED_REPEATER.getDefaultState().with(FACING, direction).with(DELAY, integer).with(LOCKED, boolean_);
	}

	@Override
	protected BlockState getUnpoweredState(BlockState state) {
		Integer integer = state.get(DELAY);
		Boolean boolean_ = state.get(LOCKED);
		Direction direction = state.get(FACING);
		return Blocks.UNPOWERED_REPEATER.getDefaultState().with(FACING, direction).with(DELAY, integer).with(LOCKED, boolean_);
	}

	@Override
	public Item getDropItem(BlockState state, Random random, int id) {
		return Items.REPEATER;
	}

	@Override
	public Item getPickItem(World world, BlockPos pos) {
		return Items.REPEATER;
	}

	@Override
	public boolean isLocked(BlockView view, BlockPos pos, BlockState state) {
		return this.getMaxInputLevelSides(view, pos, state) > 0;
	}

	@Override
	protected boolean emitsRedstonePower(Block block) {
		return isRedstoneGate(block);
	}

	@Override
	public void randomDisplayTick(World world, BlockPos pos, BlockState state, Random rand) {
		if (this.powered) {
			Direction direction = state.get(FACING);
			double d = (double)((float)pos.getX() + 0.5F) + (double)(rand.nextFloat() - 0.5F) * 0.2;
			double e = (double)((float)pos.getY() + 0.4F) + (double)(rand.nextFloat() - 0.5F) * 0.2;
			double f = (double)((float)pos.getZ() + 0.5F) + (double)(rand.nextFloat() - 0.5F) * 0.2;
			float g = -5.0F;
			if (rand.nextBoolean()) {
				g = (float)((Integer)state.get(DELAY) * 2 - 1);
			}

			g /= 16.0F;
			double h = (double)(g * (float)direction.getOffsetX());
			double i = (double)(g * (float)direction.getOffsetZ());
			world.addParticle(ParticleType.REDSTONE, d + h, e, f + i, 0.0, 0.0, 0.0);
		}
	}

	@Override
	public void onBreaking(World world, BlockPos pos, BlockState state) {
		super.onBreaking(world, pos, state);
		this.updateTarget(world, pos, state);
	}

	@Override
	public BlockState stateFromData(int data) {
		return this.getDefaultState().with(FACING, Direction.fromHorizontal(data)).with(LOCKED, false).with(DELAY, 1 + (data >> 2));
	}

	@Override
	public int getData(BlockState state) {
		int i = 0;
		i |= ((Direction)state.get(FACING)).getHorizontal();
		return i | (Integer)state.get(DELAY) - 1 << 2;
	}

	@Override
	protected StateManager appendProperties() {
		return new StateManager(this, FACING, DELAY, LOCKED);
	}
}
