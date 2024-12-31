package net.minecraft.block;

import java.util.Random;
import net.minecraft.block.entity.BannerBlockEntity;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.state.property.IntProperty;
import net.minecraft.util.CommonI18n;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

public class BannerBlock extends BlockWithEntity {
	public static final DirectionProperty FACING = DirectionProperty.of("facing", Direction.DirectionType.HORIZONTAL);
	public static final IntProperty ROTATION = IntProperty.of("rotation", 0, 15);

	protected BannerBlock() {
		super(Material.WOOD);
		float f = 0.25F;
		float g = 1.0F;
		this.setBoundingBox(0.5F - f, 0.0F, 0.5F - f, 0.5F + f, g, 0.5F + f);
	}

	@Override
	public String getTranslatedName() {
		return CommonI18n.translate("item.banner.white.name");
	}

	@Override
	public Box getCollisionBox(World world, BlockPos pos, BlockState state) {
		return null;
	}

	@Override
	public Box getSelectionBox(World world, BlockPos pos) {
		this.setBoundingBox(world, pos);
		return super.getSelectionBox(world, pos);
	}

	@Override
	public boolean renderAsNormalBlock() {
		return false;
	}

	@Override
	public boolean blocksMovement(BlockView view, BlockPos pos) {
		return true;
	}

	@Override
	public boolean hasTransparency() {
		return false;
	}

	@Override
	public boolean canMobSpawnInside() {
		return true;
	}

	@Override
	public BlockEntity createBlockEntity(World world, int id) {
		return new BannerBlockEntity();
	}

	@Override
	public Item getDropItem(BlockState state, Random random, int id) {
		return Items.BANNER;
	}

	@Override
	public Item getPickItem(World world, BlockPos pos) {
		return Items.BANNER;
	}

	@Override
	public void randomDropAsItem(World world, BlockPos pos, BlockState state, float chance, int id) {
		BlockEntity blockEntity = world.getBlockEntity(pos);
		if (blockEntity instanceof BannerBlockEntity) {
			ItemStack itemStack = new ItemStack(Items.BANNER, 1, ((BannerBlockEntity)blockEntity).getBase());
			NbtCompound nbtCompound = new NbtCompound();
			blockEntity.toNbt(nbtCompound);
			nbtCompound.remove("x");
			nbtCompound.remove("y");
			nbtCompound.remove("z");
			nbtCompound.remove("id");
			itemStack.putSubNbt("BlockEntityTag", nbtCompound);
			onBlockBreak(world, pos, itemStack);
		} else {
			super.randomDropAsItem(world, pos, state, chance, id);
		}
	}

	@Override
	public boolean canBePlacedAtPos(World world, BlockPos pos) {
		return !this.isAdjacentToCactus(world, pos) && super.canBePlacedAtPos(world, pos);
	}

	@Override
	public void harvest(World world, PlayerEntity player, BlockPos pos, BlockState state, BlockEntity be) {
		if (be instanceof BannerBlockEntity) {
			BannerBlockEntity bannerBlockEntity = (BannerBlockEntity)be;
			ItemStack itemStack = new ItemStack(Items.BANNER, 1, ((BannerBlockEntity)be).getBase());
			NbtCompound nbtCompound = new NbtCompound();
			BannerBlockEntity.toNbt(nbtCompound, bannerBlockEntity.getBase(), bannerBlockEntity.getPatternsNbt());
			itemStack.putSubNbt("BlockEntityTag", nbtCompound);
			onBlockBreak(world, pos, itemStack);
		} else {
			super.harvest(world, player, pos, state, null);
		}
	}

	public static class StandingBannerBlock extends BannerBlock {
		public StandingBannerBlock() {
			this.setDefaultState(this.stateManager.getDefaultState().with(ROTATION, 0));
		}

		@Override
		public void neighborUpdate(World world, BlockPos pos, BlockState state, Block block) {
			if (!world.getBlockState(pos.down()).getBlock().getMaterial().isSolid()) {
				this.dropAsItem(world, pos, state, 0);
				world.setAir(pos);
			}

			super.neighborUpdate(world, pos, state, block);
		}

		@Override
		public BlockState stateFromData(int data) {
			return this.getDefaultState().with(ROTATION, data);
		}

		@Override
		public int getData(BlockState state) {
			return (Integer)state.get(ROTATION);
		}

		@Override
		protected StateManager appendProperties() {
			return new StateManager(this, ROTATION);
		}
	}

	public static class WallBannerBlock extends BannerBlock {
		public WallBannerBlock() {
			this.setDefaultState(this.stateManager.getDefaultState().with(FACING, Direction.NORTH));
		}

		@Override
		public void setBoundingBox(BlockView view, BlockPos pos) {
			Direction direction = view.getBlockState(pos).get(FACING);
			float f = 0.0F;
			float g = 0.78125F;
			float h = 0.0F;
			float i = 1.0F;
			float j = 0.125F;
			this.setBoundingBox(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
			switch (direction) {
				case NORTH:
				default:
					this.setBoundingBox(h, f, 1.0F - j, i, g, 1.0F);
					break;
				case SOUTH:
					this.setBoundingBox(h, f, 0.0F, i, g, j);
					break;
				case WEST:
					this.setBoundingBox(1.0F - j, f, h, 1.0F, g, i);
					break;
				case EAST:
					this.setBoundingBox(0.0F, f, h, j, g, i);
			}
		}

		@Override
		public void neighborUpdate(World world, BlockPos pos, BlockState state, Block block) {
			Direction direction = state.get(FACING);
			if (!world.getBlockState(pos.offset(direction.getOpposite())).getBlock().getMaterial().isSolid()) {
				this.dropAsItem(world, pos, state, 0);
				world.setAir(pos);
			}

			super.neighborUpdate(world, pos, state, block);
		}

		@Override
		public BlockState stateFromData(int data) {
			Direction direction = Direction.getById(data);
			if (direction.getAxis() == Direction.Axis.Y) {
				direction = Direction.NORTH;
			}

			return this.getDefaultState().with(FACING, direction);
		}

		@Override
		public int getData(BlockState state) {
			return ((Direction)state.get(FACING)).getId();
		}

		@Override
		protected StateManager appendProperties() {
			return new StateManager(this, FACING);
		}
	}
}
