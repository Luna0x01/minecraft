package net.minecraft.recipe;

import net.minecraft.class_3571;
import net.minecraft.class_3578;
import net.minecraft.class_3579;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.WrittenBookItem;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.Identifier;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.world.World;

public class BookCloningRecipeType extends class_3571 {
	public BookCloningRecipeType(Identifier identifier) {
		super(identifier);
	}

	@Override
	public boolean method_3500(Inventory inventory, World world) {
		if (!(inventory instanceof CraftingInventory)) {
			return false;
		} else {
			int i = 0;
			ItemStack itemStack = ItemStack.EMPTY;

			for (int j = 0; j < inventory.getInvSize(); j++) {
				ItemStack itemStack2 = inventory.getInvStack(j);
				if (!itemStack2.isEmpty()) {
					if (itemStack2.getItem() == Items.WRITTEN_BOOK) {
						if (!itemStack.isEmpty()) {
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

			return !itemStack.isEmpty() && itemStack.hasNbt() && i > 0;
		}
	}

	@Override
	public ItemStack method_16201(Inventory inventory) {
		int i = 0;
		ItemStack itemStack = ItemStack.EMPTY;

		for (int j = 0; j < inventory.getInvSize(); j++) {
			ItemStack itemStack2 = inventory.getInvStack(j);
			if (!itemStack2.isEmpty()) {
				if (itemStack2.getItem() == Items.WRITTEN_BOOK) {
					if (!itemStack.isEmpty()) {
						return ItemStack.EMPTY;
					}

					itemStack = itemStack2;
				} else {
					if (itemStack2.getItem() != Items.WRITABLE_BOOK) {
						return ItemStack.EMPTY;
					}

					i++;
				}
			}
		}

		if (!itemStack.isEmpty() && itemStack.hasNbt() && i >= 1 && WrittenBookItem.getGeneration(itemStack) < 2) {
			ItemStack itemStack3 = new ItemStack(Items.WRITTEN_BOOK, i);
			NbtCompound nbtCompound = itemStack.getNbt().copy();
			nbtCompound.putInt("generation", WrittenBookItem.getGeneration(itemStack) + 1);
			itemStack3.setNbt(nbtCompound);
			return itemStack3;
		} else {
			return ItemStack.EMPTY;
		}
	}

	@Override
	public DefaultedList<ItemStack> method_16203(Inventory inventory) {
		DefaultedList<ItemStack> defaultedList = DefaultedList.ofSize(inventory.getInvSize(), ItemStack.EMPTY);

		for (int i = 0; i < defaultedList.size(); i++) {
			ItemStack itemStack = inventory.getInvStack(i);
			if (itemStack.getItem().isFood()) {
				defaultedList.set(i, new ItemStack(itemStack.getItem().getRecipeRemainder()));
			} else if (itemStack.getItem() instanceof WrittenBookItem) {
				ItemStack itemStack2 = itemStack.copy();
				itemStack2.setCount(1);
				defaultedList.set(i, itemStack2);
				break;
			}
		}

		return defaultedList;
	}

	@Override
	public class_3578<?> method_16200() {
		return class_3579.field_17450;
	}

	@Override
	public boolean method_14250(int i, int j) {
		return i >= 3 && j >= 3;
	}
}
