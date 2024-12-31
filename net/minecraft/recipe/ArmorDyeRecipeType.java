package net.minecraft.recipe;

import com.google.common.collect.Lists;
import java.util.List;
import net.minecraft.entity.passive.SheepEntity;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.DyeColor;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.world.World;

public class ArmorDyeRecipeType implements RecipeType {
	@Override
	public boolean matches(CraftingInventory inventory, World world) {
		ItemStack itemStack = ItemStack.EMPTY;
		List<ItemStack> list = Lists.newArrayList();

		for (int i = 0; i < inventory.getInvSize(); i++) {
			ItemStack itemStack2 = inventory.getInvStack(i);
			if (!itemStack2.isEmpty()) {
				if (itemStack2.getItem() instanceof ArmorItem) {
					ArmorItem armorItem = (ArmorItem)itemStack2.getItem();
					if (armorItem.getMaterial() != ArmorItem.Material.LEATHER || !itemStack.isEmpty()) {
						return false;
					}

					itemStack = itemStack2;
				} else {
					if (itemStack2.getItem() != Items.DYE) {
						return false;
					}

					list.add(itemStack2);
				}
			}
		}

		return !itemStack.isEmpty() && !list.isEmpty();
	}

	@Override
	public ItemStack getResult(CraftingInventory inventory) {
		ItemStack itemStack = ItemStack.EMPTY;
		int[] is = new int[3];
		int i = 0;
		int j = 0;
		ArmorItem armorItem = null;

		for (int k = 0; k < inventory.getInvSize(); k++) {
			ItemStack itemStack2 = inventory.getInvStack(k);
			if (!itemStack2.isEmpty()) {
				if (itemStack2.getItem() instanceof ArmorItem) {
					armorItem = (ArmorItem)itemStack2.getItem();
					if (armorItem.getMaterial() != ArmorItem.Material.LEATHER || !itemStack.isEmpty()) {
						return ItemStack.EMPTY;
					}

					itemStack = itemStack2.copy();
					itemStack.setCount(1);
					if (armorItem.hasColor(itemStack2)) {
						int l = armorItem.getColor(itemStack);
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
					if (itemStack2.getItem() != Items.DYE) {
						return ItemStack.EMPTY;
					}

					float[] fs = SheepEntity.getDyedColor(DyeColor.getById(itemStack2.getData()));
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

		if (armorItem == null) {
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
			armorItem.setColor(itemStack, var25);
			return itemStack;
		}
	}

	@Override
	public int getSize() {
		return 10;
	}

	@Override
	public ItemStack getOutput() {
		return ItemStack.EMPTY;
	}

	@Override
	public DefaultedList<ItemStack> method_13670(CraftingInventory craftingInventory) {
		DefaultedList<ItemStack> defaultedList = DefaultedList.ofSize(craftingInventory.getInvSize(), ItemStack.EMPTY);

		for (int i = 0; i < defaultedList.size(); i++) {
			ItemStack itemStack = craftingInventory.getInvStack(i);
			if (itemStack.getItem().isFood()) {
				defaultedList.set(i, new ItemStack(itemStack.getItem().getRecipeRemainder()));
			}
		}

		return defaultedList;
	}
}
