package net.minecraft.util;

import java.util.Random;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.util.collection.Weighting;

public class WeightedRandomFishingLoot extends Weighting.Weight {
	private final ItemStack stack;
	private float damagePercent;
	private boolean enchantable;

	public WeightedRandomFishingLoot(ItemStack itemStack, int i) {
		super(i);
		this.stack = itemStack;
	}

	public ItemStack getItemStack(Random random) {
		ItemStack itemStack = this.stack.copy();
		if (this.damagePercent > 0.0F) {
			int i = (int)(this.damagePercent * (float)this.stack.getMaxDamage());
			int j = itemStack.getMaxDamage() - random.nextInt(random.nextInt(i) + 1);
			if (j > i) {
				j = i;
			}

			if (j < 1) {
				j = 1;
			}

			itemStack.setDamage(j);
		}

		if (this.enchantable) {
			EnchantmentHelper.addRandomEnchantment(random, itemStack, 30);
		}

		return itemStack;
	}

	public WeightedRandomFishingLoot setDamagePercent(float percent) {
		this.damagePercent = percent;
		return this;
	}

	public WeightedRandomFishingLoot setEnchantable() {
		this.enchantable = true;
		return this;
	}
}
