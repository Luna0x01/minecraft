package net.minecraft.enchantment;

import net.minecraft.util.Identifier;

public class InfinityEnchantment extends Enchantment {
	public InfinityEnchantment(int i, Identifier identifier, int j) {
		super(i, identifier, j, EnchantmentTarget.BOW);
		this.setName("arrowInfinite");
	}

	@Override
	public int getMinimumPower(int level) {
		return 20;
	}

	@Override
	public int getMaximumPower(int level) {
		return 50;
	}

	@Override
	public int getMaximumLevel() {
		return 1;
	}
}
