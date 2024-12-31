package net.minecraft.item;

import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.advancement.AchievementsAndCriterions;
import net.minecraft.client.TooltipContext;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.itemgroup.ItemGroup;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionUtil;
import net.minecraft.potion.Potions;
import net.minecraft.stat.Stats;
import net.minecraft.util.ActionResult;
import net.minecraft.util.CommonI18n;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.UseAction;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.world.World;

public class PotionItem extends Item {
	public PotionItem() {
		this.setMaxCount(1);
		this.setItemGroup(ItemGroup.BREWING);
	}

	@Override
	public ItemStack getDefaultStack() {
		return PotionUtil.setPotion(super.getDefaultStack(), Potions.WATER);
	}

	@Override
	public ItemStack method_3367(ItemStack stack, World world, LivingEntity entity) {
		PlayerEntity playerEntity = entity instanceof PlayerEntity ? (PlayerEntity)entity : null;
		if (playerEntity == null || !playerEntity.abilities.creativeMode) {
			stack.decrement(1);
		}

		if (playerEntity instanceof ServerPlayerEntity) {
			AchievementsAndCriterions.field_16353.method_15090((ServerPlayerEntity)playerEntity, stack);
		}

		if (!world.isClient) {
			for (StatusEffectInstance statusEffectInstance : PotionUtil.getPotionEffects(stack)) {
				if (statusEffectInstance.getStatusEffect().isInstant()) {
					statusEffectInstance.getStatusEffect().method_6088(playerEntity, playerEntity, entity, statusEffectInstance.getAmplifier(), 1.0);
				} else {
					entity.addStatusEffect(new StatusEffectInstance(statusEffectInstance));
				}
			}
		}

		if (playerEntity != null) {
			playerEntity.incrementStat(Stats.used(this));
		}

		if (playerEntity == null || !playerEntity.abilities.creativeMode) {
			if (stack.isEmpty()) {
				return new ItemStack(Items.GLASS_BOTTLE);
			}

			if (playerEntity != null) {
				playerEntity.inventory.insertStack(new ItemStack(Items.GLASS_BOTTLE));
			}
		}

		return stack;
	}

	@Override
	public int getMaxUseTime(ItemStack stack) {
		return 32;
	}

	@Override
	public UseAction getUseAction(ItemStack stack) {
		return UseAction.DRINK;
	}

	@Override
	public TypedActionResult<ItemStack> method_13649(World world, PlayerEntity player, Hand hand) {
		player.method_13050(hand);
		return new TypedActionResult<>(ActionResult.SUCCESS, player.getStackInHand(hand));
	}

	@Override
	public String getDisplayName(ItemStack stack) {
		return CommonI18n.translate(PotionUtil.getPotion(stack).method_11414("potion.effect."));
	}

	@Override
	public void appendTooltips(ItemStack stack, @Nullable World world, List<String> tooltip, TooltipContext tooltipContext) {
		PotionUtil.buildTooltip(stack, tooltip, 1.0F);
	}

	@Override
	public boolean hasEnchantmentGlint(ItemStack stack) {
		return super.hasEnchantmentGlint(stack) || !PotionUtil.getPotionEffects(stack).isEmpty();
	}

	@Override
	public void appendToItemGroup(ItemGroup group, DefaultedList<ItemStack> stacks) {
		if (this.canAddTo(group)) {
			for (Potion potion : Potion.REGISTRY) {
				if (potion != Potions.EMPTY) {
					stacks.add(PotionUtil.setPotion(new ItemStack(this), potion));
				}
			}
		}
	}
}
