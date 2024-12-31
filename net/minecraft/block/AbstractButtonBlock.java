package net.minecraft.block;

import java.util.List;
import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.block.enums.WallMountLocation;
import net.minecraft.client.sound.SoundCategory;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.AbstractArrowEntity;
import net.minecraft.sound.Sound;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.states.property.Properties;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shapes.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.IWorld;
import net.minecraft.world.RenderBlockView;
import net.minecraft.world.World;

public abstract class AbstractButtonBlock extends WallPlacedBlock {
	public static final BooleanProperty POWERED = Properties.POWERED;
	protected static final VoxelShape FLOOR_X_SHAPE = Block.createCuboidShape(6.0, 14.0, 5.0, 10.0, 16.0, 11.0);
	protected static final VoxelShape FLOOR_Z_SHAPE = Block.createCuboidShape(5.0, 14.0, 6.0, 11.0, 16.0, 10.0);
	protected static final VoxelShape CEILING_X_SHAPE = Block.createCuboidShape(6.0, 0.0, 5.0, 10.0, 2.0, 11.0);
	protected static final VoxelShape CEILING_Z_SHAPE = Block.createCuboidShape(5.0, 0.0, 6.0, 11.0, 2.0, 10.0);
	protected static final VoxelShape NORTH_SHAPE = Block.createCuboidShape(5.0, 6.0, 14.0, 11.0, 10.0, 16.0);
	protected static final VoxelShape SOUTH_SHAPE = Block.createCuboidShape(5.0, 6.0, 0.0, 11.0, 10.0, 2.0);
	protected static final VoxelShape WEST_SHAPE = Block.createCuboidShape(14.0, 6.0, 5.0, 16.0, 10.0, 11.0);
	protected static final VoxelShape EAST_SHAPE = Block.createCuboidShape(0.0, 6.0, 5.0, 2.0, 10.0, 11.0);
	protected static final VoxelShape FLOOR_PRESSED_X_SHAPE = Block.createCuboidShape(6.0, 15.0, 5.0, 10.0, 16.0, 11.0);
	protected static final VoxelShape FLOOR_PRESSED_Z_SHAPE = Block.createCuboidShape(5.0, 15.0, 6.0, 11.0, 16.0, 10.0);
	protected static final VoxelShape CEILING_X_PRESSED_SHAPE = Block.createCuboidShape(6.0, 0.0, 5.0, 10.0, 1.0, 11.0);
	protected static final VoxelShape CEILING_PRESSED_Z_SHAPE = Block.createCuboidShape(5.0, 0.0, 6.0, 11.0, 1.0, 10.0);
	protected static final VoxelShape NORTH_PRESSED_SHAPE = Block.createCuboidShape(5.0, 6.0, 15.0, 11.0, 10.0, 16.0);
	protected static final VoxelShape SOUTH_PRESSED_SHAPE = Block.createCuboidShape(5.0, 6.0, 0.0, 11.0, 10.0, 1.0);
	protected static final VoxelShape WEST_PRESSED_SHAPE = Block.createCuboidShape(15.0, 6.0, 5.0, 16.0, 10.0, 11.0);
	protected static final VoxelShape EAST_PRESSED_SHAPE = Block.createCuboidShape(0.0, 6.0, 5.0, 1.0, 10.0, 11.0);
	private final boolean wooden;

	protected AbstractButtonBlock(boolean bl, Block.Builder builder) {
		super(builder);
		this.setDefaultState(
			this.stateManager
				.method_16923()
				.withProperty(FACING, Direction.NORTH)
				.withProperty(POWERED, Boolean.valueOf(false))
				.withProperty(FACE, WallMountLocation.WALL)
		);
		this.wooden = bl;
	}

	@Override
	public int getTickDelay(RenderBlockView world) {
		return this.wooden ? 30 : 20;
	}

	@Override
	public boolean method_11562(BlockState state) {
		return false;
	}

	@Override
	public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos) {
		Direction direction = state.getProperty(FACING);
		boolean bl = (Boolean)state.getProperty(POWERED);
		switch ((WallMountLocation)state.getProperty(FACE)) {
			case FLOOR:
				if (direction.getAxis() == Direction.Axis.X) {
					return bl ? CEILING_X_PRESSED_SHAPE : CEILING_X_SHAPE;
				}

				return bl ? CEILING_PRESSED_Z_SHAPE : CEILING_Z_SHAPE;
			case WALL:
				switch (direction) {
					case EAST:
						return bl ? EAST_PRESSED_SHAPE : EAST_SHAPE;
					case WEST:
						return bl ? WEST_PRESSED_SHAPE : WEST_SHAPE;
					case SOUTH:
						return bl ? SOUTH_PRESSED_SHAPE : SOUTH_SHAPE;
					case NORTH:
					default:
						return bl ? NORTH_PRESSED_SHAPE : NORTH_SHAPE;
				}
			case CEILING:
			default:
				if (direction.getAxis() == Direction.Axis.X) {
					return bl ? FLOOR_PRESSED_X_SHAPE : FLOOR_X_SHAPE;
				} else {
					return bl ? FLOOR_PRESSED_Z_SHAPE : FLOOR_Z_SHAPE;
				}
		}
	}

	@Override
	public boolean onUse(
		BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, Direction direction, float distanceX, float distanceY, float distanceZ
	) {
		if ((Boolean)state.getProperty(POWERED)) {
			return true;
		} else {
			world.setBlockState(pos, state.withProperty(POWERED, Boolean.valueOf(true)), 3);
			this.playClickSound(player, world, pos, true);
			this.updateNeighbors(state, world, pos);
			world.getBlockTickScheduler().schedule(pos, this, this.getTickDelay(world));
			return true;
		}
	}

	protected void playClickSound(@Nullable PlayerEntity player, IWorld world, BlockPos pos, boolean powered) {
		world.playSound(powered ? player : null, pos, this.getClickSound(powered), SoundCategory.BLOCKS, 0.3F, powered ? 0.6F : 0.5F);
	}

	protected abstract Sound getClickSound(boolean powered);

	@Override
	public void onStateReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean moved) {
		if (!moved && state.getBlock() != newState.getBlock()) {
			if ((Boolean)state.getProperty(POWERED)) {
				this.updateNeighbors(state, world, pos);
			}

			super.onStateReplaced(state, world, pos, newState, moved);
		}
	}

	@Override
	public int getWeakRedstonePower(BlockState state, BlockView world, BlockPos pos, Direction direction) {
		return state.getProperty(POWERED) ? 15 : 0;
	}

	@Override
	public int getStrongRedstonePower(BlockState state, BlockView world, BlockPos pos, Direction direction) {
		return state.getProperty(POWERED) && getDirection(state) == direction ? 15 : 0;
	}

	@Override
	public boolean emitsRedstonePower(BlockState state) {
		return true;
	}

	@Override
	public void scheduledTick(BlockState state, World world, BlockPos pos, Random random) {
		if (!world.isClient && (Boolean)state.getProperty(POWERED)) {
			if (this.wooden) {
				this.tryPowerWithProjectiles(state, world, pos);
			} else {
				world.setBlockState(pos, state.withProperty(POWERED, Boolean.valueOf(false)), 3);
				this.updateNeighbors(state, world, pos);
				this.playClickSound(null, world, pos, false);
			}
		}
	}

	@Override
	public void onEntityCollision(BlockState state, World world, BlockPos pos, Entity entity) {
		if (!world.isClient && this.wooden && !(Boolean)state.getProperty(POWERED)) {
			this.tryPowerWithProjectiles(state, world, pos);
		}
	}

	private void tryPowerWithProjectiles(BlockState state, World world, BlockPos pos) {
		List<? extends Entity> list = world.getEntitiesInBox(AbstractArrowEntity.class, state.getOutlineShape(world, pos).getBoundingBox().offset(pos));
		boolean bl = !list.isEmpty();
		boolean bl2 = (Boolean)state.getProperty(POWERED);
		if (bl != bl2) {
			world.setBlockState(pos, state.withProperty(POWERED, Boolean.valueOf(bl)), 3);
			this.updateNeighbors(state, world, pos);
			this.playClickSound(null, world, pos, bl);
		}

		if (bl) {
			world.getBlockTickScheduler().schedule(new BlockPos(pos), this, this.getTickDelay(world));
		}
	}

	private void updateNeighbors(BlockState state, World world, BlockPos pos) {
		world.updateNeighborsAlways(pos, this);
		world.updateNeighborsAlways(pos.offset(getDirection(state).getOpposite()), this);
	}

	@Override
	protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
		builder.method_16928(FACING, POWERED, FACE);
	}

	@Override
	public BlockRenderLayer getRenderLayer(BlockView world, BlockState state, BlockPos pos, Direction direction) {
		return BlockRenderLayer.UNDEFINED;
	}
}
