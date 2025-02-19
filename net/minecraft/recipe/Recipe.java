package net.minecraft.recipe;

import net.minecraft.block.Blocks;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.world.World;

public interface Recipe<C extends Inventory> {
	boolean matches(C inventory, World world);

	ItemStack craft(C inventory);

	boolean fits(int width, int height);

	ItemStack getOutput();

	default DefaultedList<ItemStack> getRemainder(C inventory) {
		DefaultedList<ItemStack> defaultedList = DefaultedList.ofSize(inventory.size(), ItemStack.EMPTY);

		for (int i = 0; i < defaultedList.size(); i++) {
			Item item = inventory.getStack(i).getItem();
			if (item.hasRecipeRemainder()) {
				defaultedList.set(i, new ItemStack(item.getRecipeRemainder()));
			}
		}

		return defaultedList;
	}

	default DefaultedList<Ingredient> getIngredients() {
		return DefaultedList.of();
	}

	default boolean isIgnoredInRecipeBook() {
		return false;
	}

	default String getGroup() {
		return "";
	}

	default ItemStack createIcon() {
		return new ItemStack(Blocks.CRAFTING_TABLE);
	}

	Identifier getId();

	RecipeSerializer<?> getSerializer();

	RecipeType<?> getType();

	default boolean isEmpty() {
		DefaultedList<Ingredient> defaultedList = this.getIngredients();
		return defaultedList.isEmpty() || defaultedList.stream().anyMatch(ingredient -> ingredient.getMatchingStacksClient().length == 0);
	}
}
