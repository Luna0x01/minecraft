package net.minecraft.enchantment;

import net.minecraft.util.collection.Weighting;

public class EnchantmentLevelEntry extends Weighting.Weight {
	public final Enchantment enchantment;
	public final int level;

	public EnchantmentLevelEntry(Enchantment enchantment, int i) {
		super(enchantment.getRarity().getChance());
		this.enchantment = enchantment;
		this.level = i;
	}
}
