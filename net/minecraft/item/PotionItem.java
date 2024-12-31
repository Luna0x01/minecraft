package net.minecraft.item;

import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.advancement.criterion.Criterions;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionUtil;
import net.minecraft.potion.Potions;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.stat.Stats;
import net.minecraft.text.Text;
import net.minecraft.util.DefaultedList;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.UseAction;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;

public class PotionItem extends Item {
	public PotionItem(Item.Settings settings) {
		super(settings);
	}

	@Override
	public ItemStack getStackForRender() {
		return PotionUtil.setPotion(super.getStackForRender(), Potions.field_8991);
	}

	@Override
	public ItemStack finishUsing(ItemStack itemStack, World world, LivingEntity livingEntity) {
		PlayerEntity playerEntity = livingEntity instanceof PlayerEntity ? (PlayerEntity)livingEntity : null;
		if (playerEntity instanceof ServerPlayerEntity) {
			Criterions.CONSUME_ITEM.trigger((ServerPlayerEntity)playerEntity, itemStack);
		}

		if (!world.isClient) {
			for (StatusEffectInstance statusEffectInstance : PotionUtil.getPotionEffects(itemStack)) {
				if (statusEffectInstance.getEffectType().isInstant()) {
					statusEffectInstance.getEffectType().applyInstantEffect(playerEntity, playerEntity, livingEntity, statusEffectInstance.getAmplifier(), 1.0);
				} else {
					livingEntity.addStatusEffect(new StatusEffectInstance(statusEffectInstance));
				}
			}
		}

		if (playerEntity != null) {
			playerEntity.incrementStat(Stats.field_15372.getOrCreateStat(this));
			if (!playerEntity.abilities.creativeMode) {
				itemStack.decrement(1);
			}
		}

		if (playerEntity == null || !playerEntity.abilities.creativeMode) {
			if (itemStack.isEmpty()) {
				return new ItemStack(Items.field_8469);
			}

			if (playerEntity != null) {
				playerEntity.inventory.insertStack(new ItemStack(Items.field_8469));
			}
		}

		return itemStack;
	}

	@Override
	public int getMaxUseTime(ItemStack itemStack) {
		return 32;
	}

	@Override
	public UseAction getUseAction(ItemStack itemStack) {
		return UseAction.field_8946;
	}

	@Override
	public TypedActionResult<ItemStack> use(World world, PlayerEntity playerEntity, Hand hand) {
		playerEntity.setCurrentHand(hand);
		return TypedActionResult.success(playerEntity.getStackInHand(hand));
	}

	@Override
	public String getTranslationKey(ItemStack itemStack) {
		return PotionUtil.getPotion(itemStack).finishTranslationKey(this.getTranslationKey() + ".effect.");
	}

	@Override
	public void appendTooltip(ItemStack itemStack, @Nullable World world, List<Text> list, TooltipContext tooltipContext) {
		PotionUtil.buildTooltip(itemStack, list, 1.0F);
	}

	@Override
	public boolean hasEnchantmentGlint(ItemStack itemStack) {
		return super.hasEnchantmentGlint(itemStack) || !PotionUtil.getPotionEffects(itemStack).isEmpty();
	}

	@Override
	public void appendStacks(ItemGroup itemGroup, DefaultedList<ItemStack> defaultedList) {
		if (this.isIn(itemGroup)) {
			for (Potion potion : Registry.field_11143) {
				if (potion != Potions.field_8984) {
					defaultedList.add(PotionUtil.setPotion(new ItemStack(this), potion));
				}
			}
		}
	}
}
