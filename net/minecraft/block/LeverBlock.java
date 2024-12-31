package net.minecraft.block;

import java.util.Random;
import net.minecraft.class_4338;
import net.minecraft.block.enums.WallMountLocation;
import net.minecraft.client.sound.SoundCategory;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.sound.Sounds;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.states.property.Properties;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shapes.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;

public class LeverBlock extends WallPlacedBlock {
	public static final BooleanProperty field_18391 = Properties.POWERED;
	protected static final VoxelShape field_18392 = Block.createCuboidShape(5.0, 4.0, 10.0, 11.0, 12.0, 16.0);
	protected static final VoxelShape field_18393 = Block.createCuboidShape(5.0, 4.0, 0.0, 11.0, 12.0, 6.0);
	protected static final VoxelShape field_18394 = Block.createCuboidShape(10.0, 4.0, 5.0, 16.0, 12.0, 11.0);
	protected static final VoxelShape field_18395 = Block.createCuboidShape(0.0, 4.0, 5.0, 6.0, 12.0, 11.0);
	protected static final VoxelShape field_18396 = Block.createCuboidShape(5.0, 0.0, 4.0, 11.0, 6.0, 12.0);
	protected static final VoxelShape field_18397 = Block.createCuboidShape(4.0, 0.0, 5.0, 12.0, 6.0, 11.0);
	protected static final VoxelShape field_18398 = Block.createCuboidShape(5.0, 10.0, 4.0, 11.0, 16.0, 12.0);
	protected static final VoxelShape field_18399 = Block.createCuboidShape(4.0, 10.0, 5.0, 12.0, 16.0, 11.0);

	protected LeverBlock(Block.Builder builder) {
		super(builder);
		this.setDefaultState(
			this.stateManager
				.method_16923()
				.withProperty(FACING, Direction.NORTH)
				.withProperty(field_18391, Boolean.valueOf(false))
				.withProperty(FACE, WallMountLocation.WALL)
		);
	}

	@Override
	public boolean method_11562(BlockState state) {
		return false;
	}

	@Override
	public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos) {
		switch ((WallMountLocation)state.getProperty(FACE)) {
			case FLOOR:
				switch (((Direction)state.getProperty(FACING)).getAxis()) {
					case X:
						return field_18397;
					case Z:
					default:
						return field_18396;
				}
			case WALL:
				switch ((Direction)state.getProperty(FACING)) {
					case EAST:
						return field_18395;
					case WEST:
						return field_18394;
					case SOUTH:
						return field_18393;
					case NORTH:
					default:
						return field_18392;
				}
			case CEILING:
			default:
				switch (((Direction)state.getProperty(FACING)).getAxis()) {
					case X:
						return field_18399;
					case Z:
					default:
						return field_18398;
				}
		}
	}

	@Override
	public boolean onUse(
		BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, Direction direction, float distanceX, float distanceY, float distanceZ
	) {
		state = state.method_16930(field_18391);
		boolean bl = (Boolean)state.getProperty(field_18391);
		if (world.isClient) {
			if (bl) {
				method_16695(state, world, pos, 1.0F);
			}

			return true;
		} else {
			world.setBlockState(pos, state, 3);
			float f = bl ? 0.6F : 0.5F;
			world.playSound(null, pos, Sounds.BLOCK_LEVER_CLICK, SoundCategory.BLOCKS, 0.3F, f);
			this.method_16696(state, world, pos);
			return true;
		}
	}

	private static void method_16695(BlockState blockState, IWorld iWorld, BlockPos blockPos, float f) {
		Direction direction = ((Direction)blockState.getProperty(FACING)).getOpposite();
		Direction direction2 = getDirection(blockState).getOpposite();
		double d = (double)blockPos.getX() + 0.5 + 0.1 * (double)direction.getOffsetX() + 0.2 * (double)direction2.getOffsetX();
		double e = (double)blockPos.getY() + 0.5 + 0.1 * (double)direction.getOffsetY() + 0.2 * (double)direction2.getOffsetY();
		double g = (double)blockPos.getZ() + 0.5 + 0.1 * (double)direction.getOffsetZ() + 0.2 * (double)direction2.getOffsetZ();
		iWorld.method_16343(new class_4338(1.0F, 0.0F, 0.0F, f), d, e, g, 0.0, 0.0, 0.0);
	}

	@Override
	public void randomDisplayTick(BlockState state, World world, BlockPos pos, Random random) {
		if ((Boolean)state.getProperty(field_18391) && random.nextFloat() < 0.25F) {
			method_16695(state, world, pos, 0.5F);
		}
	}

	@Override
	public void onStateReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean moved) {
		if (!moved && state.getBlock() != newState.getBlock()) {
			if ((Boolean)state.getProperty(field_18391)) {
				this.method_16696(state, world, pos);
			}

			super.onStateReplaced(state, world, pos, newState, moved);
		}
	}

	@Override
	public int getWeakRedstonePower(BlockState state, BlockView world, BlockPos pos, Direction direction) {
		return state.getProperty(field_18391) ? 15 : 0;
	}

	@Override
	public int getStrongRedstonePower(BlockState state, BlockView world, BlockPos pos, Direction direction) {
		return state.getProperty(field_18391) && getDirection(state) == direction ? 15 : 0;
	}

	@Override
	public boolean emitsRedstonePower(BlockState state) {
		return true;
	}

	private void method_16696(BlockState blockState, World world, BlockPos blockPos) {
		world.updateNeighborsAlways(blockPos, this);
		world.updateNeighborsAlways(blockPos.offset(getDirection(blockState).getOpposite()), this);
	}

	@Override
	protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
		builder.method_16928(FACE, FACING, field_18391);
	}

	@Override
	public BlockRenderLayer getRenderLayer(BlockView world, BlockState state, BlockPos pos, Direction direction) {
		return BlockRenderLayer.UNDEFINED;
	}
}
