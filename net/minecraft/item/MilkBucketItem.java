package net.minecraft.item;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.itemgroup.ItemGroup;
import net.minecraft.stat.Stats;
import net.minecraft.util.UseAction;
import net.minecraft.world.World;

public class MilkBucketItem extends Item {
	public MilkBucketItem() {
		this.setMaxCount(1);
		this.setItemGroup(ItemGroup.MISC);
	}

	@Override
	public ItemStack onFinishUse(ItemStack stack, World world, PlayerEntity player) {
		if (!player.abilities.creativeMode) {
			stack.count--;
		}

		if (!world.isClient) {
			player.clearStatusEffects();
		}

		player.incrementStat(Stats.USED[Item.getRawId(this)]);
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
	public ItemStack onStartUse(ItemStack stack, World world, PlayerEntity player) {
		player.setUseItem(stack, this.getMaxUseTime(stack));
		return stack;
	}
}
