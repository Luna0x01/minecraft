package net.minecraft.item;

import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.itemgroup.ItemGroup;
import net.minecraft.util.Rarity;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.world.World;

public class AppleItem extends FoodItem {
	public AppleItem(int i, float f, boolean bl) {
		super(i, f, bl);
		this.setUnbreakable(true);
	}

	@Override
	public boolean hasEnchantmentGlint(ItemStack stack) {
		return super.hasEnchantmentGlint(stack) || stack.getData() > 0;
	}

	@Override
	public Rarity getRarity(ItemStack stack) {
		return stack.getData() == 0 ? Rarity.RARE : Rarity.EPIC;
	}

	@Override
	protected void eat(ItemStack stack, World world, PlayerEntity player) {
		if (!world.isClient) {
			if (stack.getData() > 0) {
				player.addStatusEffect(new StatusEffectInstance(StatusEffects.REGENERATION, 400, 1));
				player.addStatusEffect(new StatusEffectInstance(StatusEffects.RESISTANCE, 6000, 0));
				player.addStatusEffect(new StatusEffectInstance(StatusEffects.FIRE_RESISTANCE, 6000, 0));
				player.addStatusEffect(new StatusEffectInstance(StatusEffects.ABSORPTION, 2400, 3));
			} else {
				player.addStatusEffect(new StatusEffectInstance(StatusEffects.REGENERATION, 100, 1));
				player.addStatusEffect(new StatusEffectInstance(StatusEffects.ABSORPTION, 2400, 0));
			}
		}
	}

	@Override
	public void appendToItemGroup(ItemGroup group, DefaultedList<ItemStack> stacks) {
		if (this.canAddTo(group)) {
			stacks.add(new ItemStack(this));
			stacks.add(new ItemStack(this, 1, 1));
		}
	}
}
