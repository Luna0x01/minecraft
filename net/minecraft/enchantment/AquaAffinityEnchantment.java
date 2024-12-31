package net.minecraft.enchantment;

import net.minecraft.util.Identifier;

public class AquaAffinityEnchantment extends Enchantment {
	public AquaAffinityEnchantment(int i, Identifier identifier, int j) {
		super(i, identifier, j, EnchantmentTarget.HEAD);
		this.setName("waterWorker");
	}

	@Override
	public int getMinimumPower(int level) {
		return 1;
	}

	@Override
	public int getMaximumPower(int level) {
		return this.getMinimumPower(level) + 40;
	}

	@Override
	public int getMaximumLevel() {
		return 1;
	}
}
