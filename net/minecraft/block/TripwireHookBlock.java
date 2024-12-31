package net.minecraft.block;

import com.google.common.base.MoreObjects;
import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.sound.SoundCategory;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.sound.Sounds;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.states.property.Properties;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shapes.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.IWorld;
import net.minecraft.world.RenderBlockView;
import net.minecraft.world.World;

public class TripwireHookBlock extends Block {
	public static final DirectionProperty FACING = HorizontalFacingBlock.FACING;
	public static final BooleanProperty field_18553 = Properties.POWERED;
	public static final BooleanProperty field_18554 = Properties.ATTACHED;
	protected static final VoxelShape field_18555 = Block.createCuboidShape(5.0, 0.0, 10.0, 11.0, 10.0, 16.0);
	protected static final VoxelShape field_18556 = Block.createCuboidShape(5.0, 0.0, 0.0, 11.0, 10.0, 6.0);
	protected static final VoxelShape field_18557 = Block.createCuboidShape(10.0, 0.0, 5.0, 16.0, 10.0, 11.0);
	protected static final VoxelShape field_18558 = Block.createCuboidShape(0.0, 0.0, 5.0, 6.0, 10.0, 11.0);

	public TripwireHookBlock(Block.Builder builder) {
		super(builder);
		this.setDefaultState(
			this.stateManager
				.method_16923()
				.withProperty(FACING, Direction.NORTH)
				.withProperty(field_18553, Boolean.valueOf(false))
				.withProperty(field_18554, Boolean.valueOf(false))
		);
	}

	@Override
	public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos) {
		switch ((Direction)state.getProperty(FACING)) {
			case EAST:
			default:
				return field_18558;
			case WEST:
				return field_18557;
			case SOUTH:
				return field_18556;
			case NORTH:
				return field_18555;
		}
	}

	@Override
	public boolean method_11562(BlockState state) {
		return false;
	}

	@Override
	public boolean canPlaceAt(BlockState state, RenderBlockView world, BlockPos pos) {
		Direction direction = state.getProperty(FACING);
		BlockPos blockPos = pos.offset(direction.getOpposite());
		BlockState blockState = world.getBlockState(blockPos);
		boolean bl = method_14309(blockState.getBlock());
		return !bl
			&& direction.getAxis().isHorizontal()
			&& blockState.getRenderLayer(world, blockPos, direction) == BlockRenderLayer.SOLID
			&& !blockState.emitsRedstonePower();
	}

	@Override
	public BlockState getStateForNeighborUpdate(BlockState state, Direction direction, BlockState neighborState, IWorld world, BlockPos pos, BlockPos neighborPos) {
		return direction.getOpposite() == state.getProperty(FACING) && !state.canPlaceAt(world, pos)
			? Blocks.AIR.getDefaultState()
			: super.getStateForNeighborUpdate(state, direction, neighborState, world, pos, neighborPos);
	}

	@Nullable
	@Override
	public BlockState getPlacementState(ItemPlacementContext context) {
		BlockState blockState = this.getDefaultState().withProperty(field_18553, Boolean.valueOf(false)).withProperty(field_18554, Boolean.valueOf(false));
		RenderBlockView renderBlockView = context.getWorld();
		BlockPos blockPos = context.getBlockPos();
		Direction[] directions = context.method_16021();

		for (Direction direction : directions) {
			if (direction.getAxis().isHorizontal()) {
				Direction direction2 = direction.getOpposite();
				blockState = blockState.withProperty(FACING, direction2);
				if (blockState.canPlaceAt(renderBlockView, blockPos)) {
					return blockState;
				}
			}
		}

		return null;
	}

	@Override
	public void onPlaced(World world, BlockPos pos, BlockState state, LivingEntity placer, ItemStack itemStack) {
		this.update(world, pos, state, false, false, -1, null);
	}

	public void update(
		World world, BlockPos pos, BlockState hookState, boolean beingRemoved, boolean updateNeighbors, int tripwireLength, @Nullable BlockState otherState
	) {
		Direction direction = hookState.getProperty(FACING);
		boolean bl = (Boolean)hookState.getProperty(field_18554);
		boolean bl2 = (Boolean)hookState.getProperty(field_18553);
		boolean bl3 = !beingRemoved;
		boolean bl4 = false;
		int i = 0;
		BlockState[] blockStates = new BlockState[42];

		for (int j = 1; j < 42; j++) {
			BlockPos blockPos = pos.offset(direction, j);
			BlockState blockState = world.getBlockState(blockPos);
			if (blockState.getBlock() == Blocks.TRIPWIRE_HOOK) {
				if (blockState.getProperty(FACING) == direction.getOpposite()) {
					i = j;
				}
				break;
			}

			if (blockState.getBlock() != Blocks.TRIPWIRE && j != tripwireLength) {
				blockStates[j] = null;
				bl3 = false;
			} else {
				if (j == tripwireLength) {
					blockState = (BlockState)MoreObjects.firstNonNull(otherState, blockState);
				}

				boolean bl5 = !(Boolean)blockState.getProperty(TripwireBlock.field_18544);
				boolean bl6 = (Boolean)blockState.getProperty(TripwireBlock.field_18542);
				bl4 |= bl5 && bl6;
				blockStates[j] = blockState;
				if (j == tripwireLength) {
					world.getBlockTickScheduler().schedule(pos, this, this.getTickDelay(world));
					bl3 &= bl5;
				}
			}
		}

		bl3 &= i > 1;
		bl4 &= bl3;
		BlockState blockState2 = this.getDefaultState().withProperty(field_18554, Boolean.valueOf(bl3)).withProperty(field_18553, Boolean.valueOf(bl4));
		if (i > 0) {
			BlockPos blockPos2 = pos.offset(direction, i);
			Direction direction2 = direction.getOpposite();
			world.setBlockState(blockPos2, blockState2.withProperty(FACING, direction2), 3);
			this.updateNeighborsOnAxis(world, blockPos2, direction2);
			this.method_11641(world, blockPos2, bl3, bl4, bl, bl2);
		}

		this.method_11641(world, pos, bl3, bl4, bl, bl2);
		if (!beingRemoved) {
			world.setBlockState(pos, blockState2.withProperty(FACING, direction), 3);
			if (updateNeighbors) {
				this.updateNeighborsOnAxis(world, pos, direction);
			}
		}

		if (bl != bl3) {
			for (int k = 1; k < i; k++) {
				BlockPos blockPos3 = pos.offset(direction, k);
				BlockState blockState3 = blockStates[k];
				if (blockState3 != null) {
					world.setBlockState(blockPos3, blockState3.withProperty(field_18554, Boolean.valueOf(bl3)), 3);
					if (!world.getBlockState(blockPos3).isAir()) {
					}
				}
			}
		}
	}

	@Override
	public void scheduledTick(BlockState state, World world, BlockPos pos, Random random) {
		this.update(world, pos, state, false, true, -1, null);
	}

	private void method_11641(World world, BlockPos blockPos, boolean bl, boolean bl2, boolean bl3, boolean bl4) {
		if (bl2 && !bl4) {
			world.playSound(null, blockPos, Sounds.BLOCK_TRIPWIRE_CLICK_ON, SoundCategory.BLOCKS, 0.4F, 0.6F);
		} else if (!bl2 && bl4) {
			world.playSound(null, blockPos, Sounds.BLOCK_TRIPWIRE_CLICK_OFF, SoundCategory.BLOCKS, 0.4F, 0.5F);
		} else if (bl && !bl3) {
			world.playSound(null, blockPos, Sounds.BLOCK_TRIPWIRE_ATTACH, SoundCategory.BLOCKS, 0.4F, 0.7F);
		} else if (!bl && bl3) {
			world.playSound(null, blockPos, Sounds.BLOCK_TRIPWIRE_DETACH, SoundCategory.BLOCKS, 0.4F, 1.2F / (world.random.nextFloat() * 0.2F + 0.9F));
		}
	}

	private void updateNeighborsOnAxis(World world, BlockPos pos, Direction dir) {
		world.updateNeighborsAlways(pos, this);
		world.updateNeighborsAlways(pos.offset(dir.getOpposite()), this);
	}

	@Override
	public void onStateReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean moved) {
		if (!moved && state.getBlock() != newState.getBlock()) {
			boolean bl = (Boolean)state.getProperty(field_18554);
			boolean bl2 = (Boolean)state.getProperty(field_18553);
			if (bl || bl2) {
				this.update(world, pos, state, true, false, -1, null);
			}

			if (bl2) {
				world.updateNeighborsAlways(pos, this);
				world.updateNeighborsAlways(pos.offset(((Direction)state.getProperty(FACING)).getOpposite()), this);
			}

			super.onStateReplaced(state, world, pos, newState, moved);
		}
	}

	@Override
	public int getWeakRedstonePower(BlockState state, BlockView world, BlockPos pos, Direction direction) {
		return state.getProperty(field_18553) ? 15 : 0;
	}

	@Override
	public int getStrongRedstonePower(BlockState state, BlockView world, BlockPos pos, Direction direction) {
		if (!(Boolean)state.getProperty(field_18553)) {
			return 0;
		} else {
			return state.getProperty(FACING) == direction ? 15 : 0;
		}
	}

	@Override
	public boolean emitsRedstonePower(BlockState state) {
		return true;
	}

	@Override
	public RenderLayer getRenderLayerType() {
		return RenderLayer.CUTOUT_MIPPED;
	}

	@Override
	public BlockState withRotation(BlockState state, BlockRotation rotation) {
		return state.withProperty(FACING, rotation.rotate(state.getProperty(FACING)));
	}

	@Override
	public BlockState withMirror(BlockState state, BlockMirror mirror) {
		return state.rotate(mirror.getRotation(state.getProperty(FACING)));
	}

	@Override
	protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
		builder.method_16928(FACING, field_18553, field_18554);
	}

	@Override
	public BlockRenderLayer getRenderLayer(BlockView world, BlockState state, BlockPos pos, Direction direction) {
		return BlockRenderLayer.UNDEFINED;
	}
}
