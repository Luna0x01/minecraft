package net.minecraft.block;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import java.util.List;
import java.util.Set;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.PistonBlockEntity;
import net.minecraft.block.enums.PistonType;
import net.minecraft.block.piston.PistonBehavior;
import net.minecraft.block.piston.PistonHandler;
import net.minecraft.client.sound.SoundCategory;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.Sounds;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.states.property.Properties;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shapes.VoxelShape;
import net.minecraft.util.shapes.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

public class PistonBlock extends FacingBlock {
	public static final BooleanProperty field_18654 = Properties.EXTENDED;
	protected static final VoxelShape field_18655 = Block.createCuboidShape(0.0, 0.0, 0.0, 12.0, 16.0, 16.0);
	protected static final VoxelShape field_18656 = Block.createCuboidShape(4.0, 0.0, 0.0, 16.0, 16.0, 16.0);
	protected static final VoxelShape field_18657 = Block.createCuboidShape(0.0, 0.0, 0.0, 16.0, 16.0, 12.0);
	protected static final VoxelShape field_18658 = Block.createCuboidShape(0.0, 0.0, 4.0, 16.0, 16.0, 16.0);
	protected static final VoxelShape field_18659 = Block.createCuboidShape(0.0, 0.0, 0.0, 16.0, 12.0, 16.0);
	protected static final VoxelShape field_18660 = Block.createCuboidShape(0.0, 4.0, 0.0, 16.0, 16.0, 16.0);
	private final boolean sticky;

	public PistonBlock(boolean bl, Block.Builder builder) {
		super(builder);
		this.setDefaultState(this.stateManager.method_16923().withProperty(FACING, Direction.NORTH).withProperty(field_18654, Boolean.valueOf(false)));
		this.sticky = bl;
	}

	@Override
	public boolean method_13703(BlockState state) {
		return !(Boolean)state.getProperty(field_18654);
	}

	@Override
	public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos) {
		if ((Boolean)state.getProperty(field_18654)) {
			switch ((Direction)state.getProperty(FACING)) {
				case DOWN:
					return field_18660;
				case UP:
				default:
					return field_18659;
				case NORTH:
					return field_18658;
				case SOUTH:
					return field_18657;
				case WEST:
					return field_18656;
				case EAST:
					return field_18655;
			}
		} else {
			return VoxelShapes.matchesAnywhere();
		}
	}

	@Override
	public boolean method_11568(BlockState state) {
		return !(Boolean)state.getProperty(field_18654) || state.getProperty(FACING) == Direction.DOWN;
	}

	@Override
	public void onPlaced(World world, BlockPos pos, BlockState state, LivingEntity placer, ItemStack itemStack) {
		if (!world.isClient) {
			this.tryMove(world, pos, state);
		}
	}

	@Override
	public void neighborUpdate(BlockState state, World world, BlockPos pos, Block block, BlockPos neighborPos) {
		if (!world.isClient) {
			this.tryMove(world, pos, state);
		}
	}

	@Override
	public void onBlockAdded(BlockState state, World world, BlockPos pos, BlockState oldState) {
		if (oldState.getBlock() != state.getBlock()) {
			if (!world.isClient && world.getBlockEntity(pos) == null) {
				this.tryMove(world, pos, state);
			}
		}
	}

	@Override
	public BlockState getPlacementState(ItemPlacementContext context) {
		return this.getDefaultState().withProperty(FACING, context.method_16020().getOpposite()).withProperty(field_18654, Boolean.valueOf(false));
	}

	private void tryMove(World world, BlockPos pos, BlockState state) {
		Direction direction = state.getProperty(FACING);
		boolean bl = this.shouldExtend(world, pos, direction);
		if (bl && !(Boolean)state.getProperty(field_18654)) {
			if (new PistonHandler(world, pos, direction, true).calculatePush()) {
				world.addBlockAction(pos, this, 0, direction.getId());
			}
		} else if (!bl && (Boolean)state.getProperty(field_18654)) {
			BlockPos blockPos = pos.offset(direction, 2);
			BlockState blockState = world.getBlockState(blockPos);
			int i = 1;
			if (blockState.getBlock() == Blocks.MOVING_PISTON && blockState.getProperty(FACING) == direction) {
				BlockEntity blockEntity = world.getBlockEntity(blockPos);
				if (blockEntity instanceof PistonBlockEntity) {
					PistonBlockEntity pistonBlockEntity = (PistonBlockEntity)blockEntity;
					if (pistonBlockEntity.isExtending()
						&& (
							pistonBlockEntity.getAmountExtended(0.0F) < 0.5F || world.getLastUpdateTime() == pistonBlockEntity.method_16855() || ((ServerWorld)world).method_21266()
						)) {
						i = 2;
					}
				}
			}

			world.addBlockAction(pos, this, i, direction.getId());
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
		Direction direction = state.getProperty(FACING);
		if (!world.isClient) {
			boolean bl = this.shouldExtend(world, pos, direction);
			if (bl && (type == 1 || type == 2)) {
				world.setBlockState(pos, state.withProperty(field_18654, Boolean.valueOf(true)), 2);
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

			world.setBlockState(pos, state.withProperty(field_18654, Boolean.valueOf(true)), 67);
			world.playSound(null, pos, Sounds.BLOCK_PISTON_EXTEND, SoundCategory.BLOCKS, 0.5F, world.random.nextFloat() * 0.25F + 0.6F);
		} else if (type == 1 || type == 2) {
			BlockEntity blockEntity = world.getBlockEntity(pos.offset(direction));
			if (blockEntity instanceof PistonBlockEntity) {
				((PistonBlockEntity)blockEntity).finish();
			}

			world.setBlockState(
				pos,
				Blocks.MOVING_PISTON
					.getDefaultState()
					.withProperty(PistonExtensionBlock.FACING, direction)
					.withProperty(PistonExtensionBlock.TYPE, this.sticky ? PistonType.STICKY : PistonType.DEFAULT),
				3
			);
			world.setBlockEntity(
				pos, PistonExtensionBlock.createPistonEntity(this.getDefaultState().withProperty(FACING, Direction.getById(data & 7)), direction, false, true)
			);
			if (this.sticky) {
				BlockPos blockPos = pos.add(direction.getOffsetX() * 2, direction.getOffsetY() * 2, direction.getOffsetZ() * 2);
				BlockState blockState = world.getBlockState(blockPos);
				Block block = blockState.getBlock();
				boolean bl2 = false;
				if (block == Blocks.MOVING_PISTON) {
					BlockEntity blockEntity2 = world.getBlockEntity(blockPos);
					if (blockEntity2 instanceof PistonBlockEntity) {
						PistonBlockEntity pistonBlockEntity = (PistonBlockEntity)blockEntity2;
						if (pistonBlockEntity.getFacing() == direction && pistonBlockEntity.isExtending()) {
							pistonBlockEntity.finish();
							bl2 = true;
						}
					}
				}

				if (!bl2) {
					if (type != 1
						|| blockState.isAir()
						|| !method_9001(blockState, world, blockPos, direction.getOpposite(), false, direction)
						|| blockState.getPistonBehavior() != PistonBehavior.NORMAL && block != Blocks.PISTON && block != Blocks.STICKY_PISTON) {
						world.method_8553(pos.offset(direction));
					} else {
						this.move(world, pos, direction, false);
					}
				}
			} else {
				world.method_8553(pos.offset(direction));
			}

			world.playSound(null, pos, Sounds.BLOCK_PISTON_CONTRACT, SoundCategory.BLOCKS, 0.5F, world.random.nextFloat() * 0.15F + 0.6F);
		}

		return true;
	}

	@Override
	public boolean method_11562(BlockState state) {
		return false;
	}

	public static boolean method_9001(BlockState blockState, World world, BlockPos blockPos, Direction direction, boolean bl, Direction direction2) {
		Block block = blockState.getBlock();
		if (block == Blocks.OBSIDIAN) {
			return false;
		} else if (!world.method_8524().contains(blockPos)) {
			return false;
		} else if (blockPos.getY() >= 0 && (direction != Direction.DOWN || blockPos.getY() != 0)) {
			if (blockPos.getY() <= world.getMaxBuildHeight() - 1 && (direction != Direction.UP || blockPos.getY() != world.getMaxBuildHeight() - 1)) {
				if (block != Blocks.PISTON && block != Blocks.STICKY_PISTON) {
					if (blockState.getHardness(world, blockPos) == -1.0F) {
						return false;
					}

					switch (blockState.getPistonBehavior()) {
						case BLOCK:
							return false;
						case DESTROY:
							return bl;
						case PUSH_ONLY:
							return direction == direction2;
					}
				} else if ((Boolean)blockState.getProperty(field_18654)) {
					return false;
				}

				return !block.hasBlockEntity();
			} else {
				return false;
			}
		} else {
			return false;
		}
	}

	private boolean move(World world, BlockPos pos, Direction dir, boolean retract) {
		BlockPos blockPos = pos.offset(dir);
		if (!retract && world.getBlockState(blockPos).getBlock() == Blocks.PISTON_HEAD) {
			world.setBlockState(blockPos, Blocks.AIR.getDefaultState(), 20);
		}

		PistonHandler pistonHandler = new PistonHandler(world, pos, dir, retract);
		if (!pistonHandler.calculatePush()) {
			return false;
		} else {
			List<BlockPos> list = pistonHandler.getMovedBlocks();
			List<BlockState> list2 = Lists.newArrayList();

			for (int i = 0; i < list.size(); i++) {
				BlockPos blockPos2 = (BlockPos)list.get(i);
				list2.add(world.getBlockState(blockPos2));
			}

			List<BlockPos> list3 = pistonHandler.getBrokenBlocks();
			int j = list.size() + list3.size();
			BlockState[] blockStates = new BlockState[j];
			Direction direction = retract ? dir : dir.getOpposite();
			Set<BlockPos> set = Sets.newHashSet(list);

			for (int k = list3.size() - 1; k >= 0; k--) {
				BlockPos blockPos3 = (BlockPos)list3.get(k);
				BlockState blockState = world.getBlockState(blockPos3);
				blockState.method_16867(world, blockPos3, 0);
				world.setBlockState(blockPos3, Blocks.AIR.getDefaultState(), 18);
				j--;
				blockStates[j] = blockState;
			}

			for (int l = list.size() - 1; l >= 0; l--) {
				BlockPos blockPos4 = (BlockPos)list.get(l);
				BlockState blockState2 = world.getBlockState(blockPos4);
				blockPos4 = blockPos4.offset(direction);
				set.remove(blockPos4);
				world.setBlockState(blockPos4, Blocks.MOVING_PISTON.getDefaultState().withProperty(FACING, dir), 68);
				world.setBlockEntity(blockPos4, PistonExtensionBlock.createPistonEntity((BlockState)list2.get(l), dir, retract, false));
				j--;
				blockStates[j] = blockState2;
			}

			if (retract) {
				PistonType pistonType = this.sticky ? PistonType.STICKY : PistonType.DEFAULT;
				BlockState blockState3 = Blocks.PISTON_HEAD
					.getDefaultState()
					.withProperty(PistonHeadBlock.FACING, dir)
					.withProperty(PistonHeadBlock.field_18667, pistonType);
				BlockState blockState4 = Blocks.MOVING_PISTON
					.getDefaultState()
					.withProperty(PistonExtensionBlock.FACING, dir)
					.withProperty(PistonExtensionBlock.TYPE, this.sticky ? PistonType.STICKY : PistonType.DEFAULT);
				set.remove(blockPos);
				world.setBlockState(blockPos, blockState4, 68);
				world.setBlockEntity(blockPos, PistonExtensionBlock.createPistonEntity(blockState3, dir, true, true));
			}

			for (BlockPos blockPos5 : set) {
				world.setBlockState(blockPos5, Blocks.AIR.getDefaultState(), 66);
			}

			for (int m = list3.size() - 1; m >= 0; m--) {
				BlockState blockState5 = blockStates[j++];
				BlockPos blockPos6 = (BlockPos)list3.get(m);
				blockState5.method_16888(world, blockPos6, 2);
				world.updateNeighborsAlways(blockPos6, blockState5.getBlock());
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
	public BlockState withRotation(BlockState state, BlockRotation rotation) {
		return state.withProperty(FACING, rotation.rotate(state.getProperty(FACING)));
	}

	@Override
	public BlockState withMirror(BlockState state, BlockMirror mirror) {
		return state.rotate(mirror.getRotation(state.getProperty(FACING)));
	}

	@Override
	protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
		builder.method_16928(FACING, field_18654);
	}

	@Override
	public BlockRenderLayer getRenderLayer(BlockView world, BlockState state, BlockPos pos, Direction direction) {
		return state.getProperty(FACING) != direction.getOpposite() && state.getProperty(field_18654) ? BlockRenderLayer.UNDEFINED : BlockRenderLayer.SOLID;
	}

	@Override
	public int getLightSubtracted(BlockState state, BlockView world, BlockPos pos) {
		return 0;
	}

	@Override
	public boolean canPlaceAtSide(BlockState state, BlockView world, BlockPos pos, BlockPlacementEnvironment environment) {
		return false;
	}
}
