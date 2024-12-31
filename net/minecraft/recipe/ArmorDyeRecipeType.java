package net.minecraft.recipe;

import com.google.common.collect.Lists;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.entity.passive.SheepEntity;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.DyeColor;
import net.minecraft.world.World;

public class ArmorDyeRecipeType implements RecipeType {
	@Override
	public boolean matches(CraftingInventory inventory, World world) {
		ItemStack itemStack = null;
		List<ItemStack> list = Lists.newArrayList();

		for (int i = 0; i < inventory.getInvSize(); i++) {
			ItemStack itemStack2 = inventory.getInvStack(i);
			if (itemStack2 != null) {
				if (itemStack2.getItem() instanceof ArmorItem) {
					ArmorItem armorItem = (ArmorItem)itemStack2.getItem();
					if (armorItem.getMaterial() != ArmorItem.Material.LEATHER || itemStack != null) {
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

		return itemStack != null && !list.isEmpty();
	}

	@Nullable
	@Override
	public ItemStack getResult(CraftingInventory inventory) {
		ItemStack itemStack = null;
		int[] is = new int[3];
		int i = 0;
		int j = 0;
		ArmorItem armorItem = null;

		for (int k = 0; k < inventory.getInvSize(); k++) {
			ItemStack itemStack2 = inventory.getInvStack(k);
			if (itemStack2 != null) {
				if (itemStack2.getItem() instanceof ArmorItem) {
					armorItem = (ArmorItem)itemStack2.getItem();
					if (armorItem.getMaterial() != ArmorItem.Material.LEATHER || itemStack != null) {
						return null;
					}

					itemStack = itemStack2.copy();
					itemStack.count = 1;
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
						return null;
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
			return null;
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
