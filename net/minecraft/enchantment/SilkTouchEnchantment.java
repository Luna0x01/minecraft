package net.minecraft.enchantment;

import net.minecraft.entity.EquipmentSlot;

public class SilkTouchEnchantment extends Enchantment {
	protected SilkTouchEnchantment(Enchantment.Rarity rarity, EquipmentSlot... equipmentSlots) {
		super(rarity, EnchantmentTarget.DIGGER, equipmentSlots);
	}

	@Override
	public int getMinimumPower(int level) {
		return 15;
	}

	@Override
	public int getMaximumPower(int level) {
		return super.getMinimumPower(level) + 50;
	}

	@Override
	public int getMaximumLevel() {
		return 1;
	}

	@Override
	public boolean differs(Enchantment other) {
		return super.differs(other) && other != Enchantments.FORTUNE;
	}
}
