package net.minecraft.recipe;

import com.google.common.collect.Lists;
import java.util.List;
import net.minecraft.class_3571;
import net.minecraft.class_3578;
import net.minecraft.class_3579;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.DyeItem;
import net.minecraft.item.DyeableArmorItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;

public class ArmorDyeRecipeType extends class_3571 {
	public ArmorDyeRecipeType(Identifier identifier) {
		super(identifier);
	}

	@Override
	public boolean method_3500(Inventory inventory, World world) {
		if (!(inventory instanceof CraftingInventory)) {
			return false;
		} else {
			ItemStack itemStack = ItemStack.EMPTY;
			List<ItemStack> list = Lists.newArrayList();

			for (int i = 0; i < inventory.getInvSize(); i++) {
				ItemStack itemStack2 = inventory.getInvStack(i);
				if (!itemStack2.isEmpty()) {
					if (itemStack2.getItem() instanceof DyeableArmorItem) {
						if (!itemStack.isEmpty()) {
							return false;
						}

						itemStack = itemStack2;
					} else {
						if (!(itemStack2.getItem() instanceof DyeItem)) {
							return false;
						}

						list.add(itemStack2);
					}
				}
			}

			return !itemStack.isEmpty() && !list.isEmpty();
		}
	}

	@Override
	public ItemStack method_16201(Inventory inventory) {
		ItemStack itemStack = ItemStack.EMPTY;
		int[] is = new int[3];
		int i = 0;
		int j = 0;
		DyeableArmorItem dyeableArmorItem = null;

		for (int k = 0; k < inventory.getInvSize(); k++) {
			ItemStack itemStack2 = inventory.getInvStack(k);
			if (!itemStack2.isEmpty()) {
				Item item = itemStack2.getItem();
				if (item instanceof DyeableArmorItem) {
					dyeableArmorItem = (DyeableArmorItem)item;
					if (!itemStack.isEmpty()) {
						return ItemStack.EMPTY;
					}

					itemStack = itemStack2.copy();
					itemStack.setCount(1);
					if (dyeableArmorItem.method_16049(itemStack2)) {
						int l = dyeableArmorItem.method_16050(itemStack);
						float f = (float)(l >> 16 & 0xFF) / 255.0F;
						float g = (float)(l >> 8 & 0xFF) / 255.0F;
						float h = (float)(l & 0xFF) / 255.0F;
						i = (int)((float)i + Math.max(f, Math.max(g, h)) * 255.0F);
						is[0] = (int)((float)is[0] + f * 255.0F);
						is[1] = (int)((float)is[1] + g * 255.0F);
						is[2] = (int)((float)is[2] + h * 255.0F);
						j++;
					}
				} else {
					if (!(item instanceof DyeItem)) {
						return ItemStack.EMPTY;
					}

					float[] fs = ((DyeItem)item).method_16047().getColorComponents();
					int m = (int)(fs[0] * 255.0F);
					int n = (int)(fs[1] * 255.0F);
					int o = (int)(fs[2] * 255.0F);
					i += Math.max(m, Math.max(n, o));
					is[0] += m;
					is[1] += n;
					is[2] += o;
					j++;
				}
			}
		}

		if (dyeableArmorItem == null) {
			return ItemStack.EMPTY;
		} else {
			int p = is[0] / j;
			int q = is[1] / j;
			int r = is[2] / j;
			float s = (float)i / (float)j;
			float t = (float)Math.max(p, Math.max(q, r));
			p = (int)((float)p * s / t);
			q = (int)((float)q * s / t);
			r = (int)((float)r * s / t);
			int var25 = (p << 8) + q;
			var25 = (var25 << 8) + r;
			dyeableArmorItem.method_16048(itemStack, var25);
			return itemStack;
		}
	}

	@Override
	public boolean method_14250(int i, int j) {
		return i * j >= 2;
	}

	@Override
	public class_3578<?> method_16200() {
		return class_3579.field_17449;
	}
}
