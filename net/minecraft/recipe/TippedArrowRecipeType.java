package net.minecraft.recipe;

import net.minecraft.class_3571;
import net.minecraft.class_3578;
import net.minecraft.class_3579;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.potion.PotionUtil;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;

public class TippedArrowRecipeType extends class_3571 {
	public TippedArrowRecipeType(Identifier identifier) {
		super(identifier);
	}

	@Override
	public boolean method_3500(Inventory inventory, World world) {
		if (inventory.method_11260() == 3 && inventory.method_11259() == 3) {
			for (int i = 0; i < inventory.method_11260(); i++) {
				for (int j = 0; j < inventory.method_11259(); j++) {
					ItemStack itemStack = inventory.getInvStack(i + j * inventory.method_11260());
					if (itemStack.isEmpty()) {
						return false;
					}

					Item item = itemStack.getItem();
					if (i == 1 && j == 1) {
						if (item != Items.LINGERING_POTION) {
							return false;
						}
					} else if (item != Items.ARROW) {
						return false;
					}
				}
			}

			return true;
		} else {
			return false;
		}
	}

	@Override
	public ItemStack method_16201(Inventory inventory) {
		ItemStack itemStack = inventory.getInvStack(1 + inventory.method_11260());
		if (itemStack.getItem() != Items.LINGERING_POTION) {
			return ItemStack.EMPTY;
		} else {
			ItemStack itemStack2 = new ItemStack(Items.TIPPED_ARROW, 8);
			PotionUtil.setPotion(itemStack2, PotionUtil.getPotion(itemStack));
			PotionUtil.setCustomPotionEffects(itemStack2, PotionUtil.getCustomPotionEffects(itemStack));
			return itemStack2;
		}
	}

	@Override
	public boolean method_14250(int i, int j) {
		return i >= 2 && j >= 2;
	}

	@Override
	public class_3578<?> method_16200() {
		return class_3579.field_17457;
	}
}
