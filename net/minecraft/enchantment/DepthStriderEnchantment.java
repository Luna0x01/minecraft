package net.minecraft.enchantment;

import net.minecraft.util.Identifier;

public class DepthStriderEnchantment extends Enchantment {
	public DepthStriderEnchantment(int i, Identifier identifier, int j) {
		super(i, identifier, j, EnchantmentTarget.FEET);
		this.setName("waterWalker");
	}

	@Override
	public int getMinimumPower(int level) {
		return level * 10;
	}

	@Override
	public int getMaximumPower(int level) {
		return this.getMinimumPower(level) + 15;
	}

	@Override
	public int getMaximumLevel() {
		return 3;
	}
}
