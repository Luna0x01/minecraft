package net.minecraft.item;

import net.minecraft.advancement.AchievementsAndCriterions;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.itemgroup.ItemGroup;
import net.minecraft.stat.Stats;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.UseAction;
import net.minecraft.world.World;

public class MilkBucketItem extends Item {
	public MilkBucketItem() {
		this.setMaxCount(1);
		this.setItemGroup(ItemGroup.MISC);
	}

	@Override
	public ItemStack method_3367(ItemStack stack, World world, LivingEntity entity) {
		if (entity instanceof ServerPlayerEntity) {
			ServerPlayerEntity serverPlayerEntity = (ServerPlayerEntity)entity;
			AchievementsAndCriterions.field_16353.method_15090(serverPlayerEntity, stack);
			serverPlayerEntity.incrementStat(Stats.used(this));
		}

		if (entity instanceof PlayerEntity && !((PlayerEntity)entity).abilities.creativeMode) {
			stack.decrement(1);
		}

		if (!world.isClient) {
			entity.clearStatusEffects();
		}

		return stack.isEmpty() ? new ItemStack(Items.BUCKET) : stack;
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
}
