package net.minecraft.enchantment;

import net.minecraft.entity.EquipmentSlot;

public class MendingEnchantment extends Enchantment {
	public MendingEnchantment(Enchantment.Rarity rarity, EquipmentSlot... equipmentSlots) {
		super(rarity, EnchantmentTarget.BREAKABLE, equipmentSlots);
	}

	@Override
	public int getMinimumPower(int level) {
		return level * 25;
	}

	@Override
	public int getMaximumPower(int level) {
		return this.getMinimumPower(level) + 50;
	}

	@Override
	public boolean isTreasure() {
		return true;
	}

	@Override
	public int getMaximumLevel() {
		return 1;
	}
}
