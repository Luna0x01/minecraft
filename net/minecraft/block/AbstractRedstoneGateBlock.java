package net.minecraft.block;

import java.util.Random;
import net.minecraft.block.material.Material;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

public abstract class AbstractRedstoneGateBlock extends HorizontalFacingBlock {
	protected static final Box field_12644 = new Box(0.0, 0.0, 0.0, 1.0, 0.125, 1.0);
	protected final boolean powered;

	protected AbstractRedstoneGateBlock(boolean bl) {
		super(Material.DECORATION);
		this.powered = bl;
	}

	@Override
	public Box getCollisionBox(BlockState state, BlockView view, BlockPos pos) {
		return field_12644;
	}

	@Override
	public boolean method_11562(BlockState state) {
		return false;
	}

	@Override
	public boolean canBePlacedAtPos(World world, BlockPos pos) {
		return world.getBlockState(pos.down()).method_11739() ? super.canBePlacedAtPos(world, pos) : false;
	}

	public boolean isOnOpaqueBlock(World world, BlockPos pos) {
		return world.getBlockState(pos.down()).method_11739();
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
	public boolean method_8654(BlockState state, BlockView view, BlockPos pos, Direction direction) {
		return direction.getAxis() != Direction.Axis.Y;
	}

	protected boolean isPowered(BlockState state) {
		return this.powered;
	}

	@Override
	public int getStrongRedstonePower(BlockState state, BlockView world, BlockPos pos, Direction direction) {
		return state.getWeakRedstonePower(world, pos, direction);
	}

	@Override
	public int getWeakRedstonePower(BlockState state, BlockView world, BlockPos pos, Direction direction) {
		if (!this.isPowered(state)) {
			return 0;
		} else {
			return state.get(DIRECTION) == direction ? this.getOutputLevel(world, pos, state) : 0;
		}
	}

	@Override
	public void neighborUpdate(BlockState state, World world, BlockPos pos, Block block, BlockPos neighborPos) {
		if (this.isOnOpaqueBlock(world, pos)) {
			this.updatePowered(world, pos, state);
		} else {
			this.dropAsItem(world, pos, state, 0);
			world.setAir(pos);

			for (Direction direction : Direction.values()) {
				world.method_13692(pos.offset(direction), this, false);
			}
		}
	}

	protected void updatePowered(World world, BlockPos pos, BlockState state) {
		if (!this.isLocked(world, pos, state)) {
			boolean bl = this.hasPower(world, pos, state);
			if (this.powered != bl && !world.hasScheduledTick(pos, this)) {
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
		Direction direction = state.get(DIRECTION);
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
		Direction direction = state.get(DIRECTION);
		Direction direction2 = direction.rotateYClockwise();
		Direction direction3 = direction.rotateYCounterclockwise();
		return Math.max(this.getInputLevel(view, pos.offset(direction2), direction2), this.getInputLevel(view, pos.offset(direction3), direction3));
	}

	protected int getInputLevel(BlockView view, BlockPos pos, Direction dir) {
		BlockState blockState = view.getBlockState(pos);
		Block block = blockState.getBlock();
		if (this.stateEmitRedstonePower(blockState)) {
			if (block == Blocks.REDSTONE_BLOCK) {
				return 15;
			} else {
				return block == Blocks.REDSTONE_WIRE ? (Integer)blockState.get(RedstoneWireBlock.POWER) : view.getStrongRedstonePower(pos, dir);
			}
		} else {
			return 0;
		}
	}

	@Override
	public boolean emitsRedstonePower(BlockState state) {
		return true;
	}

	@Override
	public BlockState getStateFromData(World world, BlockPos pos, Direction dir, float x, float y, float z, int id, LivingEntity entity) {
		return this.getDefaultState().with(DIRECTION, entity.getHorizontalDirection().getOpposite());
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
		Direction direction = state.get(DIRECTION);
		BlockPos blockPos = pos.offset(direction.getOpposite());
		world.updateNeighbor(blockPos, this, pos);
		world.updateNeighborsExcept(blockPos, this, direction);
	}

	@Override
	public void onBreakByPlayer(World world, BlockPos pos, BlockState state) {
		if (this.powered) {
			for (Direction direction : Direction.values()) {
				world.method_13692(pos.offset(direction), this, false);
			}
		}

		super.onBreakByPlayer(world, pos, state);
	}

	@Override
	public boolean isFullBoundsCubeForCulling(BlockState blockState) {
		return false;
	}

	protected boolean stateEmitRedstonePower(BlockState state) {
		return state.emitsRedstonePower();
	}

	protected int getOutputLevel(BlockView world, BlockPos pos, BlockState state) {
		return 15;
	}

	public static boolean isRedstoneGateBlock(BlockState state) {
		return Blocks.UNPOWERED_REPEATER.method_11603(state) || Blocks.UNPOWERED_COMPARATOR.method_11603(state);
	}

	public boolean method_11603(BlockState blockState) {
		Block block = blockState.getBlock();
		return block == this.getPoweredState(this.getDefaultState()).getBlock() || block == this.getUnpoweredState(this.getDefaultState()).getBlock();
	}

	public boolean isTargetNotAligned(World world, BlockPos pos, BlockState state) {
		Direction direction = ((Direction)state.get(DIRECTION)).getOpposite();
		BlockPos blockPos = pos.offset(direction);
		return isRedstoneGateBlock(world.getBlockState(blockPos)) ? world.getBlockState(blockPos).get(DIRECTION) != direction : false;
	}

	protected int getUpdateDelay(BlockState state) {
		return this.getUpdateDelayInternal(state);
	}

	protected abstract int getUpdateDelayInternal(BlockState state);

	protected abstract BlockState getPoweredState(BlockState state);

	protected abstract BlockState getUnpoweredState(BlockState state);

	@Override
	public boolean isEqualTo(Block block) {
		return this.method_11603(block.getDefaultState());
	}

	@Override
	public RenderLayer getRenderLayerType() {
		return RenderLayer.CUTOUT;
	}

	@Override
	public BlockRenderLayer getRenderLayer(BlockView world, BlockState state, BlockPos pos, Direction direction) {
		return direction == Direction.DOWN ? BlockRenderLayer.SOLID : BlockRenderLayer.UNDEFINED;
	}
}
