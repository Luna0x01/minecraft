package net.minecraft;

import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.FoodItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class class_3550 extends FoodItem {
	public class_3550(int i, float f, boolean bl, Item.Settings settings) {
		super(i, f, bl, settings);
	}

	@Override
	public boolean hasEnchantmentGlint(ItemStack stack) {
		return true;
	}

	@Override
	protected void eat(ItemStack stack, World world, PlayerEntity player) {
		if (!world.isClient) {
			player.method_2654(new StatusEffectInstance(StatusEffects.REGENERATION, 400, 1));
			player.method_2654(new StatusEffectInstance(StatusEffects.RESISTANCE, 6000, 0));
			player.method_2654(new StatusEffectInstance(StatusEffects.FIRE_RESISTANCE, 6000, 0));
			player.method_2654(new StatusEffectInstance(StatusEffects.ABSORPTION, 2400, 3));
		}
	}
}
