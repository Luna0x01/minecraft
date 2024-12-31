package net.minecraft.block;

import java.util.List;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.PistonBlockEntity;
import net.minecraft.block.material.Material;
import net.minecraft.block.piston.PistonHandler;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.itemgroup.ItemGroup;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

public class PistonBlock extends Block {
	public static final DirectionProperty DIRECTION = DirectionProperty.of("facing");
	public static final BooleanProperty EXTENDED = BooleanProperty.of("extended");
	private final boolean sticky;

	public PistonBlock(boolean bl) {
		super(Material.PISTON);
		this.setDefaultState(this.stateManager.getDefaultState().with(DIRECTION, Direction.NORTH).with(EXTENDED, false));
		this.sticky = bl;
		this.setSound(STONE);
		this.setStrength(0.5F);
		this.setItemGroup(ItemGroup.REDSTONE);
	}

	@Override
	public boolean hasTransparency() {
		return false;
	}

	@Override
	public void onPlaced(World world, BlockPos pos, BlockState state, LivingEntity placer, ItemStack itemStack) {
		world.setBlockState(pos, state.with(DIRECTION, getDirectionFromEntity(world, pos, placer)), 2);
		if (!world.isClient) {
			this.tryMove(world, pos, state);
		}
	}

	@Override
	public void neighborUpdate(World world, BlockPos pos, BlockState state, Block block) {
		if (!world.isClient) {
			this.tryMove(world, pos, state);
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
		return this.getDefaultState().with(DIRECTION, getDirectionFromEntity(world, pos, entity)).with(EXTENDED, false);
	}

	private void tryMove(World world, BlockPos pos, BlockState state) {
		Direction direction = state.get(DIRECTION);
		boolean bl = this.shouldExtend(world, pos, direction);
		if (bl && !(Boolean)state.get(EXTENDED)) {
			if (new PistonHandler(world, pos, direction, true).calculatePush()) {
				world.addBlockAction(pos, this, 0, direction.getId());
			}
		} else if (!bl && (Boolean)state.get(EXTENDED)) {
			world.setBlockState(pos, state.with(EXTENDED, false), 2);
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
	public boolean onEvent(World world, BlockPos pos, BlockState state, int id, int data) {
		Direction direction = state.get(DIRECTION);
		if (!world.isClient) {
			boolean bl = this.shouldExtend(world, pos, direction);
			if (bl && id == 1) {
				world.setBlockState(pos, state.with(EXTENDED, true), 2);
				return false;
			}

			if (!bl && id == 0) {
				return false;
			}
		}

		if (id == 0) {
			if (!this.move(world, pos, direction, true)) {
				return false;
			}

			world.setBlockState(pos, state.with(EXTENDED, true), 2);
			world.playSound(
				(double)pos.getX() + 0.5, (double)pos.getY() + 0.5, (double)pos.getZ() + 0.5, "tile.piston.out", 0.5F, world.random.nextFloat() * 0.25F + 0.6F
			);
		} else if (id == 1) {
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
				Block block = world.getBlockState(blockPos).getBlock();
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
					&& block.getMaterial() != Material.AIR
					&& isMovable(block, world, blockPos, direction.getOpposite(), false)
					&& (block.getPistonInteractionType() == 0 || block == Blocks.PISTON || block == Blocks.STICKY_PISTON)) {
					this.move(world, pos, direction, false);
				}
			} else {
				world.setAir(pos.offset(direction));
			}

			world.playSound(
				(double)pos.getX() + 0.5, (double)pos.getY() + 0.5, (double)pos.getZ() + 0.5, "tile.piston.in", 0.5F, world.random.nextFloat() * 0.15F + 0.6F
			);
		}

		return true;
	}

	@Override
	public void setBoundingBox(BlockView view, BlockPos pos) {
		BlockState blockState = view.getBlockState(pos);
		if (blockState.getBlock() == this && (Boolean)blockState.get(EXTENDED)) {
			float f = 0.25F;
			Direction direction = blockState.get(DIRECTION);
			if (direction != null) {
				switch (direction) {
					case DOWN:
						this.setBoundingBox(0.0F, 0.25F, 0.0F, 1.0F, 1.0F, 1.0F);
						break;
					case UP:
						this.setBoundingBox(0.0F, 0.0F, 0.0F, 1.0F, 0.75F, 1.0F);
						break;
					case NORTH:
						this.setBoundingBox(0.0F, 0.0F, 0.25F, 1.0F, 1.0F, 1.0F);
						break;
					case SOUTH:
						this.setBoundingBox(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 0.75F);
						break;
					case WEST:
						this.setBoundingBox(0.25F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
						break;
					case EAST:
						this.setBoundingBox(0.0F, 0.0F, 0.0F, 0.75F, 1.0F, 1.0F);
				}
			}
		} else {
			this.setBoundingBox(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
		}
	}

	@Override
	public void setBlockItemBounds() {
		this.setBoundingBox(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
	}

	@Override
	public void appendCollisionBoxes(World world, BlockPos pos, BlockState state, Box box, List<Box> list, Entity entity) {
		this.setBoundingBox(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
		super.appendCollisionBoxes(world, pos, state, box, list, entity);
	}

	@Override
	public Box getCollisionBox(World world, BlockPos pos, BlockState state) {
		this.setBoundingBox(world, pos);
		return super.getCollisionBox(world, pos, state);
	}

	@Override
	public boolean renderAsNormalBlock() {
		return false;
	}

	public static Direction getDirection(int id) {
		int i = id & 7;
		return i > 5 ? null : Direction.getById(i);
	}

	public static Direction getDirectionFromEntity(World world, BlockPos pos, LivingEntity entity) {
		if (MathHelper.abs((float)entity.x - (float)pos.getX()) < 2.0F && MathHelper.abs((float)entity.z - (float)pos.getZ()) < 2.0F) {
			double d = entity.y + (double)entity.getEyeHeight();
			if (d - (double)pos.getY() > 2.0) {
				return Direction.UP;
			}

			if ((double)pos.getY() - d > 0.0) {
				return Direction.DOWN;
			}
		}

		return entity.getHorizontalDirection().getOpposite();
	}

	public static boolean isMovable(Block block, World world, BlockPos pos, Direction direction, boolean canBreak) {
		if (block == Blocks.OBSIDIAN) {
			return false;
		} else if (!world.getWorldBorder().contains(pos)) {
			return false;
		} else if (pos.getY() >= 0 && (direction != Direction.DOWN || pos.getY() != 0)) {
			if (pos.getY() <= world.getMaxBuildHeight() - 1 && (direction != Direction.UP || pos.getY() != world.getMaxBuildHeight() - 1)) {
				if (block != Blocks.PISTON && block != Blocks.STICKY_PISTON) {
					if (block.getStrength(world, pos) == -1.0F) {
						return false;
					}

					if (block.getPistonInteractionType() == 2) {
						return false;
					}

					if (block.getPistonInteractionType() == 1) {
						if (!canBreak) {
							return false;
						}

						return true;
					}
				} else if ((Boolean)world.getBlockState(pos).get(EXTENDED)) {
					return false;
				}

				return !(block instanceof BlockEntityProvider);
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
		List<BlockPos> list = pistonHandler.getMovedBlocks();
		List<BlockPos> list2 = pistonHandler.getBrokenBlocks();
		if (!pistonHandler.calculatePush()) {
			return false;
		} else {
			int i = list.size() + list2.size();
			Block[] blocks = new Block[i];
			Direction direction = retract ? dir : dir.getOpposite();

			for (int j = list2.size() - 1; j >= 0; j--) {
				BlockPos blockPos = (BlockPos)list2.get(j);
				Block block = world.getBlockState(blockPos).getBlock();
				block.dropAsItem(world, blockPos, world.getBlockState(blockPos), 0);
				world.setAir(blockPos);
				i--;
				blocks[i] = block;
			}

			for (int k = list.size() - 1; k >= 0; k--) {
				BlockPos blockPos2 = (BlockPos)list.get(k);
				BlockState blockState = world.getBlockState(blockPos2);
				Block block2 = blockState.getBlock();
				int l = block2.getData(blockState);
				world.setAir(blockPos2);
				blockPos2 = blockPos2.offset(direction);
				world.setBlockState(blockPos2, Blocks.PISTON_EXTENSION.getDefaultState().with(DIRECTION, dir), 4);
				world.setBlockEntity(blockPos2, PistonExtensionBlock.createPistonEntity(blockState, dir, retract, false));
				i--;
				blocks[i] = block2;
			}

			BlockPos blockPos3 = pos.offset(dir);
			if (retract) {
				PistonHeadBlock.PistonHeadType pistonHeadType = this.sticky ? PistonHeadBlock.PistonHeadType.STICKY : PistonHeadBlock.PistonHeadType.DEFAULT;
				BlockState blockState2 = Blocks.PISTON_HEAD.getDefaultState().with(PistonHeadBlock.FACING, dir).with(PistonHeadBlock.TYPE, pistonHeadType);
				BlockState blockState3 = Blocks.PISTON_EXTENSION
					.getDefaultState()
					.with(PistonExtensionBlock.FACING, dir)
					.with(PistonExtensionBlock.TYPE, this.sticky ? PistonHeadBlock.PistonHeadType.STICKY : PistonHeadBlock.PistonHeadType.DEFAULT);
				world.setBlockState(blockPos3, blockState3, 4);
				world.setBlockEntity(blockPos3, PistonExtensionBlock.createPistonEntity(blockState2, dir, true, false));
			}

			for (int m = list2.size() - 1; m >= 0; m--) {
				world.updateNeighborsAlways((BlockPos)list2.get(m), blocks[i++]);
			}

			for (int n = list.size() - 1; n >= 0; n--) {
				world.updateNeighborsAlways((BlockPos)list.get(n), blocks[i++]);
			}

			if (retract) {
				world.updateNeighborsAlways(blockPos3, Blocks.PISTON_HEAD);
				world.updateNeighborsAlways(pos, this);
			}

			return true;
		}
	}

	@Override
	public BlockState getRenderState(BlockState state) {
		return this.getDefaultState().with(DIRECTION, Direction.UP);
	}

	@Override
	public BlockState stateFromData(int data) {
		return this.getDefaultState().with(DIRECTION, getDirection(data)).with(EXTENDED, (data & 8) > 0);
	}

	@Override
	public int getData(BlockState state) {
		int i = 0;
		i |= ((Direction)state.get(DIRECTION)).getId();
		if ((Boolean)state.get(EXTENDED)) {
			i |= 8;
		}

		return i;
	}

	@Override
	protected StateManager appendProperties() {
		return new StateManager(this, DIRECTION, EXTENDED);
	}
}
