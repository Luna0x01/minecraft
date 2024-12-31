package net.minecraft.item;

import java.util.List;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.itemgroup.ItemGroup;
import net.minecraft.util.Rarity;
import net.minecraft.world.World;

public class AppleItem extends FoodItem {
	public AppleItem(int i, float f, boolean bl) {
		super(i, f, bl);
		this.setUnbreakable(true);
	}

	@Override
	public boolean hasEnchantmentGlint(ItemStack stack) {
		return stack.getData() > 0;
	}

	@Override
	public Rarity getRarity(ItemStack stack) {
		return stack.getData() == 0 ? Rarity.RARE : Rarity.EPIC;
	}

	@Override
	protected void eat(ItemStack stack, World world, PlayerEntity player) {
		if (!world.isClient) {
			player.addStatusEffect(new StatusEffectInstance(StatusEffect.ABSORPTION.id, 2400, 0));
		}

		if (stack.getData() > 0) {
			if (!world.isClient) {
				player.addStatusEffect(new StatusEffectInstance(StatusEffect.REGENERATION.id, 600, 4));
				player.addStatusEffect(new StatusEffectInstance(StatusEffect.RESISTANCE.id, 6000, 0));
				player.addStatusEffect(new StatusEffectInstance(StatusEffect.FIRE_RESISTANCE.id, 6000, 0));
			}
		} else {
			super.eat(stack, world, player);
		}
	}

	@Override
	public void appendItemStacks(Item item, ItemGroup group, List<ItemStack> list) {
		list.add(new ItemStack(item, 1, 0));
		list.add(new ItemStack(item, 1, 1));
	}
}
