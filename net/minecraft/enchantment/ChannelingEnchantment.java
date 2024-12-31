package net.minecraft.enchantment;

import net.minecraft.entity.EquipmentSlot;

public class ChannelingEnchantment extends Enchantment {
	public ChannelingEnchantment(Enchantment.Rarity rarity, EquipmentSlot... equipmentSlots) {
		super(rarity, EnchantmentTarget.TRIDENT, equipmentSlots);
	}

	@Override
	public int getMinimumPower(int level) {
		return 25;
	}

	@Override
	public int getMaximumPower(int level) {
		return 50;
	}

	@Override
	public int getMaximumLevel() {
		return 1;
	}

	@Override
	public boolean differs(Enchantment other) {
		return super.differs(other);
	}
}
