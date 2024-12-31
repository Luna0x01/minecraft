package net.minecraft.recipe;

import com.google.common.collect.Lists;
import java.util.List;
import net.minecraft.class_3571;
import net.minecraft.class_3578;
import net.minecraft.class_3579;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;

public class RepairingRecipeType extends class_3571 {
	public RepairingRecipeType(Identifier identifier) {
		super(identifier);
	}

	@Override
	public boolean method_3500(Inventory inventory, World world) {
		if (!(inventory instanceof CraftingInventory)) {
			return false;
		} else {
			List<ItemStack> list = Lists.newArrayList();

			for (int i = 0; i < inventory.getInvSize(); i++) {
				ItemStack itemStack = inventory.getInvStack(i);
				if (!itemStack.isEmpty()) {
					list.add(itemStack);
					if (list.size() > 1) {
						ItemStack itemStack2 = (ItemStack)list.get(0);
						if (itemStack.getItem() != itemStack2.getItem() || itemStack2.getCount() != 1 || itemStack.getCount() != 1 || !itemStack2.getItem().isDamageable()) {
							return false;
						}
					}
				}
			}

			return list.size() == 2;
		}
	}

	@Override
	public ItemStack method_16201(Inventory inventory) {
		List<ItemStack> list = Lists.newArrayList();

		for (int i = 0; i < inventory.getInvSize(); i++) {
			ItemStack itemStack = inventory.getInvStack(i);
			if (!itemStack.isEmpty()) {
				list.add(itemStack);
				if (list.size() > 1) {
					ItemStack itemStack2 = (ItemStack)list.get(0);
					if (itemStack.getItem() != itemStack2.getItem() || itemStack2.getCount() != 1 || itemStack.getCount() != 1 || !itemStack2.getItem().isDamageable()) {
						return ItemStack.EMPTY;
					}
				}
			}
		}

		if (list.size() == 2) {
			ItemStack itemStack3 = (ItemStack)list.get(0);
			ItemStack itemStack4 = (ItemStack)list.get(1);
			if (itemStack3.getItem() == itemStack4.getItem() && itemStack3.getCount() == 1 && itemStack4.getCount() == 1 && itemStack3.getItem().isDamageable()) {
				Item item = itemStack3.getItem();
				int j = item.getMaxDamage() - itemStack3.getDamage();
				int k = item.getMaxDamage() - itemStack4.getDamage();
				int l = j + k + item.getMaxDamage() * 5 / 100;
				int m = item.getMaxDamage() - l;
				if (m < 0) {
					m = 0;
				}

				ItemStack itemStack5 = new ItemStack(itemStack3.getItem());
				itemStack5.setDamage(m);
				return itemStack5;
			}
		}

		return ItemStack.EMPTY;
	}

	@Override
	public boolean method_14250(int i, int j) {
		return i * j >= 2;
	}

	@Override
	public class_3578<?> method_16200() {
		return class_3579.field_17456;
	}
}
