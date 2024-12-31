package net.minecraft.block;

import java.util.Random;
import net.minecraft.block.enums.WallMountLocation;
import net.minecraft.entity.EntityContext;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.particle.DustParticleEffect;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;

public class LeverBlock extends WallMountedBlock {
	public static final BooleanProperty POWERED = Properties.POWERED;
	protected static final VoxelShape NORTH_WALL_SHAPE = Block.createCuboidShape(5.0, 4.0, 10.0, 11.0, 12.0, 16.0);
	protected static final VoxelShape SOUTH_WALL_SHAPE = Block.createCuboidShape(5.0, 4.0, 0.0, 11.0, 12.0, 6.0);
	protected static final VoxelShape WEST_WALL_SHAPE = Block.createCuboidShape(10.0, 4.0, 5.0, 16.0, 12.0, 11.0);
	protected static final VoxelShape EAST_WALL_SHAPE = Block.createCuboidShape(0.0, 4.0, 5.0, 6.0, 12.0, 11.0);
	protected static final VoxelShape FLOOR_Z_AXIS_SHAPE = Block.createCuboidShape(5.0, 0.0, 4.0, 11.0, 6.0, 12.0);
	protected static final VoxelShape FLOOR_X_AXIS_SHAPE = Block.createCuboidShape(4.0, 0.0, 5.0, 12.0, 6.0, 11.0);
	protected static final VoxelShape CEILING_Z_AXIS_SHAPE = Block.createCuboidShape(5.0, 10.0, 4.0, 11.0, 16.0, 12.0);
	protected static final VoxelShape CEILING_X_AXIS_SHAPE = Block.createCuboidShape(4.0, 10.0, 5.0, 12.0, 16.0, 11.0);

	protected LeverBlock(Block.Settings settings) {
		super(settings);
		this.setDefaultState(
			this.stateManager.getDefaultState().with(FACING, Direction.field_11043).with(POWERED, Boolean.valueOf(false)).with(FACE, WallMountLocation.field_12471)
		);
	}

	@Override
	public VoxelShape getOutlineShape(BlockState blockState, BlockView blockView, BlockPos blockPos, EntityContext entityContext) {
		switch ((WallMountLocation)blockState.get(FACE)) {
			case field_12475:
				switch (((Direction)blockState.get(FACING)).getAxis()) {
					case field_11048:
						return FLOOR_X_AXIS_SHAPE;
					case field_11051:
					default:
						return FLOOR_Z_AXIS_SHAPE;
				}
			case field_12471:
				switch ((Direction)blockState.get(FACING)) {
					case field_11034:
						return EAST_WALL_SHAPE;
					case field_11039:
						return WEST_WALL_SHAPE;
					case field_11035:
						return SOUTH_WALL_SHAPE;
					case field_11043:
					default:
						return NORTH_WALL_SHAPE;
				}
			case field_12473:
			default:
				switch (((Direction)blockState.get(FACING)).getAxis()) {
					case field_11048:
						return CEILING_X_AXIS_SHAPE;
					case field_11051:
					default:
						return CEILING_Z_AXIS_SHAPE;
				}
		}
	}

	@Override
	public ActionResult onUse(BlockState blockState, World world, BlockPos blockPos, PlayerEntity playerEntity, Hand hand, BlockHitResult blockHitResult) {
		if (world.isClient) {
			BlockState blockState2 = blockState.cycle(POWERED);
			if ((Boolean)blockState2.get(POWERED)) {
				spawnParticles(blockState2, world, blockPos, 1.0F);
			}

			return ActionResult.field_5812;
		} else {
			BlockState blockState3 = this.method_21846(blockState, world, blockPos);
			float f = blockState3.get(POWERED) ? 0.6F : 0.5F;
			world.playSound(null, blockPos, SoundEvents.field_14962, SoundCategory.field_15245, 0.3F, f);
			return ActionResult.field_5812;
		}
	}

	public BlockState method_21846(BlockState blockState, World world, BlockPos blockPos) {
		blockState = blockState.cycle(POWERED);
		world.setBlockState(blockPos, blockState, 3);
		this.updateNeighbors(blockState, world, blockPos);
		return blockState;
	}

	private static void spawnParticles(BlockState blockState, IWorld iWorld, BlockPos blockPos, float f) {
		Direction direction = ((Direction)blockState.get(FACING)).getOpposite();
		Direction direction2 = getDirection(blockState).getOpposite();
		double d = (double)blockPos.getX() + 0.5 + 0.1 * (double)direction.getOffsetX() + 0.2 * (double)direction2.getOffsetX();
		double e = (double)blockPos.getY() + 0.5 + 0.1 * (double)direction.getOffsetY() + 0.2 * (double)direction2.getOffsetY();
		double g = (double)blockPos.getZ() + 0.5 + 0.1 * (double)direction.getOffsetZ() + 0.2 * (double)direction2.getOffsetZ();
		iWorld.addParticle(new DustParticleEffect(1.0F, 0.0F, 0.0F, f), d, e, g, 0.0, 0.0, 0.0);
	}

	@Override
	public void randomDisplayTick(BlockState blockState, World world, BlockPos blockPos, Random random) {
		if ((Boolean)blockState.get(POWERED) && random.nextFloat() < 0.25F) {
			spawnParticles(blockState, world, blockPos, 0.5F);
		}
	}

	@Override
	public void onBlockRemoved(BlockState blockState, World world, BlockPos blockPos, BlockState blockState2, boolean bl) {
		if (!bl && blockState.getBlock() != blockState2.getBlock()) {
			if ((Boolean)blockState.get(POWERED)) {
				this.updateNeighbors(blockState, world, blockPos);
			}

			super.onBlockRemoved(blockState, world, blockPos, blockState2, bl);
		}
	}

	@Override
	public int getWeakRedstonePower(BlockState blockState, BlockView blockView, BlockPos blockPos, Direction direction) {
		return blockState.get(POWERED) ? 15 : 0;
	}

	@Override
	public int getStrongRedstonePower(BlockState blockState, BlockView blockView, BlockPos blockPos, Direction direction) {
		return blockState.get(POWERED) && getDirection(blockState) == direction ? 15 : 0;
	}

	@Override
	public boolean emitsRedstonePower(BlockState blockState) {
		return true;
	}

	private void updateNeighbors(BlockState blockState, World world, BlockPos blockPos) {
		world.updateNeighborsAlways(blockPos, this);
		world.updateNeighborsAlways(blockPos.offset(getDirection(blockState).getOpposite()), this);
	}

	@Override
	protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
		builder.add(FACE, FACING, POWERED);
	}
}
