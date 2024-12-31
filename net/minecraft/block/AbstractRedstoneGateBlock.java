package net.minecraft.block;

import java.util.Random;
import net.minecraft.class_3605;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.states.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shapes.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.RenderBlockView;
import net.minecraft.world.World;

public abstract class AbstractRedstoneGateBlock extends HorizontalFacingBlock {
	protected static final VoxelShape SHAPE = Block.createCuboidShape(0.0, 0.0, 0.0, 16.0, 2.0, 16.0);
	public static final BooleanProperty POWERED = Properties.POWERED;

	protected AbstractRedstoneGateBlock(Block.Builder builder) {
		super(builder);
	}

	@Override
	public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos) {
		return SHAPE;
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
	public void scheduledTick(BlockState state, World world, BlockPos pos, Random random) {
		if (!this.isLocked(world, pos, state)) {
			boolean bl = (Boolean)state.getProperty(POWERED);
			boolean bl2 = this.hasPower(world, pos, state);
			if (bl && !bl2) {
				world.setBlockState(pos, state.withProperty(POWERED, Boolean.valueOf(false)), 2);
			} else if (!bl) {
				world.setBlockState(pos, state.withProperty(POWERED, Boolean.valueOf(true)), 2);
				if (!bl2) {
					world.getBlockTickScheduler().method_16419(pos, this, this.getUpdateDelayInternal(state), class_3605.HIGH);
				}
			}
		}
	}

	@Override
	public int getStrongRedstonePower(BlockState state, BlockView world, BlockPos pos, Direction direction) {
		return state.getWeakRedstonePower(world, pos, direction);
	}

	@Override
	public int getWeakRedstonePower(BlockState state, BlockView world, BlockPos pos, Direction direction) {
		if (!(Boolean)state.getProperty(POWERED)) {
			return 0;
		} else {
			return state.getProperty(FACING) == direction ? this.getOutputLevel(world, pos, state) : 0;
		}
	}

	@Override
	public void neighborUpdate(BlockState state, World world, BlockPos pos, Block block, BlockPos neighborPos) {
		if (state.canPlaceAt(world, pos)) {
			this.updatePowered(world, pos, state);
		} else {
			state.method_16867(world, pos, 0);
			world.method_8553(pos);

			for (Direction direction : Direction.values()) {
				world.updateNeighborsAlways(pos.offset(direction), this);
			}
		}
	}

	protected void updatePowered(World world, BlockPos pos, BlockState state) {
		if (!this.isLocked(world, pos, state)) {
			boolean bl = (Boolean)state.getProperty(POWERED);
			boolean bl2 = this.hasPower(world, pos, state);
			if (bl != bl2 && !world.getBlockTickScheduler().method_16420(pos, this)) {
				class_3605 lv = class_3605.HIGH;
				if (this.isTargetNotAligned(world, pos, state)) {
					lv = class_3605.EXTREMELY_HIGH;
				} else if (bl) {
					lv = class_3605.VERY_HIGH;
				}

				world.getBlockTickScheduler().method_16419(pos, this, this.getUpdateDelayInternal(state), lv);
			}
		}
	}

	public boolean isLocked(RenderBlockView world, BlockPos pos, BlockState state) {
		return false;
	}

	protected boolean hasPower(World world, BlockPos pos, BlockState state) {
		return this.getPower(world, pos, state) > 0;
	}

	protected int getPower(World world, BlockPos pos, BlockState state) {
		Direction direction = state.getProperty(FACING);
		BlockPos blockPos = pos.offset(direction);
		int i = world.getEmittedRedstonePower(blockPos, direction);
		if (i >= 15) {
			return i;
		} else {
			BlockState blockState = world.getBlockState(blockPos);
			return Math.max(i, blockState.getBlock() == Blocks.REDSTONE_WIRE ? (Integer)blockState.getProperty(RedstoneWireBlock.POWER) : 0);
		}
	}

	protected int getMaxInputLevelSides(RenderBlockView world, BlockPos pos, BlockState state) {
		Direction direction = state.getProperty(FACING);
		Direction direction2 = direction.rotateYClockwise();
		Direction direction3 = direction.rotateYCounterclockwise();
		return Math.max(this.getInputLevel(world, pos.offset(direction2), direction2), this.getInputLevel(world, pos.offset(direction3), direction3));
	}

	protected int getInputLevel(RenderBlockView world, BlockPos pos, Direction direction) {
		BlockState blockState = world.getBlockState(pos);
		Block block = blockState.getBlock();
		if (this.stateEmitRedstonePower(blockState)) {
			if (block == Blocks.REDSTONE_BLOCK) {
				return 15;
			} else {
				return block == Blocks.REDSTONE_WIRE ? (Integer)blockState.getProperty(RedstoneWireBlock.POWER) : world.method_8576(pos, direction);
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
	public BlockState getPlacementState(ItemPlacementContext context) {
		return this.getDefaultState().withProperty(FACING, context.method_16145().getOpposite());
	}

	@Override
	public void onPlaced(World world, BlockPos pos, BlockState state, LivingEntity placer, ItemStack itemStack) {
		if (this.hasPower(world, pos, state)) {
			world.getBlockTickScheduler().schedule(pos, this, 1);
		}
	}

	@Override
	public void onBlockAdded(BlockState state, World world, BlockPos pos, BlockState oldState) {
		this.updateTarget(world, pos, state);
	}

	@Override
	public void onStateReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean moved) {
		if (!moved && state.getBlock() != newState.getBlock()) {
			super.onStateReplaced(state, world, pos, newState, moved);
			this.removeBlockEntity(world, pos);
			this.updateTarget(world, pos, state);
		}
	}

	protected void removeBlockEntity(World world, BlockPos pos) {
	}

	protected void updateTarget(World world, BlockPos pos, BlockState state) {
		Direction direction = state.getProperty(FACING);
		BlockPos blockPos = pos.offset(direction.getOpposite());
		world.updateNeighbor(blockPos, this, pos);
		world.updateNeighborsExcept(blockPos, this, direction);
	}

	protected boolean stateEmitRedstonePower(BlockState state) {
		return state.emitsRedstonePower();
	}

	protected int getOutputLevel(BlockView world, BlockPos pos, BlockState state) {
		return 15;
	}

	public static boolean isRedstoneGateBlock(BlockState state) {
		return state.getBlock() instanceof AbstractRedstoneGateBlock;
	}

	public boolean isTargetNotAligned(BlockView world, BlockPos pos, BlockState state) {
		Direction direction = ((Direction)state.getProperty(FACING)).getOpposite();
		BlockState blockState = world.getBlockState(pos.offset(direction));
		return isRedstoneGateBlock(blockState) && blockState.getProperty(FACING) != direction;
	}

	protected abstract int getUpdateDelayInternal(BlockState state);

	@Override
	public RenderLayer getRenderLayerType() {
		return RenderLayer.CUTOUT;
	}

	@Override
	public boolean isFullBoundsCubeForCulling(BlockState blockState) {
		return true;
	}

	@Override
	public BlockRenderLayer getRenderLayer(BlockView world, BlockState state, BlockPos pos, Direction direction) {
		return direction == Direction.DOWN ? BlockRenderLayer.SOLID : BlockRenderLayer.UNDEFINED;
	}
}
