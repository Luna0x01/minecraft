package net.minecraft;

import net.minecraft.block.entity.BannerBlockEntity;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.BannerItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DyeColor;
import net.minecraft.util.Identifier;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.world.World;

public class class_3570 extends class_3571 {
	public class_3570(Identifier identifier) {
		super(identifier);
	}

	@Override
	public boolean method_3500(Inventory inventory, World world) {
		if (!(inventory instanceof CraftingInventory)) {
			return false;
		} else {
			DyeColor dyeColor = null;
			ItemStack itemStack = null;
			ItemStack itemStack2 = null;

			for (int i = 0; i < inventory.getInvSize(); i++) {
				ItemStack itemStack3 = inventory.getInvStack(i);
				Item item = itemStack3.getItem();
				if (item instanceof BannerItem) {
					BannerItem bannerItem = (BannerItem)item;
					if (dyeColor == null) {
						dyeColor = bannerItem.method_16011();
					} else if (dyeColor != bannerItem.method_16011()) {
						return false;
					}

					boolean bl = BannerBlockEntity.getPatternCount(itemStack3) > 0;
					if (bl) {
						if (itemStack != null) {
							return false;
						}

						itemStack = itemStack3;
					} else {
						if (itemStack2 != null) {
							return false;
						}

						itemStack2 = itemStack3;
					}
				}
			}

			return itemStack != null && itemStack2 != null;
		}
	}

	@Override
	public ItemStack method_16201(Inventory inventory) {
		for (int i = 0; i < inventory.getInvSize(); i++) {
			ItemStack itemStack = inventory.getInvStack(i);
			if (!itemStack.isEmpty() && BannerBlockEntity.getPatternCount(itemStack) > 0) {
				ItemStack itemStack2 = itemStack.copy();
				itemStack2.setCount(1);
				return itemStack2;
			}
		}

		return ItemStack.EMPTY;
	}

	@Override
	public DefaultedList<ItemStack> method_16203(Inventory inventory) {
		DefaultedList<ItemStack> defaultedList = DefaultedList.ofSize(inventory.getInvSize(), ItemStack.EMPTY);

		for (int i = 0; i < defaultedList.size(); i++) {
			ItemStack itemStack = inventory.getInvStack(i);
			if (!itemStack.isEmpty()) {
				if (itemStack.getItem().isFood()) {
					defaultedList.set(i, new ItemStack(itemStack.getItem().getRecipeRemainder()));
				} else if (itemStack.hasNbt() && BannerBlockEntity.getPatternCount(itemStack) > 0) {
					ItemStack itemStack2 = itemStack.copy();
					itemStack2.setCount(1);
					defaultedList.set(i, itemStack2);
				}
			}
		}

		return defaultedList;
	}

	@Override
	public class_3578<?> method_16200() {
		return class_3579.field_17458;
	}

	@Override
	public boolean method_14250(int i, int j) {
		return i * j >= 2;
	}
}
