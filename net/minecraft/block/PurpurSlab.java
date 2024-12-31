package net.minecraft.block;

import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.block.material.Material;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.itemgroup.ItemGroup;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.state.property.Property;
import net.minecraft.util.StringIdentifiable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public abstract class PurpurSlab extends SlabBlock {
	public static final EnumProperty<PurpurSlab.class_2732> field_12723 = EnumProperty.of("variant", PurpurSlab.class_2732.class);

	public PurpurSlab() {
		super(Material.STONE);
		BlockState blockState = this.stateManager.getDefaultState();
		if (!this.isDoubleSlab()) {
			blockState = blockState.with(HALF, SlabBlock.SlabType.BOTTOM);
		}

		this.setDefaultState(blockState.with(field_12723, PurpurSlab.class_2732.DEFAULT));
		this.setItemGroup(ItemGroup.BUILDING_BLOCKS);
	}

	@Nullable
	@Override
	public Item getDropItem(BlockState state, Random random, int id) {
		return Item.fromBlock(Blocks.PURPUR_SLAB);
	}

	@Override
	public ItemStack getItemStack(World world, BlockPos blockPos, BlockState blockState) {
		return new ItemStack(Blocks.PURPUR_SLAB);
	}

	@Override
	public BlockState stateFromData(int data) {
		BlockState blockState = this.getDefaultState().with(field_12723, PurpurSlab.class_2732.DEFAULT);
		if (!this.isDoubleSlab()) {
			blockState = blockState.with(HALF, (data & 8) == 0 ? SlabBlock.SlabType.BOTTOM : SlabBlock.SlabType.TOP);
		}

		return blockState;
	}

	@Override
	public int getData(BlockState state) {
		int i = 0;
		if (!this.isDoubleSlab() && state.get(HALF) == SlabBlock.SlabType.TOP) {
			i |= 8;
		}

		return i;
	}

	@Override
	protected StateManager appendProperties() {
		return this.isDoubleSlab() ? new StateManager(this, field_12723) : new StateManager(this, HALF, field_12723);
	}

	@Override
	public String getVariantTranslationKey(int slabType) {
		return super.getTranslationKey();
	}

	@Override
	public Property<?> getSlabProperty() {
		return field_12723;
	}

	@Override
	public Comparable<?> method_11615(ItemStack itemStack) {
		return PurpurSlab.class_2732.DEFAULT;
	}

	public static class Double extends PurpurSlab {
		@Override
		public boolean isDoubleSlab() {
			return true;
		}
	}

	public static class Single extends PurpurSlab {
		@Override
		public boolean isDoubleSlab() {
			return false;
		}
	}

	public static enum class_2732 implements StringIdentifiable {
		DEFAULT;

		@Override
		public String asString() {
			return "default";
		}
	}
}
