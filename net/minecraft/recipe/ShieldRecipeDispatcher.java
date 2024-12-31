package net.minecraft.recipe;

import net.minecraft.class_3571;
import net.minecraft.class_3578;
import net.minecraft.class_3579;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.BannerItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;

public class ShieldRecipeDispatcher extends class_3571 {
	public ShieldRecipeDispatcher(Identifier identifier) {
		super(identifier);
	}

	@Override
	public boolean method_3500(Inventory inventory, World world) {
		if (!(inventory instanceof CraftingInventory)) {
			return false;
		} else {
			ItemStack itemStack = ItemStack.EMPTY;
			ItemStack itemStack2 = ItemStack.EMPTY;

			for (int i = 0; i < inventory.getInvSize(); i++) {
				ItemStack itemStack3 = inventory.getInvStack(i);
				if (!itemStack3.isEmpty()) {
					if (itemStack3.getItem() instanceof BannerItem) {
						if (!itemStack2.isEmpty()) {
							return false;
						}

						itemStack2 = itemStack3;
					} else {
						if (itemStack3.getItem() != Items.SHIELD) {
							return false;
						}

						if (!itemStack.isEmpty()) {
							return false;
						}

						if (itemStack3.getNbtCompound("BlockEntityTag") != null) {
							return false;
						}

						itemStack = itemStack3;
					}
				}
			}

			return !itemStack.isEmpty() && !itemStack2.isEmpty();
		}
	}

	@Override
	public ItemStack method_16201(Inventory inventory) {
		ItemStack itemStack = ItemStack.EMPTY;
		ItemStack itemStack2 = ItemStack.EMPTY;

		for (int i = 0; i < inventory.getInvSize(); i++) {
			ItemStack itemStack3 = inventory.getInvStack(i);
			if (!itemStack3.isEmpty()) {
				if (itemStack3.getItem() instanceof BannerItem) {
					itemStack = itemStack3;
				} else if (itemStack3.getItem() == Items.SHIELD) {
					itemStack2 = itemStack3.copy();
				}
			}
		}

		if (itemStack2.isEmpty()) {
			return itemStack2;
		} else {
			NbtCompound nbtCompound = itemStack.getNbtCompound("BlockEntityTag");
			NbtCompound nbtCompound2 = nbtCompound == null ? new NbtCompound() : nbtCompound.copy();
			nbtCompound2.putInt("Base", ((BannerItem)itemStack.getItem()).method_16011().getId());
			itemStack2.addNbt("BlockEntityTag", nbtCompound2);
			return itemStack2;
		}
	}

	@Override
	public boolean method_14250(int i, int j) {
		return i * j >= 2;
	}

	@Override
	public class_3578<?> method_16200() {
		return class_3579.field_17460;
	}
}
