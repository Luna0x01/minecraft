package net.minecraft.enchantment;

import net.minecraft.util.Identifier;

public class RespirationEnchantment extends Enchantment {
	public RespirationEnchantment(int i, Identifier identifier, int j) {
		super(i, identifier, j, EnchantmentTarget.HEAD);
		this.setName("oxygen");
	}

	@Override
	public int getMinimumPower(int level) {
		return 10 * level;
	}

	@Override
	public int getMaximumPower(int level) {
		return this.getMinimumPower(level) + 30;
	}

	@Override
	public int getMaximumLevel() {
		return 3;
	}
}
