package net.minecraft.recipe;

import net.minecraft.inventory.CraftingInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.world.World;

public class MapCloningRecipeType implements RecipeType {
	@Override
	public boolean matches(CraftingInventory inventory, World world) {
		int i = 0;
		ItemStack itemStack = null;

		for (int j = 0; j < inventory.getInvSize(); j++) {
			ItemStack itemStack2 = inventory.getInvStack(j);
			if (itemStack2 != null) {
				if (itemStack2.getItem() == Items.FILLED_MAP) {
					if (itemStack != null) {
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

		return itemStack != null && i > 0;
	}

	@Override
	public ItemStack getResult(CraftingInventory inventory) {
		int i = 0;
		ItemStack itemStack = null;

		for (int j = 0; j < inventory.getInvSize(); j++) {
			ItemStack itemStack2 = inventory.getInvStack(j);
			if (itemStack2 != null) {
				if (itemStack2.getItem() == Items.FILLED_MAP) {
					if (itemStack != null) {
						return null;
					}

					itemStack = itemStack2;
				} else {
					if (itemStack2.getItem() != Items.MAP) {
						return null;
					}

					i++;
				}
			}
		}

		if (itemStack != null && i >= 1) {
			ItemStack itemStack3 = new ItemStack(Items.FILLED_MAP, i + 1, itemStack.getData());
			if (itemStack.hasCustomName()) {
				itemStack3.setCustomName(itemStack.getCustomName());
			}

			return itemStack3;
		} else {
			return null;
		}
	}

	@Override
	public int getSize() {
		return 9;
	}

	@Override
	public ItemStack getOutput() {
		return null;
	}

	@Override
	public ItemStack[] getRemainders(CraftingInventory inventory) {
		ItemStack[] itemStacks = new ItemStack[inventory.getInvSize()];

		for (int i = 0; i < itemStacks.length; i++) {
			ItemStack itemStack = inventory.getInvStack(i);
			if (itemStack != null && itemStack.getItem().isFood()) {
				itemStacks[i] = new ItemStack(itemStack.getItem().getRecipeRemainder());
			}
		}

		return itemStacks;
	}
}
