package net.minecraft.recipe;

import javax.annotation.Nullable;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public interface RecipeType {
	boolean matches(CraftingInventory inventory, World world);

	@Nullable
	ItemStack getResult(CraftingInventory inventory);

	int getSize();

	@Nullable
	ItemStack getOutput();

	ItemStack[] getRemainders(CraftingInventory inventory);
}
