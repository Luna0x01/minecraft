package net.minecraft.recipe;

import com.google.common.collect.Lists;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class RepairingRecipeType implements RecipeType {
	@Override
	public boolean matches(CraftingInventory inventory, World world) {
		List<ItemStack> list = Lists.newArrayList();

		for (int i = 0; i < inventory.getInvSize(); i++) {
			ItemStack itemStack = inventory.getInvStack(i);
			if (itemStack != null) {
				list.add(itemStack);
				if (list.size() > 1) {
					ItemStack itemStack2 = (ItemStack)list.get(0);
					if (itemStack.getItem() != itemStack2.getItem() || itemStack2.count != 1 || itemStack.count != 1 || !itemStack2.getItem().isDamageable()) {
						return false;
					}
				}
			}
		}

		return list.size() == 2;
	}

	@Nullable
	@Override
	public ItemStack getResult(CraftingInventory inventory) {
		List<ItemStack> list = Lists.newArrayList();

		for (int i = 0; i < inventory.getInvSize(); i++) {
			ItemStack itemStack = inventory.getInvStack(i);
			if (itemStack != null) {
				list.add(itemStack);
				if (list.size() > 1) {
					ItemStack itemStack2 = (ItemStack)list.get(0);
					if (itemStack.getItem() != itemStack2.getItem() || itemStack2.count != 1 || itemStack.count != 1 || !itemStack2.getItem().isDamageable()) {
						return null;
					}
				}
			}
		}

		if (list.size() == 2) {
			ItemStack itemStack3 = (ItemStack)list.get(0);
			ItemStack itemStack4 = (ItemStack)list.get(1);
			if (itemStack3.getItem() == itemStack4.getItem() && itemStack3.count == 1 && itemStack4.count == 1 && itemStack3.getItem().isDamageable()) {
				Item item = itemStack3.getItem();
				int j = item.getMaxDamage() - itemStack3.getDamage();
				int k = item.getMaxDamage() - itemStack4.getDamage();
				int l = j + k + item.getMaxDamage() * 5 / 100;
				int m = item.getMaxDamage() - l;
				if (m < 0) {
					m = 0;
				}

				return new ItemStack(itemStack3.getItem(), 1, m);
			}
		}

		return null;
	}

	@Override
	public int getSize() {
		return 4;
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
			if (itemStack != null && itemStack.getItem().isFood()) {
				itemStacks[i] = new ItemStack(itemStack.getItem().getRecipeRemainder());
			}
		}

		return itemStacks;
	}
}
