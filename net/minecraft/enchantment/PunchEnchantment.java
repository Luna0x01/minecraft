package net.minecraft.enchantment;

import net.minecraft.util.Identifier;

public class PunchEnchantment extends Enchantment {
	public PunchEnchantment(int i, Identifier identifier, int j) {
		super(i, identifier, j, EnchantmentTarget.BOW);
		this.setName("arrowKnockback");
	}

	@Override
	public int getMinimumPower(int level) {
		return 12 + (level - 1) * 20;
	}

	@Override
	public int getMaximumPower(int level) {
		return this.getMinimumPower(level) + 25;
	}

	@Override
	public int getMaximumLevel() {
		return 2;
	}
}
