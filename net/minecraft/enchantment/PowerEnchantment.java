package net.minecraft.enchantment;

import net.minecraft.util.Identifier;

public class PowerEnchantment extends Enchantment {
	public PowerEnchantment(int i, Identifier identifier, int j) {
		super(i, identifier, j, EnchantmentTarget.BOW);
		this.setName("arrowDamage");
	}

	@Override
	public int getMinimumPower(int level) {
		return 1 + (level - 1) * 10;
	}

	@Override
	public int getMaximumPower(int level) {
		return this.getMinimumPower(level) + 15;
	}

	@Override
	public int getMaximumLevel() {
		return 5;
	}
}
