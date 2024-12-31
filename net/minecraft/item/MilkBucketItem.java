package net.minecraft.item;

import javax.annotation.Nullable;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
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

	@Nullable
	@Override
	public ItemStack method_3367(ItemStack stack, World world, LivingEntity entity) {
		if (entity instanceof PlayerEntity && !((PlayerEntity)entity).abilities.creativeMode) {
			stack.count--;
		}

		if (!world.isClient) {
			entity.clearStatusEffects();
		}

		if (entity instanceof PlayerEntity) {
			((PlayerEntity)entity).incrementStat(Stats.used(this));
		}

		return stack.count <= 0 ? new ItemStack(Items.BUCKET) : stack;
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
}
