package net.minecraft.enchantment;

import net.minecraft.util.Identifier;

public class LureEnchantment extends Enchantment {
	protected LureEnchantment(int i, Identifier identifier, int j, EnchantmentTarget enchantmentTarget) {
		super(i, identifier, j, enchantmentTarget);
		this.setName("fishingSpeed");
	}

	@Override
	public int getMinimumPower(int level) {
		return 15 + (level - 1) * 9;
	}

	@Override
	public int getMaximumPower(int level) {
		return super.getMinimumPower(level) + 50;
	}

	@Override
	public int getMaximumLevel() {
		return 3;
	}
}
