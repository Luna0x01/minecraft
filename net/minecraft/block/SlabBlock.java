package net.minecraft.block;

import java.util.Random;
import net.minecraft.block.material.Material;
import net.minecraft.block.material.MaterialColor;
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
	protected static final Box field_12683 = new Box(0.0, 0.0, 0.0, 1.0, 0.5, 1.0);
	protected static final Box field_12684 = new Box(0.0, 0.5, 0.0, 1.0, 1.0, 1.0);

	public SlabBlock(Material material) {
		this(material, material.getColor());
	}

	public SlabBlock(Material material, MaterialColor materialColor) {
		super(material, materialColor);
		this.fullBlock = this.isDoubleSlab();
		this.setOpacity(255);
	}

	@Override
	protected boolean requiresSilkTouch() {
		return false;
	}

	@Override
	public Box getCollisionBox(BlockState state, BlockView view, BlockPos pos) {
		if (this.isDoubleSlab()) {
			return collisionBox;
		} else {
			return state.get(HALF) == SlabBlock.SlabType.TOP ? field_12684 : field_12683;
		}
	}

	@Override
	public boolean method_11568(BlockState state) {
		return ((SlabBlock)state.getBlock()).isDoubleSlab() || state.get(HALF) == SlabBlock.SlabType.TOP;
	}

	@Override
	public BlockRenderLayer getRenderLayer(BlockView world, BlockState state, BlockPos pos, Direction direction) {
		if (((SlabBlock)state.getBlock()).isDoubleSlab()) {
			return BlockRenderLayer.SOLID;
		} else if (direction == Direction.UP && state.get(HALF) == SlabBlock.SlabType.TOP) {
			return BlockRenderLayer.SOLID;
		} else {
			return direction == Direction.DOWN && state.get(HALF) == SlabBlock.SlabType.BOTTOM ? BlockRenderLayer.SOLID : BlockRenderLayer.UNDEFINED;
		}
	}

	@Override
	public boolean isFullBoundsCubeForCulling(BlockState blockState) {
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
	public boolean method_11562(BlockState state) {
		return this.isDoubleSlab();
	}

	@Override
	public boolean method_8654(BlockState state, BlockView view, BlockPos pos, Direction direction) {
		if (this.isDoubleSlab()) {
			return super.method_8654(state, view, pos, direction);
		} else if (direction != Direction.UP && direction != Direction.DOWN && !super.method_8654(state, view, pos, direction)) {
			return false;
		} else {
			BlockState blockState = view.getBlockState(pos.offset(direction));
			boolean bl = method_11616(blockState) && blockState.get(HALF) == SlabBlock.SlabType.TOP;
			boolean bl2 = method_11616(state) && state.get(HALF) == SlabBlock.SlabType.TOP;
			if (bl2) {
				if (direction == Direction.DOWN) {
					return true;
				} else {
					return direction == Direction.UP && super.method_8654(state, view, pos, direction) ? true : !method_11616(blockState) || !bl;
				}
			} else if (direction == Direction.UP) {
				return true;
			} else {
				return direction == Direction.DOWN && super.method_8654(state, view, pos, direction) ? true : !method_11616(blockState) || bl;
			}
		}
	}

	protected static boolean method_11616(BlockState blockState) {
		Block block = blockState.getBlock();
		return block == Blocks.STONE_SLAB || block == Blocks.WOODEN_SLAB || block == Blocks.STONE_SLAB2 || block == Blocks.PURPUR_SLAB;
	}

	public abstract String getVariantTranslationKey(int slabType);

	public abstract boolean isDoubleSlab();

	public abstract Property<?> getSlabProperty();

	public abstract Comparable<?> method_11615(ItemStack itemStack);

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
