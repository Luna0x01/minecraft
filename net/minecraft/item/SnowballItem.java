package net.minecraft.item;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.thrown.SnowballEntity;
import net.minecraft.item.itemgroup.ItemGroup;
import net.minecraft.stat.Stats;
import net.minecraft.world.World;

public class SnowballItem extends Item {
	public SnowballItem() {
		this.maxCount = 16;
		this.setItemGroup(ItemGroup.MISC);
	}

	@Override
	public ItemStack onStartUse(ItemStack stack, World world, PlayerEntity player) {
		if (!player.abilities.creativeMode) {
			stack.count--;
		}

		world.playSound((Entity)player, "random.bow", 0.5F, 0.4F / (RANDOM.nextFloat() * 0.4F + 0.8F));
		if (!world.isClient) {
			world.spawnEntity(new SnowballEntity(world, player));
		}

		player.incrementStat(Stats.USED[Item.getRawId(this)]);
		return stack;
	}
}
