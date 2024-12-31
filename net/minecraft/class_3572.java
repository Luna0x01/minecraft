package net.minecraft;

import net.minecraft.inventory.CraftingInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.recipe.Ingredient;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;

public class class_3572 extends class_3571 {
	private static final Ingredient field_17430 = Ingredient.ofItems(Items.PAPER);
	private static final Ingredient field_17431 = Ingredient.ofItems(Items.GUNPOWDER);
	private static final Ingredient field_17432 = Ingredient.ofItems(Items.FIREWORK_STAR);

	public class_3572(Identifier identifier) {
		super(identifier);
	}

	@Override
	public boolean method_3500(Inventory inventory, World world) {
		if (!(inventory instanceof CraftingInventory)) {
			return false;
		} else {
			boolean bl = false;
			int i = 0;

			for (int j = 0; j < inventory.getInvSize(); j++) {
				ItemStack itemStack = inventory.getInvStack(j);
				if (!itemStack.isEmpty()) {
					if (field_17430.test(itemStack)) {
						if (bl) {
							return false;
						}

						bl = true;
					} else if (field_17431.test(itemStack)) {
						if (++i > 3) {
							return false;
						}
					} else if (!field_17432.test(itemStack)) {
						return false;
					}
				}
			}

			return bl && i >= 1;
		}
	}

	@Override
	public ItemStack method_16201(Inventory inventory) {
		ItemStack itemStack = new ItemStack(Items.FIREWORK_ROCKET, 3);
		NbtCompound nbtCompound = itemStack.getOrCreateNbtCompound("Fireworks");
		NbtList nbtList = new NbtList();
		int i = 0;

		for (int j = 0; j < inventory.getInvSize(); j++) {
			ItemStack itemStack2 = inventory.getInvStack(j);
			if (!itemStack2.isEmpty()) {
				if (field_17431.test(itemStack2)) {
					i++;
				} else if (field_17432.test(itemStack2)) {
					NbtCompound nbtCompound2 = itemStack2.getNbtCompound("Explosion");
					if (nbtCompound2 != null) {
						nbtList.add((NbtElement)nbtCompound2);
					}
				}
			}
		}

		nbtCompound.putByte("Flight", (byte)i);
		if (!nbtList.isEmpty()) {
			nbtCompound.put("Explosions", nbtList);
		}

		return itemStack;
	}

	@Override
	public boolean method_14250(int i, int j) {
		return i * j >= 2;
	}

	@Override
	public ItemStack getOutput() {
		return new ItemStack(Items.FIREWORK_ROCKET);
	}

	@Override
	public class_3578<?> method_16200() {
		return class_3579.field_17453;
	}
}
