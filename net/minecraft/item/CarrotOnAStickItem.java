package net.minecraft.item;

import net.minecraft.entity.passive.PigEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.itemgroup.ItemGroup;
import net.minecraft.stat.Stats;
import net.minecraft.world.World;

public class CarrotOnAStickItem extends Item {
	public CarrotOnAStickItem() {
		this.setItemGroup(ItemGroup.TRANSPORTATION);
		this.setMaxCount(1);
		this.setMaxDamage(25);
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
		if (player.hasVehicle() && player.vehicle instanceof PigEntity) {
			PigEntity pigEntity = (PigEntity)player.vehicle;
			if (pigEntity.getPlayerControlGoal().method_4495() && stack.getMaxDamage() - stack.getData() >= 7) {
				pigEntity.getPlayerControlGoal().method_4494();
				stack.damage(7, player);
				if (stack.count == 0) {
					ItemStack itemStack = new ItemStack(Items.FISHING_ROD);
					itemStack.setNbt(stack.getNbt());
					return itemStack;
				}
			}
		}

		player.incrementStat(Stats.USED[Item.getRawId(this)]);
		return stack;
	}
}
