package net.minecraft.item;

import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.itemgroup.ItemGroup;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionUtil;
import net.minecraft.stat.Stats;
import net.minecraft.util.ActionResult;
import net.minecraft.util.CommonI18n;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.UseAction;
import net.minecraft.world.World;

public class PotionItem extends Item {
	public PotionItem() {
		this.setMaxCount(1);
		this.setItemGroup(ItemGroup.BREWING);
	}

	@Nullable
	@Override
	public ItemStack method_3367(ItemStack stack, World world, LivingEntity entity) {
		PlayerEntity playerEntity = entity instanceof PlayerEntity ? (PlayerEntity)entity : null;
		if (playerEntity == null || !playerEntity.abilities.creativeMode) {
			stack.count--;
		}

		if (!world.isClient) {
			for (StatusEffectInstance statusEffectInstance : PotionUtil.getPotionEffects(stack)) {
				entity.addStatusEffect(new StatusEffectInstance(statusEffectInstance));
			}
		}

		if (playerEntity != null) {
			playerEntity.incrementStat(Stats.used(this));
		}

		if (playerEntity == null || !playerEntity.abilities.creativeMode) {
			if (stack.count <= 0) {
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
	public TypedActionResult<ItemStack> method_11373(ItemStack itemStack, World world, PlayerEntity playerEntity, Hand hand) {
		playerEntity.method_13050(hand);
		return new TypedActionResult<>(ActionResult.SUCCESS, itemStack);
	}

	@Override
	public String getDisplayName(ItemStack stack) {
		return CommonI18n.translate(PotionUtil.getPotion(stack).method_11414("potion.effect."));
	}

	@Override
	public void appendTooltip(ItemStack stack, PlayerEntity player, List<String> lines, boolean advanced) {
		PotionUtil.buildTooltip(stack, lines, 1.0F);
	}

	@Override
	public boolean hasEnchantmentGlint(ItemStack stack) {
		return !PotionUtil.getPotionEffects(stack).isEmpty();
	}

	@Override
	public void appendItemStacks(Item item, ItemGroup group, List<ItemStack> list) {
		for (Potion potion : Potion.REGISTRY) {
			list.add(PotionUtil.setPotion(new ItemStack(item), potion));
		}
	}
}
