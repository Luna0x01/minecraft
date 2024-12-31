package net.minecraft.item;

import java.util.List;
import net.minecraft.advancement.AchievementsAndCriterions;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
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
			if (stack.getData() > 0) {
				player.incrementStat(AchievementsAndCriterions.field_14356);
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
	public void appendItemStacks(Item item, ItemGroup group, List<ItemStack> list) {
		list.add(new ItemStack(item));
		list.add(new ItemStack(item, 1, 1));
	}
}
