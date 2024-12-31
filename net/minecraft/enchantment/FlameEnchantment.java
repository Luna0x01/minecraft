package net.minecraft.enchantment;

import net.minecraft.entity.EquipmentSlot;

public class FlameEnchantment extends Enchantment {
	public FlameEnchantment(Enchantment.Rarity rarity, EquipmentSlot... equipmentSlots) {
		super(rarity, EnchantmentTarget.BOW, equipmentSlots);
		this.setName("arrowFire");
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
