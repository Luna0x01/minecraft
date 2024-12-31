package net.minecraft.recipe;

import net.minecraft.inventory.CraftingInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.world.World;

public class MapCloningRecipeType implements RecipeType {
	@Override
	public boolean matches(CraftingInventory inventory, World world) {
		int i = 0;
		ItemStack itemStack = ItemStack.EMPTY;

		for (int j = 0; j < inventory.getInvSize(); j++) {
			ItemStack itemStack2 = inventory.getInvStack(j);
			if (!itemStack2.isEmpty()) {
				if (itemStack2.getItem() == Items.FILLED_MAP) {
					if (!itemStack.isEmpty()) {
						return false;
					}

					itemStack = itemStack2;
				} else {
					if (itemStack2.getItem() != Items.MAP) {
						return false;
					}

					i++;
				}
			}
		}

		return !itemStack.isEmpty() && i > 0;
	}

	@Override
	public ItemStack getResult(CraftingInventory inventory) {
		int i = 0;
		ItemStack itemStack = ItemStack.EMPTY;

		for (int j = 0; j < inventory.getInvSize(); j++) {
			ItemStack itemStack2 = inventory.getInvStack(j);
			if (!itemStack2.isEmpty()) {
				if (itemStack2.getItem() == Items.FILLED_MAP) {
					if (!itemStack.isEmpty()) {
						return ItemStack.EMPTY;
					}

					itemStack = itemStack2;
				} else {
					if (itemStack2.getItem() != Items.MAP) {
						return ItemStack.EMPTY;
					}

					i++;
				}
			}
		}

		if (!itemStack.isEmpty() && i >= 1) {
			ItemStack itemStack3 = new ItemStack(Items.FILLED_MAP, i + 1, itemStack.getData());
			if (itemStack.hasCustomName()) {
				itemStack3.setCustomName(itemStack.getCustomName());
			}

			if (itemStack.hasNbt()) {
				itemStack3.setNbt(itemStack.getNbt());
			}

			return itemStack3;
		} else {
			return ItemStack.EMPTY;
		}
	}

	@Override
	public ItemStack getOutput() {
		return ItemStack.EMPTY;
	}

	@Override
	public DefaultedList<ItemStack> method_13670(CraftingInventory craftingInventory) {
		DefaultedList<ItemStack> defaultedList = DefaultedList.ofSize(craftingInventory.getInvSize(), ItemStack.EMPTY);

		for (int i = 0; i < defaultedList.size(); i++) {
			ItemStack itemStack = craftingInventory.getInvStack(i);
			if (itemStack.getItem().isFood()) {
				defaultedList.set(i, new ItemStack(itemStack.getItem().getRecipeRemainder()));
			}
		}

		return defaultedList;
	}

	@Override
	public boolean method_14251() {
		return true;
	}

	@Override
	public boolean method_14250(int i, int j) {
		return i >= 3 && j >= 3;
	}
}
