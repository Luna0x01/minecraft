package net.minecraft.block;

import com.google.common.collect.Lists;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.PistonBlockEntity;
import net.minecraft.block.material.Material;
import net.minecraft.block.piston.PistonBehavior;
import net.minecraft.block.piston.PistonHandler;
import net.minecraft.client.sound.SoundCategory;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.itemgroup.ItemGroup;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.sound.Sounds;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

public class PistonBlock extends FacingBlock {
	public static final BooleanProperty EXTENDED = BooleanProperty.of("extended");
	protected static final Box field_12881 = new Box(0.0, 0.0, 0.0, 0.75, 1.0, 1.0);
	protected static final Box field_12882 = new Box(0.25, 0.0, 0.0, 1.0, 1.0, 1.0);
	protected static final Box field_12883 = new Box(0.0, 0.0, 0.0, 1.0, 1.0, 0.75);
	protected static final Box field_12884 = new Box(0.0, 0.0, 0.25, 1.0, 1.0, 1.0);
	protected static final Box field_12885 = new Box(0.0, 0.0, 0.0, 1.0, 0.75, 1.0);
	protected static final Box field_12886 = new Box(0.0, 0.25, 0.0, 1.0, 1.0, 1.0);
	private final boolean sticky;

	public PistonBlock(boolean bl) {
		super(Material.PISTON);
		this.setDefaultState(this.stateManager.getDefaultState().with(FACING, Direction.NORTH).with(EXTENDED, false));
		this.sticky = bl;
		this.setBlockSoundGroup(BlockSoundGroup.STONE);
		this.setStrength(0.5F);
		this.setItemGroup(ItemGroup.REDSTONE);
	}

	@Override
	public Box getCollisionBox(BlockState state, BlockView view, BlockPos pos) {
		if ((Boolean)state.get(EXTENDED)) {
			switch ((Direction)state.get(FACING)) {
				case DOWN:
					return field_12886;
				case UP:
				default:
					return field_12885;
				case NORTH:
					return field_12884;
				case SOUTH:
					return field_12883;
				case WEST:
					return field_12882;
				case EAST:
					return field_12881;
			}
		} else {
			return collisionBox;
		}
	}

	@Override
	public boolean method_11568(BlockState state) {
		return !(Boolean)state.get(EXTENDED) || state.get(FACING) == Direction.DOWN;
	}

	@Override
	public void appendCollisionBoxes(BlockState state, World world, BlockPos pos, Box entityBox, List<Box> boxes, @Nullable Entity entity) {
		appendCollisionBoxes(pos, entityBox, boxes, state.getCollisionBox((BlockView)world, pos));
	}

	@Override
	public boolean isFullBoundsCubeForCulling(BlockState blockState) {
		return false;
	}

	@Override
	public void onPlaced(World world, BlockPos pos, BlockState state, LivingEntity placer, ItemStack itemStack) {
		world.setBlockState(pos, state.with(FACING, method_9000(pos, placer)), 2);
		if (!world.isClient) {
			this.tryMove(world, pos, state);
		}
	}

	@Override
	public void method_8641(BlockState blockState, World world, BlockPos blockPos, Block block) {
		if (!world.isClient) {
			this.tryMove(world, blockPos, blockState);
		}
	}

	@Override
	public void onCreation(World world, BlockPos pos, BlockState state) {
		if (!world.isClient && world.getBlockEntity(pos) == null) {
			this.tryMove(world, pos, state);
		}
	}

	@Override
	public BlockState getStateFromData(World world, BlockPos pos, Direction dir, float x, float y, float z, int id, LivingEntity entity) {
		return this.getDefaultState().with(FACING, method_9000(pos, entity)).with(EXTENDED, false);
	}

	private void tryMove(World world, BlockPos pos, BlockState state) {
		Direction direction = state.get(FACING);
		boolean bl = this.shouldExtend(world, pos, direction);
		if (bl && !(Boolean)state.get(EXTENDED)) {
			if (new PistonHandler(world, pos, direction, true).calculatePush()) {
				world.addBlockAction(pos, this, 0, direction.getId());
			}
		} else if (!bl && (Boolean)state.get(EXTENDED)) {
			world.addBlockAction(pos, this, 1, direction.getId());
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
			if (bl && type == 1) {
				world.setBlockState(pos, state.with(EXTENDED, true), 2);
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

			world.setBlockState(pos, state.with(EXTENDED, true), 2);
			world.method_11486(null, pos, Sounds.BLOCK_PISTON_EXTEND, SoundCategory.BLOCKS, 0.5F, world.random.nextFloat() * 0.25F + 0.6F);
		} else if (type == 1) {
			BlockEntity blockEntity = world.getBlockEntity(pos.offset(direction));
			if (blockEntity instanceof PistonBlockEntity) {
				((PistonBlockEntity)blockEntity).finish();
			}

			world.setBlockState(
				pos,
				Blocks.PISTON_EXTENSION
					.getDefaultState()
					.with(PistonExtensionBlock.FACING, direction)
					.with(PistonExtensionBlock.TYPE, this.sticky ? PistonHeadBlock.PistonHeadType.STICKY : PistonHeadBlock.PistonHeadType.DEFAULT),
				3
			);
			world.setBlockEntity(pos, PistonExtensionBlock.createPistonEntity(this.stateFromData(data), direction, false, true));
			if (this.sticky) {
				BlockPos blockPos = pos.add(direction.getOffsetX() * 2, direction.getOffsetY() * 2, direction.getOffsetZ() * 2);
				BlockState blockState = world.getBlockState(blockPos);
				Block block = blockState.getBlock();
				boolean bl2 = false;
				if (block == Blocks.PISTON_EXTENSION) {
					BlockEntity blockEntity2 = world.getBlockEntity(blockPos);
					if (blockEntity2 instanceof PistonBlockEntity) {
						PistonBlockEntity pistonBlockEntity = (PistonBlockEntity)blockEntity2;
						if (pistonBlockEntity.getFacing() == direction && pistonBlockEntity.isExtending()) {
							pistonBlockEntity.finish();
							bl2 = true;
						}
					}
				}

				if (!bl2
					&& blockState.getMaterial() != Material.AIR
					&& method_9001(blockState, world, blockPos, direction.getOpposite(), false)
					&& (blockState.getPistonBehavior() == PistonBehavior.NORMAL || block == Blocks.PISTON || block == Blocks.STICKY_PISTON)) {
					this.move(world, pos, direction, false);
				}
			} else {
				world.setAir(pos.offset(direction));
			}

			world.method_11486(null, pos, Sounds.BLOCK_PISTON_CONTRACT, SoundCategory.BLOCKS, 0.5F, world.random.nextFloat() * 0.15F + 0.6F);
		}

		return true;
	}

	@Override
	public boolean method_11562(BlockState state) {
		return false;
	}

	@Nullable
	public static Direction getDirection(int id) {
		int i = id & 7;
		return i > 5 ? null : Direction.getById(i);
	}

	public static Direction method_9000(BlockPos blockPos, LivingEntity livingEntity) {
		if (MathHelper.abs((float)livingEntity.x - (float)blockPos.getX()) < 2.0F && MathHelper.abs((float)livingEntity.z - (float)blockPos.getZ()) < 2.0F) {
			double d = livingEntity.y + (double)livingEntity.getEyeHeight();
			if (d - (double)blockPos.getY() > 2.0) {
				return Direction.UP;
			}

			if ((double)blockPos.getY() - d > 0.0) {
				return Direction.DOWN;
			}
		}

		return livingEntity.getHorizontalDirection().getOpposite();
	}

	public static boolean method_9001(BlockState blockState, World world, BlockPos blockPos, Direction direction, boolean bl) {
		Block block = blockState.getBlock();
		if (block == Blocks.OBSIDIAN) {
			return false;
		} else if (!world.getWorldBorder().contains(blockPos)) {
			return false;
		} else if (blockPos.getY() >= 0 && (direction != Direction.DOWN || blockPos.getY() != 0)) {
			if (blockPos.getY() <= world.getMaxBuildHeight() - 1 && (direction != Direction.UP || blockPos.getY() != world.getMaxBuildHeight() - 1)) {
				if (block != Blocks.PISTON && block != Blocks.STICKY_PISTON) {
					if (blockState.getHardness(world, blockPos) == -1.0F) {
						return false;
					}

					if (blockState.getPistonBehavior() == PistonBehavior.BLOCK) {
						return false;
					}

					if (blockState.getPistonBehavior() == PistonBehavior.DESTROY) {
						return bl;
					}
				} else if ((Boolean)blockState.get(EXTENDED)) {
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
		if (!retract) {
			world.setAir(pos.offset(dir));
		}

		PistonHandler pistonHandler = new PistonHandler(world, pos, dir, retract);
		if (!pistonHandler.calculatePush()) {
			return false;
		} else {
			List<BlockPos> list = pistonHandler.getMovedBlocks();
			List<BlockState> list2 = Lists.newArrayList();

			for (int i = 0; i < list.size(); i++) {
				BlockPos blockPos = (BlockPos)list.get(i);
				list2.add(world.getBlockState(blockPos).getBlockState(world, blockPos));
			}

			List<BlockPos> list3 = pistonHandler.getBrokenBlocks();
			int j = list.size() + list3.size();
			BlockState[] blockStates = new BlockState[j];
			Direction direction = retract ? dir : dir.getOpposite();

			for (int k = list3.size() - 1; k >= 0; k--) {
				BlockPos blockPos2 = (BlockPos)list3.get(k);
				BlockState blockState = world.getBlockState(blockPos2);
				blockState.getBlock().dropAsItem(world, blockPos2, blockState, 0);
				world.setAir(blockPos2);
				j--;
				blockStates[j] = blockState;
			}

			for (int l = list.size() - 1; l >= 0; l--) {
				BlockPos blockPos3 = (BlockPos)list.get(l);
				BlockState blockState2 = world.getBlockState(blockPos3);
				world.setBlockState(blockPos3, Blocks.AIR.getDefaultState(), 2);
				blockPos3 = blockPos3.offset(direction);
				world.setBlockState(blockPos3, Blocks.PISTON_EXTENSION.getDefaultState().with(FACING, dir), 4);
				world.setBlockEntity(blockPos3, PistonExtensionBlock.createPistonEntity((BlockState)list2.get(l), dir, retract, false));
				j--;
				blockStates[j] = blockState2;
			}

			BlockPos blockPos4 = pos.offset(dir);
			if (retract) {
				PistonHeadBlock.PistonHeadType pistonHeadType = this.sticky ? PistonHeadBlock.PistonHeadType.STICKY : PistonHeadBlock.PistonHeadType.DEFAULT;
				BlockState blockState3 = Blocks.PISTON_HEAD.getDefaultState().with(PistonHeadBlock.FACING, dir).with(PistonHeadBlock.TYPE, pistonHeadType);
				BlockState blockState4 = Blocks.PISTON_EXTENSION
					.getDefaultState()
					.with(PistonExtensionBlock.FACING, dir)
					.with(PistonExtensionBlock.TYPE, this.sticky ? PistonHeadBlock.PistonHeadType.STICKY : PistonHeadBlock.PistonHeadType.DEFAULT);
				world.setBlockState(blockPos4, blockState4, 4);
				world.setBlockEntity(blockPos4, PistonExtensionBlock.createPistonEntity(blockState3, dir, true, false));
			}

			for (int m = list3.size() - 1; m >= 0; m--) {
				world.updateNeighborsAlways((BlockPos)list3.get(m), blockStates[j++].getBlock());
			}

			for (int n = list.size() - 1; n >= 0; n--) {
				world.updateNeighborsAlways((BlockPos)list.get(n), blockStates[j++].getBlock());
			}

			if (retract) {
				world.updateNeighborsAlways(blockPos4, Blocks.PISTON_HEAD);
				world.updateNeighborsAlways(pos, this);
			}

			return true;
		}
	}

	@Override
	public BlockState stateFromData(int data) {
		return this.getDefaultState().with(FACING, getDirection(data)).with(EXTENDED, (data & 8) > 0);
	}

	@Override
	public int getData(BlockState state) {
		int i = 0;
		i |= ((Direction)state.get(FACING)).getId();
		if ((Boolean)state.get(EXTENDED)) {
			i |= 8;
		}

		return i;
	}

	@Override
	public BlockState withRotation(BlockState state, BlockRotation rotation) {
		return state.with(FACING, rotation.rotate(state.get(FACING)));
	}

	@Override
	public BlockState withMirror(BlockState state, BlockMirror mirror) {
		return state.withRotation(mirror.getRotation(state.get(FACING)));
	}

	@Override
	protected StateManager appendProperties() {
		return new StateManager(this, FACING, EXTENDED);
	}
}
