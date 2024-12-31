package net.minecraft.block;

import java.util.Random;
import net.minecraft.block.piston.PistonBehavior;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shapes.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.IWorld;
import net.minecraft.world.RenderBlockView;
import net.minecraft.world.World;

public abstract class AbstractPressurePlateBlock extends Block {
	protected static final VoxelShape PRESSED_SHAPE = Block.createCuboidShape(1.0, 0.0, 1.0, 15.0, 0.5, 15.0);
	protected static final VoxelShape DEFAULT_SHAPE = Block.createCuboidShape(1.0, 0.0, 1.0, 15.0, 1.0, 15.0);
	protected static final Box BOX = new Box(0.125, 0.0, 0.125, 0.875, 0.25, 0.875);

	protected AbstractPressurePlateBlock(Block.Builder builder) {
		super(builder);
	}

	@Override
	public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos) {
		return this.getRedstoneOutput(state) > 0 ? PRESSED_SHAPE : DEFAULT_SHAPE;
	}

	@Override
	public int getTickDelay(RenderBlockView world) {
		return 20;
	}

	@Override
	public boolean method_11562(BlockState state) {
		return false;
	}

	@Override
	public boolean canMobSpawnInside() {
		return true;
	}

	@Override
	public BlockState getStateForNeighborUpdate(BlockState state, Direction direction, BlockState neighborState, IWorld world, BlockPos pos, BlockPos neighborPos) {
		return direction == Direction.DOWN && !state.canPlaceAt(world, pos)
			? Blocks.AIR.getDefaultState()
			: super.getStateForNeighborUpdate(state, direction, neighborState, world, pos, neighborPos);
	}

	@Override
	public boolean canPlaceAt(BlockState state, RenderBlockView world, BlockPos pos) {
		BlockState blockState = world.getBlockState(pos.down());
		return blockState.method_16913() || blockState.getBlock() instanceof FenceBlock;
	}

	@Override
	public void scheduledTick(BlockState state, World world, BlockPos pos, Random random) {
		if (!world.isClient) {
			int i = this.getRedstoneOutput(state);
			if (i > 0) {
				this.updatePlateState(world, pos, state, i);
			}
		}
	}

	@Override
	public void onEntityCollision(BlockState state, World world, BlockPos pos, Entity entity) {
		if (!world.isClient) {
			int i = this.getRedstoneOutput(state);
			if (i == 0) {
				this.updatePlateState(world, pos, state, i);
			}
		}
	}

	protected void updatePlateState(World world, BlockPos pos, BlockState state, int output) {
		int i = this.getRedstoneOutput(world, pos);
		boolean bl = output > 0;
		boolean bl2 = i > 0;
		if (output != i) {
			state = this.setRedstoneOutput(state, i);
			world.setBlockState(pos, state, 2);
			this.updateNeighbours(world, pos);
			world.onRenderRegionUpdate(pos, pos);
		}

		if (!bl2 && bl) {
			this.playDepressSound(world, pos);
		} else if (bl2 && !bl) {
			this.playPressSound(world, pos);
		}

		if (bl2) {
			world.getBlockTickScheduler().schedule(new BlockPos(pos), this, this.getTickDelay(world));
		}
	}

	protected abstract void playPressSound(IWorld world, BlockPos pos);

	protected abstract void playDepressSound(IWorld world, BlockPos pos);

	@Override
	public void onStateReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean moved) {
		if (!moved && state.getBlock() != newState.getBlock()) {
			if (this.getRedstoneOutput(state) > 0) {
				this.updateNeighbours(world, pos);
			}

			super.onStateReplaced(state, world, pos, newState, moved);
		}
	}

	protected void updateNeighbours(World world, BlockPos pos) {
		world.updateNeighborsAlways(pos, this);
		world.updateNeighborsAlways(pos.down(), this);
	}

	@Override
	public int getWeakRedstonePower(BlockState state, BlockView world, BlockPos pos, Direction direction) {
		return this.getRedstoneOutput(state);
	}

	@Override
	public int getStrongRedstonePower(BlockState state, BlockView world, BlockPos pos, Direction direction) {
		return direction == Direction.UP ? this.getRedstoneOutput(state) : 0;
	}

	@Override
	public boolean emitsRedstonePower(BlockState state) {
		return true;
	}

	@Override
	public PistonBehavior getPistonBehavior(BlockState state) {
		return PistonBehavior.DESTROY;
	}

	protected abstract int getRedstoneOutput(World world, BlockPos pos);

	protected abstract int getRedstoneOutput(BlockState state);

	protected abstract BlockState setRedstoneOutput(BlockState state, int value);

	@Override
	public BlockRenderLayer getRenderLayer(BlockView world, BlockState state, BlockPos pos, Direction direction) {
		return BlockRenderLayer.UNDEFINED;
	}
}
