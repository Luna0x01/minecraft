package net.minecraft.block;

import java.util.Random;
import javax.annotation.Nullable;
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
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.CommonI18n;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

public class BannerBlock extends BlockWithEntity {
	public static final DirectionProperty FACING = HorizontalFacingBlock.DIRECTION;
	public static final IntProperty ROTATION = IntProperty.of("rotation", 0, 15);
	protected static final Box field_12558 = new Box(0.25, 0.0, 0.25, 0.75, 1.0, 0.75);

	protected BannerBlock() {
		super(Material.WOOD);
	}

	@Override
	public String getTranslatedName() {
		return CommonI18n.translate("item.banner.white.name");
	}

	@Nullable
	@Override
	public Box getCollisionBox(BlockState state, World world, BlockPos pos) {
		return EMPTY_BOX;
	}

	@Override
	public boolean method_11562(BlockState state) {
		return false;
	}

	@Override
	public boolean blocksMovement(BlockView view, BlockPos pos) {
		return true;
	}

	@Override
	public boolean isFullBoundsCubeForCulling(BlockState blockState) {
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

	@Nullable
	@Override
	public Item getDropItem(BlockState state, Random random, int id) {
		return Items.BANNER;
	}

	@Nullable
	private ItemStack method_11547(World world, BlockPos blockPos, BlockState blockState) {
		BlockEntity blockEntity = world.getBlockEntity(blockPos);
		if (blockEntity instanceof BannerBlockEntity) {
			ItemStack itemStack = new ItemStack(Items.BANNER, 1, ((BannerBlockEntity)blockEntity).getBase());
			NbtCompound nbtCompound = blockEntity.toNbt(new NbtCompound());
			nbtCompound.remove("x");
			nbtCompound.remove("y");
			nbtCompound.remove("z");
			nbtCompound.remove("id");
			itemStack.putSubNbt("BlockEntityTag", nbtCompound);
			return itemStack;
		} else {
			return null;
		}
	}

	@Override
	public ItemStack getItemStack(World world, BlockPos blockPos, BlockState blockState) {
		ItemStack itemStack = this.method_11547(world, blockPos, blockState);
		return itemStack != null ? itemStack : new ItemStack(Items.BANNER);
	}

	@Override
	public void randomDropAsItem(World world, BlockPos pos, BlockState state, float chance, int id) {
		ItemStack itemStack = this.method_11547(world, pos, state);
		if (itemStack != null) {
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
	public void method_8651(World world, PlayerEntity player, BlockPos pos, BlockState state, @Nullable BlockEntity blockEntity, @Nullable ItemStack stack) {
		if (blockEntity instanceof BannerBlockEntity) {
			BannerBlockEntity bannerBlockEntity = (BannerBlockEntity)blockEntity;
			ItemStack itemStack = new ItemStack(Items.BANNER, 1, ((BannerBlockEntity)blockEntity).getBase());
			NbtCompound nbtCompound = new NbtCompound();
			BannerBlockEntity.toNbt(nbtCompound, bannerBlockEntity.getBase(), bannerBlockEntity.getPatternsNbt());
			itemStack.putSubNbt("BlockEntityTag", nbtCompound);
			onBlockBreak(world, pos, itemStack);
		} else {
			super.method_8651(world, player, pos, state, null, stack);
		}
	}

	public static class StandingBannerBlock extends BannerBlock {
		public StandingBannerBlock() {
			this.setDefaultState(this.stateManager.getDefaultState().with(ROTATION, 0));
		}

		@Override
		public Box getCollisionBox(BlockState state, BlockView view, BlockPos pos) {
			return field_12558;
		}

		@Override
		public BlockState withRotation(BlockState state, BlockRotation rotation) {
			return state.with(ROTATION, rotation.rotate((Integer)state.get(ROTATION), 16));
		}

		@Override
		public BlockState withMirror(BlockState state, BlockMirror mirror) {
			return state.with(ROTATION, mirror.mirror((Integer)state.get(ROTATION), 16));
		}

		@Override
		public void method_8641(BlockState blockState, World world, BlockPos blockPos, Block block) {
			if (!world.getBlockState(blockPos.down()).getMaterial().isSolid()) {
				this.dropAsItem(world, blockPos, blockState, 0);
				world.setAir(blockPos);
			}

			super.method_8641(blockState, world, blockPos, block);
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
		protected static final Box field_12559 = new Box(0.0, 0.0, 0.875, 1.0, 0.78125, 1.0);
		protected static final Box field_12560 = new Box(0.0, 0.0, 0.0, 1.0, 0.78125, 0.125);
		protected static final Box field_12561 = new Box(0.875, 0.0, 0.0, 1.0, 0.78125, 1.0);
		protected static final Box field_12562 = new Box(0.0, 0.0, 0.0, 0.125, 0.78125, 1.0);

		public WallBannerBlock() {
			this.setDefaultState(this.stateManager.getDefaultState().with(FACING, Direction.NORTH));
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
		public Box getCollisionBox(BlockState state, BlockView view, BlockPos pos) {
			switch ((Direction)state.get(FACING)) {
				case NORTH:
				default:
					return field_12559;
				case SOUTH:
					return field_12560;
				case WEST:
					return field_12561;
				case EAST:
					return field_12562;
			}
		}

		@Override
		public void method_8641(BlockState blockState, World world, BlockPos blockPos, Block block) {
			Direction direction = blockState.get(FACING);
			if (!world.getBlockState(blockPos.offset(direction.getOpposite())).getMaterial().isSolid()) {
				this.dropAsItem(world, blockPos, blockState, 0);
				world.setAir(blockPos);
			}

			super.method_8641(blockState, world, blockPos, block);
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
