package net.minecraft.item;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.FishingBobberEntity;
import net.minecraft.item.itemgroup.ItemGroup;
import net.minecraft.stat.Stats;
import net.minecraft.world.World;

public class FishingRodItem extends Item {
	public FishingRodItem() {
		this.setMaxDamage(64);
		this.setMaxCount(1);
		this.setItemGroup(ItemGroup.TOOLS);
	}

	@Override
	public boolean isHandheld() {
		return true;
	}

	@Override
	public boolean shouldRotate() {
		return true;
	}

	@Override
	public ItemStack onStartUse(ItemStack stack, World world, PlayerEntity player) {
		if (player.fishHook != null) {
			int i = player.fishHook.retract();
			stack.damage(i, player);
			player.swingHand();
		} else {
			world.playSound((Entity)player, "random.bow", 0.5F, 0.4F / (RANDOM.nextFloat() * 0.4F + 0.8F));
			if (!world.isClient) {
				world.spawnEntity(new FishingBobberEntity(world, player));
			}

			player.swingHand();
			player.incrementStat(Stats.USED[Item.getRawId(this)]);
		}

		return stack;
	}

	@Override
	public boolean isEnchantable(ItemStack stack) {
		return super.isEnchantable(stack);
	}

	@Override
	public int getEnchantability() {
		return 1;
	}
}
