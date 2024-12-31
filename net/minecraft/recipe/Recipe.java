package net.minecraft.recipe;

import net.minecraft.block.Blocks;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DefaultedList;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;

public interface Recipe<C extends Inventory> {
	boolean matches(C inventory, World world);

	ItemStack craft(C inventory);

	boolean fits(int i, int j);

	ItemStack getOutput();

	default DefaultedList<ItemStack> getRemainingStacks(C inventory) {
		DefaultedList<ItemStack> defaultedList = DefaultedList.ofSize(inventory.getInvSize(), ItemStack.EMPTY);

		for (int i = 0; i < defaultedList.size(); i++) {
			Item item = inventory.getInvStack(i).getItem();
			if (item.hasRecipeRemainder()) {
				defaultedList.set(i, new ItemStack(item.getRecipeRemainder()));
			}
		}

		return defaultedList;
	}

	default DefaultedList<Ingredient> getPreviewInputs() {
		return DefaultedList.of();
	}

	default boolean isIgnoredInRecipeBook() {
		return false;
	}

	default String getGroup() {
		return "";
	}

	default ItemStack getRecipeKindIcon() {
		return new ItemStack(Blocks.field_9980);
	}

	Identifier getId();

	RecipeSerializer<?> getSerializer();

	RecipeType<?> getType();
}
