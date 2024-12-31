package net.minecraft.enchantment;

import net.minecraft.util.Identifier;

public class KnockbackEnchantment extends Enchantment {
	protected KnockbackEnchantment(int i, Identifier identifier, int j) {
		super(i, identifier, j, EnchantmentTarget.WEAPON);
		this.setName("knockback");
	}

	@Override
	public int getMinimumPower(int level) {
		return 5 + 20 * (level - 1);
	}

	@Override
	public int getMaximumPower(int level) {
		return super.getMinimumPower(level) + 50;
	}

	@Override
	public int getMaximumLevel() {
		return 2;
	}
}
