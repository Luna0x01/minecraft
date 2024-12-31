package net.minecraft.recipe;

import net.minecraft.class_3578;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.world.World;

public interface RecipeType {
	boolean method_3500(Inventory inventory, World world);

	ItemStack method_16201(Inventory inventory);

	boolean method_14250(int i, int j);

	ItemStack getOutput();

	default DefaultedList<ItemStack> method_16203(Inventory inventory) {
		DefaultedList<ItemStack> defaultedList = DefaultedList.ofSize(inventory.getInvSize(), ItemStack.EMPTY);

		for (int i = 0; i < defaultedList.size(); i++) {
			Item item = inventory.getInvStack(i).getItem();
			if (item.isFood()) {
				defaultedList.set(i, new ItemStack(item.getRecipeRemainder()));
			}
		}

		return defaultedList;
	}

	default DefaultedList<Ingredient> method_14252() {
		return DefaultedList.of();
	}

	default boolean method_14251() {
		return false;
	}

	default String method_14253() {
		return "";
	}

	Identifier method_16202();

	class_3578<?> method_16200();
}
