package net.minecraft.enchantment;

import net.minecraft.entity.EquipmentSlot;

public class VanishingCurseEnchantment extends Enchantment {
	public VanishingCurseEnchantment(Enchantment.Rarity rarity, EquipmentSlot... equipmentSlots) {
		super(rarity, EnchantmentTarget.ALL, equipmentSlots);
		this.setName("vanishing_curse");
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
	public boolean isTreasure() {
		return true;
	}

	@Override
	public boolean isCursed() {
		return true;
	}
}
