package net.minecraft;

import com.google.common.collect.ImmutableList;
import java.util.List;
import net.minecraft.block.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;

public enum class_4113 {
	SEARCH(new ItemStack(Items.COMPASS)),
	BUILDING_BLOCKS(new ItemStack(Blocks.BRICKS)),
	REDSTONE(new ItemStack(Items.REDSTONE)),
	EQUIPMENT(new ItemStack(Items.IRON_AXE), new ItemStack(Items.GOLDEN_SWORD)),
	MISC(new ItemStack(Items.LAVA_BUCKET), new ItemStack(Items.APPLE)),
	FURNACE_SEARCH(new ItemStack(Items.COMPASS)),
	FURNACE_FOOD(new ItemStack(Items.RAW_PORKCHOP)),
	FURNACE_BLOCKS(new ItemStack(Blocks.STONE)),
	FURNACE_MISC(new ItemStack(Items.LAVA_BUCKET), new ItemStack(Items.EMERALD));

	private final List<ItemStack> field_20009;

	private class_4113(ItemStack... itemStacks) {
		this.field_20009 = ImmutableList.copyOf(itemStacks);
	}

	public List<ItemStack> method_18268() {
		return this.field_20009;
	}
}
