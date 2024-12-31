package net.minecraft.enchantment;

import net.minecraft.entity.EquipmentSlot;

public class KnockbackEnchantment extends Enchantment {
	protected KnockbackEnchantment(Enchantment.Rarity rarity, EquipmentSlot... equipmentSlots) {
		super(rarity, EnchantmentTarget.WEAPON, equipmentSlots);
		this.setName("knockback");
	}

	@Override
	public int getMinimumPower(int level) {
		return 5 + 20 * (level - 1);
	}

	@Override
	public int getMaximumPower(int level) {
		return super.getMinimumPower(level) + 50;
	}

	@Override
	public int getMaximumLevel() {
		return 2;
	}
}
