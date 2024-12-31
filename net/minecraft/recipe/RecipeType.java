package net.minecraft.recipe;

import net.minecraft.inventory.CraftingInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.world.World;

public interface RecipeType {
	boolean matches(CraftingInventory inventory, World world);

	ItemStack getResult(CraftingInventory inventory);

	int getSize();

	ItemStack getOutput();

	DefaultedList<ItemStack> method_13670(CraftingInventory craftingInventory);
}
