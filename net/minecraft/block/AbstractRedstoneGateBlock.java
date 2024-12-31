package net.minecraft.block;

import java.util.Random;
import net.minecraft.block.material.Material;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

public abstract class AbstractRedstoneGateBlock extends FacingBlock {
	protected final boolean powered;

	protected AbstractRedstoneGateBlock(boolean bl) {
		super(Material.DECORATION);
		this.powered = bl;
		this.setBoundingBox(0.0F, 0.0F, 0.0F, 1.0F, 0.125F, 1.0F);
	}

	@Override
	public boolean renderAsNormalBlock() {
		return false;
	}

	@Override
	public boolean canBePlacedAtPos(World world, BlockPos pos) {
		return World.isOpaque(world, pos.down()) ? super.canBePlacedAtPos(world, pos) : false;
	}

	public boolean isOnOpaqueBlock(World world, BlockPos pos) {
		return World.isOpaque(world, pos.down());
	}

	@Override
	public void onRandomTick(World world, BlockPos pos, BlockState state, Random rand) {
	}

	@Override
	public void onScheduledTick(World world, BlockPos pos, BlockState state, Random rand) {
		if (!this.isLocked(world, pos, state)) {
			boolean bl = this.hasPower(world, pos, state);
			if (this.powered && !bl) {
				world.setBlockState(pos, this.getUnpoweredState(state), 2);
			} else if (!this.powered) {
				world.setBlockState(pos, this.getPoweredState(state), 2);
				if (!bl) {
					world.createAndScheduleBlockTick(pos, this.getPoweredState(state).getBlock(), this.getUpdateDelay(state), -1);
				}
			}
		}
	}

	@Override
	public boolean isSideInvisible(BlockView view, BlockPos pos, Direction facing) {
		return facing.getAxis() != Direction.Axis.Y;
	}

	protected boolean isPowered(BlockState state) {
		return this.powered;
	}

	@Override
	public int getStrongRedstonePower(BlockView view, BlockPos pos, BlockState state, Direction facing) {
		return this.getWeakRedstonePower(view, pos, state, facing);
	}

	@Override
	public int getWeakRedstonePower(BlockView view, BlockPos pos, BlockState state, Direction facing) {
		if (!this.isPowered(state)) {
			return 0;
		} else {
			return state.get(FACING) == facing ? this.getOutputLevel(view, pos, state) : 0;
		}
	}

	@Override
	public void neighborUpdate(World world, BlockPos pos, BlockState state, Block block) {
		if (this.isOnOpaqueBlock(world, pos)) {
			this.updatePowered(world, pos, state);
		} else {
			this.dropAsItem(world, pos, state, 0);
			world.setAir(pos);

			for (Direction direction : Direction.values()) {
				world.updateNeighborsAlways(pos.offset(direction), this);
			}
		}
	}

	protected void updatePowered(World world, BlockPos pos, BlockState state) {
		if (!this.isLocked(world, pos, state)) {
			boolean bl = this.hasPower(world, pos, state);
			if ((this.powered && !bl || !this.powered && bl) && !world.hasScheduledTick(pos, this)) {
				int i = -1;
				if (this.isTargetNotAligned(world, pos, state)) {
					i = -3;
				} else if (this.powered) {
					i = -2;
				}

				world.createAndScheduleBlockTick(pos, this, this.getUpdateDelayInternal(state), i);
			}
		}
	}

	public boolean isLocked(BlockView view, BlockPos pos, BlockState state) {
		return false;
	}

	protected boolean hasPower(World world, BlockPos pos, BlockState state) {
		return this.getPower(world, pos, state) > 0;
	}

	protected int getPower(World world, BlockPos pos, BlockState state) {
		Direction direction = state.get(FACING);
		BlockPos blockPos = pos.offset(direction);
		int i = world.getEmittedRedstonePower(blockPos, direction);
		if (i >= 15) {
			return i;
		} else {
			BlockState blockState = world.getBlockState(blockPos);
			return Math.max(i, blockState.getBlock() == Blocks.REDSTONE_WIRE ? (Integer)blockState.get(RedstoneWireBlock.POWER) : 0);
		}
	}

	protected int getMaxInputLevelSides(BlockView view, BlockPos pos, BlockState state) {
		Direction direction = state.get(FACING);
		Direction direction2 = direction.rotateYClockwise();
		Direction direction3 = direction.rotateYCounterclockwise();
		return Math.max(this.getInputLevel(view, pos.offset(direction2), direction2), this.getInputLevel(view, pos.offset(direction3), direction3));
	}

	protected int getInputLevel(BlockView view, BlockPos pos, Direction dir) {
		BlockState blockState = view.getBlockState(pos);
		Block block = blockState.getBlock();
		if (this.emitsRedstonePower(block)) {
			return block == Blocks.REDSTONE_WIRE ? (Integer)blockState.get(RedstoneWireBlock.POWER) : view.getStrongRedstonePower(pos, dir);
		} else {
			return 0;
		}
	}

	@Override
	public boolean emitsRedstonePower() {
		return true;
	}

	@Override
	public BlockState getStateFromData(World world, BlockPos pos, Direction dir, float x, float y, float z, int id, LivingEntity entity) {
		return this.getDefaultState().with(FACING, entity.getHorizontalDirection().getOpposite());
	}

	@Override
	public void onPlaced(World world, BlockPos pos, BlockState state, LivingEntity placer, ItemStack itemStack) {
		if (this.hasPower(world, pos, state)) {
			world.createAndScheduleBlockTick(pos, this, 1);
		}
	}

	@Override
	public void onCreation(World world, BlockPos pos, BlockState state) {
		this.updateTarget(world, pos, state);
	}

	protected void updateTarget(World world, BlockPos pos, BlockState state) {
		Direction direction = state.get(FACING);
		BlockPos blockPos = pos.offset(direction.getOpposite());
		world.neighbourUpdate(blockPos, this);
		world.updateNeighborsExcept(blockPos, this, direction);
	}

	@Override
	public void onBreakByPlayer(World world, BlockPos pos, BlockState state) {
		if (this.powered) {
			for (Direction direction : Direction.values()) {
				world.updateNeighborsAlways(pos.offset(direction), this);
			}
		}

		super.onBreakByPlayer(world, pos, state);
	}

	@Override
	public boolean hasTransparency() {
		return false;
	}

	protected boolean emitsRedstonePower(Block block) {
		return block.emitsRedstonePower();
	}

	protected int getOutputLevel(BlockView world, BlockPos pos, BlockState state) {
		return 15;
	}

	public static boolean isRedstoneGate(Block block) {
		return Blocks.UNPOWERED_REPEATER.isComparator(block) || Blocks.UNPOWERED_COMPARATOR.isComparator(block);
	}

	public boolean isComparator(Block block) {
		return block == this.getPoweredState(this.getDefaultState()).getBlock() || block == this.getUnpoweredState(this.getDefaultState()).getBlock();
	}

	public boolean isTargetNotAligned(World world, BlockPos pos, BlockState state) {
		Direction direction = ((Direction)state.get(FACING)).getOpposite();
		BlockPos blockPos = pos.offset(direction);
		return isRedstoneGate(world.getBlockState(blockPos).getBlock()) ? world.getBlockState(blockPos).get(FACING) != direction : false;
	}

	protected int getUpdateDelay(BlockState state) {
		return this.getUpdateDelayInternal(state);
	}

	protected abstract int getUpdateDelayInternal(BlockState state);

	protected abstract BlockState getPoweredState(BlockState state);

	protected abstract BlockState getUnpoweredState(BlockState state);

	@Override
	public boolean isEqualTo(Block block) {
		return this.isComparator(block);
	}

	@Override
	public RenderLayer getRenderLayerType() {
		return RenderLayer.CUTOUT;
	}
}
