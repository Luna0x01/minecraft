package net.minecraft.block;

import java.util.List;
import java.util.Random;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.state.property.Property;
import net.minecraft.util.StringIdentifiable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

public abstract class SlabBlock extends Block {
	public static final EnumProperty<SlabBlock.SlabType> HALF = EnumProperty.of("half", SlabBlock.SlabType.class);

	public SlabBlock(Material material) {
		super(material);
		if (this.isDoubleSlab()) {
			this.fullBlock = true;
		} else {
			this.setBoundingBox(0.0F, 0.0F, 0.0F, 1.0F, 0.5F, 1.0F);
		}

		this.setOpacity(255);
	}

	@Override
	protected boolean requiresSilkTouch() {
		return false;
	}

	@Override
	public void setBoundingBox(BlockView view, BlockPos pos) {
		if (this.isDoubleSlab()) {
			this.setBoundingBox(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
		} else {
			BlockState blockState = view.getBlockState(pos);
			if (blockState.getBlock() == this) {
				if (blockState.get(HALF) == SlabBlock.SlabType.TOP) {
					this.setBoundingBox(0.0F, 0.5F, 0.0F, 1.0F, 1.0F, 1.0F);
				} else {
					this.setBoundingBox(0.0F, 0.0F, 0.0F, 1.0F, 0.5F, 1.0F);
				}
			}
		}
	}

	@Override
	public void setBlockItemBounds() {
		if (this.isDoubleSlab()) {
			this.setBoundingBox(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
		} else {
			this.setBoundingBox(0.0F, 0.0F, 0.0F, 1.0F, 0.5F, 1.0F);
		}
	}

	@Override
	public void appendCollisionBoxes(World world, BlockPos pos, BlockState state, Box box, List<Box> list, Entity entity) {
		this.setBoundingBox(world, pos);
		super.appendCollisionBoxes(world, pos, state, box, list, entity);
	}

	@Override
	public boolean hasTransparency() {
		return this.isDoubleSlab();
	}

	@Override
	public BlockState getStateFromData(World world, BlockPos pos, Direction dir, float x, float y, float z, int id, LivingEntity entity) {
		BlockState blockState = super.getStateFromData(world, pos, dir, x, y, z, id, entity).with(HALF, SlabBlock.SlabType.BOTTOM);
		if (this.isDoubleSlab()) {
			return blockState;
		} else {
			return dir != Direction.DOWN && (dir == Direction.UP || !((double)y > 0.5)) ? blockState : blockState.with(HALF, SlabBlock.SlabType.TOP);
		}
	}

	@Override
	public int getDropCount(Random rand) {
		return this.isDoubleSlab() ? 2 : 1;
	}

	@Override
	public boolean renderAsNormalBlock() {
		return this.isDoubleSlab();
	}

	@Override
	public boolean isSideInvisible(BlockView view, BlockPos pos, Direction facing) {
		if (this.isDoubleSlab()) {
			return super.isSideInvisible(view, pos, facing);
		} else if (facing != Direction.UP && facing != Direction.DOWN && !super.isSideInvisible(view, pos, facing)) {
			return false;
		} else {
			BlockPos blockPos = pos.offset(facing.getOpposite());
			BlockState blockState = view.getBlockState(pos);
			BlockState blockState2 = view.getBlockState(blockPos);
			boolean bl = isSlab(blockState.getBlock()) && blockState.get(HALF) == SlabBlock.SlabType.TOP;
			boolean bl2 = isSlab(blockState2.getBlock()) && blockState2.get(HALF) == SlabBlock.SlabType.TOP;
			if (bl2) {
				if (facing == Direction.DOWN) {
					return true;
				} else {
					return facing == Direction.UP && super.isSideInvisible(view, pos, facing) ? true : !isSlab(blockState.getBlock()) || !bl;
				}
			} else if (facing == Direction.UP) {
				return true;
			} else {
				return facing == Direction.DOWN && super.isSideInvisible(view, pos, facing) ? true : !isSlab(blockState.getBlock()) || bl;
			}
		}
	}

	protected static boolean isSlab(Block block) {
		return block == Blocks.STONE_SLAB || block == Blocks.WOODEN_SLAB || block == Blocks.STONE_SLAB2;
	}

	public abstract String getVariantTranslationKey(int slabType);

	@Override
	public int getMeta(World world, BlockPos pos) {
		return super.getMeta(world, pos) & 7;
	}

	public abstract boolean isDoubleSlab();

	public abstract Property<?> getSlabProperty();

	public abstract Object getSlabType(ItemStack stack);

	public static enum SlabType implements StringIdentifiable {
		TOP("top"),
		BOTTOM("bottom");

		private final String name;

		private SlabType(String string2) {
			this.name = string2;
		}

		public String toString() {
			return this.name;
		}

		@Override
		public String asString() {
			return this.name;
		}
	}
}
