package net.minecraft.enchantment;

import net.minecraft.util.Identifier;

public class FireAspectEnchantment extends Enchantment {
	protected FireAspectEnchantment(int i, Identifier identifier, int j) {
		super(i, identifier, j, EnchantmentTarget.WEAPON);
		this.setName("fire");
	}

	@Override
	public int getMinimumPower(int level) {
		return 10 + 20 * (level - 1);
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
