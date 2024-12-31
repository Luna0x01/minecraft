package net.minecraft.item;

import net.minecraft.entity.EquipmentSlot;
import net.minecraft.recipe.Ingredient;
import net.minecraft.sound.SoundEvent;

public interface ArmorMaterial {
	int getDurability(EquipmentSlot equipmentSlot);

	int getProtectionAmount(EquipmentSlot equipmentSlot);

	int getEnchantability();

	SoundEvent getEquipSound();

	Ingredient getRepairIngredient();

	String getName();

	float getToughness();
}
