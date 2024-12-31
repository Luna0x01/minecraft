package net.minecraft.item;

import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.World;

public class AppleItem extends FoodItem {
	public AppleItem(int i, float f, boolean bl, Item.Settings settings) {
		super(i, f, bl, settings);
	}

	@Override
	protected void eat(ItemStack stack, World world, PlayerEntity player) {
		if (!world.isClient) {
			player.method_2654(new StatusEffectInstance(StatusEffects.REGENERATION, 100, 1));
			player.method_2654(new StatusEffectInstance(StatusEffects.ABSORPTION, 2400, 0));
		}
	}
}
