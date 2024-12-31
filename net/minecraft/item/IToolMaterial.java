package net.minecraft.item;

import net.minecraft.recipe.Ingredient;

public interface IToolMaterial {
	int getDurability();

	float getBlockBreakSpeed();

	float getAttackDamage();

	int getMiningLevel();

	int getEnchantability();

	Ingredient getRepairIngredient();
}
