package net.minecraft.block;

import java.util.Random;
import net.minecraft.block.material.Material;
import net.minecraft.block.material.MaterialColor;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.itemgroup.ItemGroup;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.state.property.Property;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public abstract class WoodSlabBlock extends SlabBlock {
	public static final EnumProperty<PlanksBlock.WoodType> VARIANT = EnumProperty.of("variant", PlanksBlock.WoodType.class);

	public WoodSlabBlock() {
		super(Material.WOOD);
		BlockState blockState = this.stateManager.getDefaultState();
		if (!this.isDoubleSlab()) {
			blockState = blockState.with(HALF, SlabBlock.SlabType.BOTTOM);
		}

		this.setDefaultState(blockState.with(VARIANT, PlanksBlock.WoodType.OAK));
		this.setItemGroup(ItemGroup.BUILDING_BLOCKS);
	}

	@Override
	public MaterialColor getMaterialColor(BlockState state) {
		return ((PlanksBlock.WoodType)state.get(VARIANT)).getMaterialColor();
	}

	@Override
	public Item getDropItem(BlockState state, Random random, int id) {
		return Item.fromBlock(Blocks.WOODEN_SLAB);
	}

	@Override
	public ItemStack getItemStack(World world, BlockPos blockPos, BlockState blockState) {
		return new ItemStack(Blocks.WOODEN_SLAB, 1, ((PlanksBlock.WoodType)blockState.get(VARIANT)).getId());
	}

	@Override
	public String getVariantTranslationKey(int slabType) {
		return super.getTranslationKey() + "." + PlanksBlock.WoodType.getById(slabType).getOldName();
	}

	@Override
	public Property<?> getSlabProperty() {
		return VARIANT;
	}

	@Override
	public Comparable<?> method_11615(ItemStack itemStack) {
		return PlanksBlock.WoodType.getById(itemStack.getData() & 7);
	}

	@Override
	public void method_13700(Item item, ItemGroup itemGroup, DefaultedList<ItemStack> defaultedList) {
		if (item != Item.fromBlock(Blocks.DOUBLE_WOODEN_SLAB)) {
			for (PlanksBlock.WoodType woodType : PlanksBlock.WoodType.values()) {
				defaultedList.add(new ItemStack(item, 1, woodType.getId()));
			}
		}
	}

	@Override
	public BlockState stateFromData(int data) {
		BlockState blockState = this.getDefaultState().with(VARIANT, PlanksBlock.WoodType.getById(data & 7));
		if (!this.isDoubleSlab()) {
			blockState = blockState.with(HALF, (data & 8) == 0 ? SlabBlock.SlabType.BOTTOM : SlabBlock.SlabType.TOP);
		}

		return blockState;
	}

	@Override
	public int getData(BlockState state) {
		int i = 0;
		i |= ((PlanksBlock.WoodType)state.get(VARIANT)).getId();
		if (!this.isDoubleSlab() && state.get(HALF) == SlabBlock.SlabType.TOP) {
			i |= 8;
		}

		return i;
	}

	@Override
	protected StateManager appendProperties() {
		return this.isDoubleSlab() ? new StateManager(this, VARIANT) : new StateManager(this, HALF, VARIANT);
	}

	@Override
	public int getMeta(BlockState state) {
		return ((PlanksBlock.WoodType)state.get(VARIANT)).getId();
	}
}
