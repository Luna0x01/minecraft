package net.minecraft.enchantment;

import net.minecraft.entity.EquipmentSlot;

public class DepthStriderEnchantment extends Enchantment {
	public DepthStriderEnchantment(Enchantment.Rarity rarity, EquipmentSlot... equipmentSlots) {
		super(rarity, EnchantmentTarget.FEET, equipmentSlots);
		this.setName("waterWalker");
	}

	@Override
	public int getMinimumPower(int level) {
		return level * 10;
	}

	@Override
	public int getMaximumPower(int level) {
		return this.getMinimumPower(level) + 15;
	}

	@Override
	public int getMaximumLevel() {
		return 3;
	}

	@Override
	public boolean differs(Enchantment other) {
		return super.differs(other) && other != Enchantments.FROST_WALKER;
	}
}
