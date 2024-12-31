package net.minecraft.recipe;

import net.minecraft.inventory.CraftingInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.world.World;

public interface RecipeType {
	boolean matches(CraftingInventory inventory, World world);

	ItemStack getResult(CraftingInventory inventory);

	boolean method_14250(int i, int j);

	ItemStack getOutput();

	DefaultedList<ItemStack> method_13670(CraftingInventory craftingInventory);

	default DefaultedList<Ingredient> method_14252() {
		return DefaultedList.of();
	}

	default boolean method_14251() {
		return false;
	}

	default String method_14253() {
		return "";
	}
}
