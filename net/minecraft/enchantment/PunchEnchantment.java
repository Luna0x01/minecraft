package net.minecraft.enchantment;

import net.minecraft.entity.EquipmentSlot;

public class PunchEnchantment extends Enchantment {
	public PunchEnchantment(Enchantment.Rarity rarity, EquipmentSlot... equipmentSlots) {
		super(rarity, EnchantmentTarget.BOW, equipmentSlots);
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
