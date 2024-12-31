package net.minecraft.enchantment;

import net.minecraft.entity.EquipmentSlot;

public class InfinityEnchantment extends Enchantment {
	public InfinityEnchantment(Enchantment.Rarity rarity, EquipmentSlot... equipmentSlots) {
		super(rarity, EnchantmentTarget.BOW, equipmentSlots);
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
