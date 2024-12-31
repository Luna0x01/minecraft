package net.minecraft.item;

import net.minecraft.advancement.criterion.Criterions;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.stat.Stats;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.UseAction;
import net.minecraft.world.World;

public class HoneyBottleItem extends Item {
	public HoneyBottleItem(Item.Settings settings) {
		super(settings);
	}

	@Override
	public ItemStack finishUsing(ItemStack itemStack, World world, LivingEntity livingEntity) {
		super.finishUsing(itemStack, world, livingEntity);
		if (livingEntity instanceof ServerPlayerEntity) {
			ServerPlayerEntity serverPlayerEntity = (ServerPlayerEntity)livingEntity;
			Criterions.CONSUME_ITEM.trigger(serverPlayerEntity, itemStack);
			serverPlayerEntity.incrementStat(Stats.field_15372.getOrCreateStat(this));
		}

		if (!world.isClient) {
			livingEntity.removeStatusEffect(StatusEffects.field_5899);
		}

		if (itemStack.isEmpty()) {
			return new ItemStack(Items.field_8469);
		} else {
			if (livingEntity instanceof PlayerEntity && !((PlayerEntity)livingEntity).abilities.creativeMode) {
				ItemStack itemStack2 = new ItemStack(Items.field_8469);
				PlayerEntity playerEntity = (PlayerEntity)livingEntity;
				if (!playerEntity.inventory.insertStack(itemStack2)) {
					playerEntity.dropItem(itemStack2, false);
				}
			}

			return itemStack;
		}
	}

	@Override
	public int getMaxUseTime(ItemStack itemStack) {
		return 40;
	}

	@Override
	public UseAction getUseAction(ItemStack itemStack) {
		return UseAction.field_8946;
	}

	@Override
	public SoundEvent getDrinkSound() {
		return SoundEvents.field_20615;
	}

	@Override
	public SoundEvent getEatSound() {
		return SoundEvents.field_20615;
	}

	@Override
	public TypedActionResult<ItemStack> use(World world, PlayerEntity playerEntity, Hand hand) {
		playerEntity.setCurrentHand(hand);
		return TypedActionResult.success(playerEntity.getStackInHand(hand));
	}
}
