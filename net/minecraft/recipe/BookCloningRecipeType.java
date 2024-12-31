package net.minecraft.recipe;

import javax.annotation.Nullable;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.WrittenBookItem;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.world.World;

public class BookCloningRecipeType implements RecipeType {
	@Override
	public boolean matches(CraftingInventory inventory, World world) {
		int i = 0;
		ItemStack itemStack = null;

		for (int j = 0; j < inventory.getInvSize(); j++) {
			ItemStack itemStack2 = inventory.getInvStack(j);
			if (itemStack2 != null) {
				if (itemStack2.getItem() == Items.WRITTEN_BOOK) {
					if (itemStack != null) {
						return false;
					}

					itemStack = itemStack2;
				} else {
					if (itemStack2.getItem() != Items.WRITABLE_BOOK) {
						return false;
					}

					i++;
				}
			}
		}

		return itemStack != null && i > 0;
	}

	@Nullable
	@Override
	public ItemStack getResult(CraftingInventory inventory) {
		int i = 0;
		ItemStack itemStack = null;

		for (int j = 0; j < inventory.getInvSize(); j++) {
			ItemStack itemStack2 = inventory.getInvStack(j);
			if (itemStack2 != null) {
				if (itemStack2.getItem() == Items.WRITTEN_BOOK) {
					if (itemStack != null) {
						return null;
					}

					itemStack = itemStack2;
				} else {
					if (itemStack2.getItem() != Items.WRITABLE_BOOK) {
						return null;
					}

					i++;
				}
			}
		}

		if (itemStack != null && i >= 1 && WrittenBookItem.getGeneration(itemStack) < 2) {
			ItemStack itemStack3 = new ItemStack(Items.WRITTEN_BOOK, i);
			itemStack3.setNbt((NbtCompound)itemStack.getNbt().copy());
			itemStack3.getNbt().putInt("generation", WrittenBookItem.getGeneration(itemStack) + 1);
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

	@Nullable
	@Override
	public ItemStack getOutput() {
		return null;
	}

	@Override
	public ItemStack[] getRemainders(CraftingInventory inventory) {
		ItemStack[] itemStacks = new ItemStack[inventory.getInvSize()];

		for (int i = 0; i < itemStacks.length; i++) {
			ItemStack itemStack = inventory.getInvStack(i);
			if (itemStack != null && itemStack.getItem() instanceof WrittenBookItem) {
				itemStacks[i] = itemStack.copy();
				itemStacks[i].count = 1;
				break;
			}
		}

		return itemStacks;
	}
}
