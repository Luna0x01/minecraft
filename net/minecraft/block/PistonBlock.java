package net.minecraft.block;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.PistonBlockEntity;
import net.minecraft.block.enums.PistonType;
import net.minecraft.block.piston.PistonBehavior;
import net.minecraft.block.piston.PistonHandler;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.pathing.NavigationType;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.tag.BlockTags;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;

public class PistonBlock extends FacingBlock {
	public static final BooleanProperty EXTENDED = Properties.EXTENDED;
	public static final int field_31373 = 0;
	public static final int field_31374 = 1;
	public static final int field_31375 = 2;
	public static final float field_31376 = 4.0F;
	protected static final VoxelShape EXTENDED_EAST_SHAPE = Block.createCuboidShape(0.0, 0.0, 0.0, 12.0, 16.0, 16.0);
	protected static final VoxelShape EXTENDED_WEST_SHAPE = Block.createCuboidShape(4.0, 0.0, 0.0, 16.0, 16.0, 16.0);
	protected static final VoxelShape EXTENDED_SOUTH_SHAPE = Block.createCuboidShape(0.0, 0.0, 0.0, 16.0, 16.0, 12.0);
	protected static final VoxelShape EXTENDED_NORTH_SHAPE = Block.createCuboidShape(0.0, 0.0, 4.0, 16.0, 16.0, 16.0);
	protected static final VoxelShape EXTENDED_UP_SHAPE = Block.createCuboidShape(0.0, 0.0, 0.0, 16.0, 12.0, 16.0);
	protected static final VoxelShape EXTENDED_DOWN_SHAPE = Block.createCuboidShape(0.0, 4.0, 0.0, 16.0, 16.0, 16.0);
	private final boolean sticky;

	public PistonBlock(boolean sticky, AbstractBlock.Settings settings) {
		super(settings);
		this.setDefaultState(this.stateManager.getDefaultState().with(FACING, Direction.NORTH).with(EXTENDED, Boolean.valueOf(false)));
		this.sticky = sticky;
	}

	@Override
	public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
		if ((Boolean)state.get(EXTENDED)) {
			switch ((Direction)state.get(FACING)) {
				case DOWN:
					return EXTENDED_DOWN_SHAPE;
				case UP:
				default:
					return EXTENDED_UP_SHAPE;
				case NORTH:
					return EXTENDED_NORTH_SHAPE;
				case SOUTH:
					return EXTENDED_SOUTH_SHAPE;
				case WEST:
					return EXTENDED_WEST_SHAPE;
				case EAST:
					return EXTENDED_EAST_SHAPE;
			}
		} else {
			return VoxelShapes.fullCube();
		}
	}

	@Override
	public void onPlaced(World world, BlockPos pos, BlockState state, LivingEntity placer, ItemStack itemStack) {
		if (!world.isClient) {
			this.tryMove(world, pos, state);
		}
	}

	@Override
	public void neighborUpdate(BlockState state, World world, BlockPos pos, Block block, BlockPos fromPos, boolean notify) {
		if (!world.isClient) {
			this.tryMove(world, pos, state);
		}
	}

	@Override
	public void onBlockAdded(BlockState state, World world, BlockPos pos, BlockState oldState, boolean notify) {
		if (!oldState.isOf(state.getBlock())) {
			if (!world.isClient && world.getBlockEntity(pos) == null) {
				this.tryMove(world, pos, state);
			}
		}
	}

	@Override
	public BlockState getPlacementState(ItemPlacementContext ctx) {
		return this.getDefaultState().with(FACING, ctx.getPlayerLookDirection().getOpposite()).with(EXTENDED, Boolean.valueOf(false));
	}

	private void tryMove(World world, BlockPos pos, BlockState state) {
		Direction direction = state.get(FACING);
		boolean bl = this.shouldExtend(world, pos, direction);
		if (bl && !(Boolean)state.get(EXTENDED)) {
			if (new PistonHandler(world, pos, direction, true).calculatePush()) {
				world.addSyncedBlockEvent(pos, this, 0, direction.getId());
			}
		} else if (!bl && (Boolean)state.get(EXTENDED)) {
			BlockPos blockPos = pos.offset(direction, 2);
			BlockState blockState = world.getBlockState(blockPos);
			int i = 1;
			if (blockState.isOf(Blocks.MOVING_PISTON)
				&& blockState.get(FACING) == direction
				&& world.getBlockEntity(blockPos) instanceof PistonBlockEntity pistonBlockEntity
				&& pistonBlockEntity.isExtending()
				&& (pistonBlockEntity.getProgress(0.0F) < 0.5F || world.getTime() == pistonBlockEntity.getSavedWorldTime() || ((ServerWorld)world).isInBlockTick())) {
				i = 2;
			}

			world.addSyncedBlockEvent(pos, this, i, direction.getId());
		}
	}

	private boolean shouldExtend(World world, BlockPos pos, Direction pistonFace) {
		for (Direction direction : Direction.values()) {
			if (direction != pistonFace && world.isEmittingRedstonePower(pos.offset(direction), direction)) {
				return true;
			}
		}

		if (world.isEmittingRedstonePower(pos, Direction.DOWN)) {
			return true;
		} else {
			BlockPos blockPos = pos.up();

			for (Direction direction2 : Direction.values()) {
				if (direction2 != Direction.DOWN && world.isEmittingRedstonePower(blockPos.offset(direction2), direction2)) {
					return true;
				}
			}

			return false;
		}
	}

	@Override
	public boolean onSyncedBlockEvent(BlockState state, World world, BlockPos pos, int type, int data) {
		Direction direction = state.get(FACING);
		if (!world.isClient) {
			boolean bl = this.shouldExtend(world, pos, direction);
			if (bl && (type == 1 || type == 2)) {
				world.setBlockState(pos, state.with(EXTENDED, Boolean.valueOf(true)), 2);
				return false;
			}

			if (!bl && type == 0) {
				return false;
			}
		}

		if (type == 0) {
			if (!this.move(world, pos, direction, true)) {
				return false;
			}

			world.setBlockState(pos, state.with(EXTENDED, Boolean.valueOf(true)), 67);
			world.playSound(null, pos, SoundEvents.BLOCK_PISTON_EXTEND, SoundCategory.BLOCKS, 0.5F, world.random.nextFloat() * 0.25F + 0.6F);
			world.emitGameEvent(GameEvent.PISTON_EXTEND, pos);
		} else if (type == 1 || type == 2) {
			BlockEntity blockEntity = world.getBlockEntity(pos.offset(direction));
			if (blockEntity instanceof PistonBlockEntity) {
				((PistonBlockEntity)blockEntity).finish();
			}

			BlockState blockState = Blocks.MOVING_PISTON
				.getDefaultState()
				.with(PistonExtensionBlock.FACING, direction)
				.with(PistonExtensionBlock.TYPE, this.sticky ? PistonType.STICKY : PistonType.DEFAULT);
			world.setBlockState(pos, blockState, 20);
			world.addBlockEntity(
				PistonExtensionBlock.createBlockEntityPiston(pos, blockState, this.getDefaultState().with(FACING, Direction.byId(data & 7)), direction, false, true)
			);
			world.updateNeighbors(pos, blockState.getBlock());
			blockState.updateNeighbors(world, pos, 2);
			if (this.sticky) {
				BlockPos blockPos = pos.add(direction.getOffsetX() * 2, direction.getOffsetY() * 2, direction.getOffsetZ() * 2);
				BlockState blockState2 = world.getBlockState(blockPos);
				boolean bl2 = false;
				if (blockState2.isOf(Blocks.MOVING_PISTON)
					&& world.getBlockEntity(blockPos) instanceof PistonBlockEntity pistonBlockEntity
					&& pistonBlockEntity.getFacing() == direction
					&& pistonBlockEntity.isExtending()) {
					pistonBlockEntity.finish();
					bl2 = true;
				}

				if (!bl2) {
					if (type != 1
						|| blockState2.isAir()
						|| !isMovable(blockState2, world, blockPos, direction.getOpposite(), false, direction)
						|| blockState2.getPistonBehavior() != PistonBehavior.NORMAL && !blockState2.isOf(Blocks.PISTON) && !blockState2.isOf(Blocks.STICKY_PISTON)) {
						world.removeBlock(pos.offset(direction), false);
					} else {
						this.move(world, pos, direction, false);
					}
				}
			} else {
				world.removeBlock(pos.offset(direction), false);
			}

			world.playSound(null, pos, SoundEvents.BLOCK_PISTON_CONTRACT, SoundCategory.BLOCKS, 0.5F, world.random.nextFloat() * 0.15F + 0.6F);
			world.emitGameEvent(GameEvent.PISTON_CONTRACT, pos);
		}

		return true;
	}

	public static boolean isMovable(BlockState state, World world, BlockPos pos, Direction direction, boolean canBreak, Direction pistonDir) {
		if (pos.getY() < world.getBottomY() || pos.getY() > world.getTopY() - 1 || !world.getWorldBorder().contains(pos)) {
			return false;
		} else if (state.isAir()) {
			return true;
		} else if (state.isOf(Blocks.OBSIDIAN) || state.isOf(Blocks.CRYING_OBSIDIAN) || state.isOf(Blocks.RESPAWN_ANCHOR)) {
			return false;
		} else if (direction == Direction.DOWN && pos.getY() == world.getBottomY()) {
			return false;
		} else if (direction == Direction.UP && pos.getY() == world.getTopY() - 1) {
			return false;
		} else {
			if (!state.isOf(Blocks.PISTON) && !state.isOf(Blocks.STICKY_PISTON)) {
				if (state.getHardness(world, pos) == -1.0F) {
					return false;
				}

				switch (state.getPistonBehavior()) {
					case BLOCK:
						return false;
					case DESTROY:
						return canBreak;
					case PUSH_ONLY:
						return direction == pistonDir;
				}
			} else if ((Boolean)state.get(EXTENDED)) {
				return false;
			}

			return !state.hasBlockEntity();
		}
	}

	private boolean move(World world, BlockPos pos, Direction dir, boolean retract) {
		BlockPos blockPos = pos.offset(dir);
		if (!retract && world.getBlockState(blockPos).isOf(Blocks.PISTON_HEAD)) {
			world.setBlockState(blockPos, Blocks.AIR.getDefaultState(), 20);
		}

		PistonHandler pistonHandler = new PistonHandler(world, pos, dir, retract);
		if (!pistonHandler.calculatePush()) {
			return false;
		} else {
			Map<BlockPos, BlockState> map = Maps.newHashMap();
			List<BlockPos> list = pistonHandler.getMovedBlocks();
			List<BlockState> list2 = Lists.newArrayList();

			for (int i = 0; i < list.size(); i++) {
				BlockPos blockPos2 = (BlockPos)list.get(i);
				BlockState blockState = world.getBlockState(blockPos2);
				list2.add(blockState);
				map.put(blockPos2, blockState);
			}

			List<BlockPos> list3 = pistonHandler.getBrokenBlocks();
			BlockState[] blockStates = new BlockState[list.size() + list3.size()];
			Direction direction = retract ? dir : dir.getOpposite();
			int j = 0;

			for (int k = list3.size() - 1; k >= 0; k--) {
				BlockPos blockPos3 = (BlockPos)list3.get(k);
				BlockState blockState2 = world.getBlockState(blockPos3);
				BlockEntity blockEntity = blockState2.hasBlockEntity() ? world.getBlockEntity(blockPos3) : null;
				dropStacks(blockState2, world, blockPos3, blockEntity);
				world.setBlockState(blockPos3, Blocks.AIR.getDefaultState(), 18);
				if (!blockState2.isIn(BlockTags.FIRE)) {
					world.addBlockBreakParticles(blockPos3, blockState2);
				}

				blockStates[j++] = blockState2;
			}

			for (int l = list.size() - 1; l >= 0; l--) {
				BlockPos blockPos4 = (BlockPos)list.get(l);
				BlockState blockState3 = world.getBlockState(blockPos4);
				blockPos4 = blockPos4.offset(direction);
				map.remove(blockPos4);
				BlockState blockState4 = Blocks.MOVING_PISTON.getDefaultState().with(FACING, dir);
				world.setBlockState(blockPos4, blockState4, 68);
				world.addBlockEntity(PistonExtensionBlock.createBlockEntityPiston(blockPos4, blockState4, (BlockState)list2.get(l), dir, retract, false));
				blockStates[j++] = blockState3;
			}

			if (retract) {
				PistonType pistonType = this.sticky ? PistonType.STICKY : PistonType.DEFAULT;
				BlockState blockState5 = Blocks.PISTON_HEAD.getDefaultState().with(PistonHeadBlock.FACING, dir).with(PistonHeadBlock.TYPE, pistonType);
				BlockState blockState6 = Blocks.MOVING_PISTON
					.getDefaultState()
					.with(PistonExtensionBlock.FACING, dir)
					.with(PistonExtensionBlock.TYPE, this.sticky ? PistonType.STICKY : PistonType.DEFAULT);
				map.remove(blockPos);
				world.setBlockState(blockPos, blockState6, 68);
				world.addBlockEntity(PistonExtensionBlock.createBlockEntityPiston(blockPos, blockState6, blockState5, dir, true, true));
			}

			BlockState blockState7 = Blocks.AIR.getDefaultState();

			for (BlockPos blockPos5 : map.keySet()) {
				world.setBlockState(blockPos5, blockState7, 82);
			}

			for (Entry<BlockPos, BlockState> entry : map.entrySet()) {
				BlockPos blockPos6 = (BlockPos)entry.getKey();
				BlockState blockState8 = (BlockState)entry.getValue();
				blockState8.prepare(world, blockPos6, 2);
				blockState7.updateNeighbors(world, blockPos6, 2);
				blockState7.prepare(world, blockPos6, 2);
			}

			j = 0;

			for (int m = list3.size() - 1; m >= 0; m--) {
				BlockState blockState9 = blockStates[j++];
				BlockPos blockPos7 = (BlockPos)list3.get(m);
				blockState9.prepare(world, blockPos7, 2);
				world.updateNeighborsAlways(blockPos7, blockState9.getBlock());
			}

			for (int n = list.size() - 1; n >= 0; n--) {
				world.updateNeighborsAlways((BlockPos)list.get(n), blockStates[j++].getBlock());
			}

			if (retract) {
				world.updateNeighborsAlways(blockPos, Blocks.PISTON_HEAD);
			}

			return true;
		}
	}

	@Override
	public BlockState rotate(BlockState state, BlockRotation rotation) {
		return state.with(FACING, rotation.rotate(state.get(FACING)));
	}

	@Override
	public BlockState mirror(BlockState state, BlockMirror mirror) {
		return state.rotate(mirror.getRotation(state.get(FACING)));
	}

	@Override
	protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
		builder.add(FACING, EXTENDED);
	}

	@Override
	public boolean hasSidedTransparency(BlockState state) {
		return (Boolean)state.get(EXTENDED);
	}

	@Override
	public boolean canPathfindThrough(BlockState state, BlockView world, BlockPos pos, NavigationType type) {
		return false;
	}
}
