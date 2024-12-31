package net.minecraft;

import com.google.common.collect.Lists;
import java.util.List;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.DyeItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.recipe.Ingredient;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;

public class class_3573 extends class_3571 {
	private static final Ingredient field_17433 = Ingredient.ofItems(Items.FIREWORK_STAR);

	public class_3573(Identifier identifier) {
		super(identifier);
	}

	@Override
	public boolean method_3500(Inventory inventory, World world) {
		if (!(inventory instanceof CraftingInventory)) {
			return false;
		} else {
			boolean bl = false;
			boolean bl2 = false;

			for (int i = 0; i < inventory.getInvSize(); i++) {
				ItemStack itemStack = inventory.getInvStack(i);
				if (!itemStack.isEmpty()) {
					if (itemStack.getItem() instanceof DyeItem) {
						bl = true;
					} else {
						if (!field_17433.test(itemStack)) {
							return false;
						}

						if (bl2) {
							return false;
						}

						bl2 = true;
					}
				}
			}

			return bl2 && bl;
		}
	}

	@Override
	public ItemStack method_16201(Inventory inventory) {
		List<Integer> list = Lists.newArrayList();
		ItemStack itemStack = null;

		for (int i = 0; i < inventory.getInvSize(); i++) {
			ItemStack itemStack2 = inventory.getInvStack(i);
			Item item = itemStack2.getItem();
			if (item instanceof DyeItem) {
				list.add(((DyeItem)item).method_16047().getSwappedId());
			} else if (field_17433.test(itemStack2)) {
				itemStack = itemStack2.copy();
				itemStack.setCount(1);
			}
		}

		if (itemStack != null && !list.isEmpty()) {
			itemStack.getOrCreateNbtCompound("Explosion").putIntArray("FadeColors", list);
			return itemStack;
		} else {
			return ItemStack.EMPTY;
		}
	}

	@Override
	public boolean method_14250(int i, int j) {
		return i * j >= 2;
	}

	@Override
	public class_3578<?> method_16200() {
		return class_3579.field_17455;
	}
}
