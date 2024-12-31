package net.minecraft.block;

import java.util.Random;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.PistonBlockEntity;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

public class PistonExtensionBlock extends BlockWithEntity {
	public static final DirectionProperty FACING = PistonHeadBlock.FACING;
	public static final EnumProperty<PistonHeadBlock.PistonHeadType> TYPE = PistonHeadBlock.TYPE;

	public PistonExtensionBlock() {
		super(Material.PISTON);
		this.setDefaultState(this.stateManager.getDefaultState().with(FACING, Direction.NORTH).with(TYPE, PistonHeadBlock.PistonHeadType.DEFAULT));
		this.setStrength(-1.0F);
	}

	@Override
	public BlockEntity createBlockEntity(World world, int id) {
		return null;
	}

	public static BlockEntity createPistonEntity(BlockState pushedBlock, Direction dir, boolean extending, boolean source) {
		return new PistonBlockEntity(pushedBlock, dir, extending, source);
	}

	@Override
	public void onBreaking(World world, BlockPos pos, BlockState state) {
		BlockEntity blockEntity = world.getBlockEntity(pos);
		if (blockEntity instanceof PistonBlockEntity) {
			((PistonBlockEntity)blockEntity).finish();
		} else {
			super.onBreaking(world, pos, state);
		}
	}

	@Override
	public boolean canBePlacedAtPos(World world, BlockPos pos) {
		return false;
	}

	@Override
	public boolean canBePlacedAdjacent(World world, BlockPos pos, Direction direction) {
		return false;
	}

	@Override
	public void onBreakByPlayer(World world, BlockPos pos, BlockState state) {
		BlockPos blockPos = pos.offset(((Direction)state.get(FACING)).getOpposite());
		BlockState blockState = world.getBlockState(blockPos);
		if (blockState.getBlock() instanceof PistonBlock && (Boolean)blockState.get(PistonBlock.EXTENDED)) {
			world.setAir(blockPos);
		}
	}

	@Override
	public boolean hasTransparency() {
		return false;
	}

	@Override
	public boolean renderAsNormalBlock() {
		return false;
	}

	@Override
	public boolean onUse(World world, BlockPos pos, BlockState state, PlayerEntity player, Direction direction, float posX, float posY, float posZ) {
		if (!world.isClient && world.getBlockEntity(pos) == null) {
			world.setAir(pos);
			return true;
		} else {
			return false;
		}
	}

	@Override
	public Item getDropItem(BlockState state, Random random, int id) {
		return null;
	}

	@Override
	public void randomDropAsItem(World world, BlockPos pos, BlockState state, float chance, int id) {
		if (!world.isClient) {
			PistonBlockEntity pistonBlockEntity = this.getPistonEntity(world, pos);
			if (pistonBlockEntity != null) {
				BlockState blockState = pistonBlockEntity.getPushedBlock();
				blockState.getBlock().dropAsItem(world, pos, blockState, 0);
			}
		}
	}

	@Override
	public BlockHitResult rayTrace(World world, BlockPos pos, Vec3d start, Vec3d end) {
		return null;
	}

	@Override
	public void neighborUpdate(World world, BlockPos pos, BlockState state, Block block) {
		if (!world.isClient) {
			world.getBlockEntity(pos);
		}
	}

	@Override
	public Box getCollisionBox(World world, BlockPos pos, BlockState state) {
		PistonBlockEntity pistonBlockEntity = this.getPistonEntity(world, pos);
		if (pistonBlockEntity == null) {
			return null;
		} else {
			float f = pistonBlockEntity.getAmountExtended(0.0F);
			if (pistonBlockEntity.isExtending()) {
				f = 1.0F - f;
			}

			return this.getCollisionBox(world, pos, pistonBlockEntity.getPushedBlock(), f, pistonBlockEntity.getFacing());
		}
	}

	@Override
	public void setBoundingBox(BlockView view, BlockPos pos) {
		PistonBlockEntity pistonBlockEntity = this.getPistonEntity(view, pos);
		if (pistonBlockEntity != null) {
			BlockState blockState = pistonBlockEntity.getPushedBlock();
			Block block = blockState.getBlock();
			if (block == this || block.getMaterial() == Material.AIR) {
				return;
			}

			float f = pistonBlockEntity.getAmountExtended(0.0F);
			if (pistonBlockEntity.isExtending()) {
				f = 1.0F - f;
			}

			block.setBoundingBox(view, pos);
			if (block == Blocks.PISTON || block == Blocks.STICKY_PISTON) {
				f = 0.0F;
			}

			Direction direction = pistonBlockEntity.getFacing();
			this.boundingBoxMinX = block.getMinX() - (double)((float)direction.getOffsetX() * f);
			this.boundingBoxMinY = block.getMinY() - (double)((float)direction.getOffsetY() * f);
			this.boundingBoxMinZ = block.getMinZ() - (double)((float)direction.getOffsetZ() * f);
			this.boundingBoxMaxX = block.getMaxX() - (double)((float)direction.getOffsetX() * f);
			this.boundingBoxMaxY = block.getMaxY() - (double)((float)direction.getOffsetY() * f);
			this.boundingBoxMaxZ = block.getMaxZ() - (double)((float)direction.getOffsetZ() * f);
		}
	}

	public Box getCollisionBox(World world, BlockPos pos, BlockState state, float progress, Direction dir) {
		if (state.getBlock() != this && state.getBlock().getMaterial() != Material.AIR) {
			Box box = state.getBlock().getCollisionBox(world, pos, state);
			if (box == null) {
				return null;
			} else {
				double d = box.minX;
				double e = box.minY;
				double f = box.minZ;
				double g = box.maxX;
				double h = box.maxY;
				double i = box.maxZ;
				if (dir.getOffsetX() < 0) {
					d -= (double)((float)dir.getOffsetX() * progress);
				} else {
					g -= (double)((float)dir.getOffsetX() * progress);
				}

				if (dir.getOffsetY() < 0) {
					e -= (double)((float)dir.getOffsetY() * progress);
				} else {
					h -= (double)((float)dir.getOffsetY() * progress);
				}

				if (dir.getOffsetZ() < 0) {
					f -= (double)((float)dir.getOffsetZ() * progress);
				} else {
					i -= (double)((float)dir.getOffsetZ() * progress);
				}

				return new Box(d, e, f, g, h, i);
			}
		} else {
			return null;
		}
	}

	private PistonBlockEntity getPistonEntity(BlockView view, BlockPos pos) {
		BlockEntity blockEntity = view.getBlockEntity(pos);
		return blockEntity instanceof PistonBlockEntity ? (PistonBlockEntity)blockEntity : null;
	}

	@Override
	public Item getPickItem(World world, BlockPos pos) {
		return null;
	}

	@Override
	public BlockState stateFromData(int data) {
		return this.getDefaultState()
			.with(FACING, PistonHeadBlock.getDirection(data))
			.with(TYPE, (data & 8) > 0 ? PistonHeadBlock.PistonHeadType.STICKY : PistonHeadBlock.PistonHeadType.DEFAULT);
	}

	@Override
	public int getData(BlockState state) {
		int i = 0;
		i |= ((Direction)state.get(FACING)).getId();
		if (state.get(TYPE) == PistonHeadBlock.PistonHeadType.STICKY) {
			i |= 8;
		}

		return i;
	}

	@Override
	protected StateManager appendProperties() {
		return new StateManager(this, FACING, TYPE);
	}
}
