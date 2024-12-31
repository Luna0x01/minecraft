package net.minecraft.enchantment;

import net.minecraft.entity.EquipmentSlot;

public class RiptideEnchantment extends Enchantment {
	public RiptideEnchantment(Enchantment.Rarity rarity, EquipmentSlot... equipmentSlots) {
		super(rarity, EnchantmentTarget.TRIDENT, equipmentSlots);
	}

	@Override
	public int getMinimumPower(int level) {
		return 10 + level * 7;
	}

	@Override
	public int getMaximumPower(int level) {
		return 50;
	}

	@Override
	public int getMaximumLevel() {
		return 3;
	}

	@Override
	public boolean differs(Enchantment other) {
		return super.differs(other) && other != Enchantments.LOYALTY && other != Enchantments.CHANNELING;
	}
}
