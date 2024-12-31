package net.minecraft.enchantment;

import net.minecraft.class_3462;
import net.minecraft.entity.EquipmentSlot;

public class ImpalingEnchantment extends Enchantment {
	public ImpalingEnchantment(Enchantment.Rarity rarity, EquipmentSlot... equipmentSlots) {
		super(rarity, EnchantmentTarget.TRIDENT, equipmentSlots);
	}

	@Override
	public int getMinimumPower(int level) {
		return 1 + (level - 1) * 8;
	}

	@Override
	public int getMaximumPower(int level) {
		return this.getMinimumPower(level) + 20;
	}

	@Override
	public int getMaximumLevel() {
		return 5;
	}

	@Override
	public float method_5489(int i, class_3462 arg) {
		return arg == class_3462.field_16822 ? (float)i * 2.5F : 0.0F;
	}
}
