package net.minecraft.item;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.itemgroup.ItemGroup;

public class NameTagItem extends Item {
	public NameTagItem() {
		this.setItemGroup(ItemGroup.TOOLS);
	}

	@Override
	public boolean canUseOnEntity(ItemStack stack, PlayerEntity player, LivingEntity entity) {
		if (!stack.hasCustomName()) {
			return false;
		} else if (entity instanceof MobEntity) {
			MobEntity mobEntity = (MobEntity)entity;
			mobEntity.setCustomName(stack.getCustomName());
			mobEntity.setPersistent();
			stack.count--;
			return true;
		} else {
			return super.canUseOnEntity(stack, player, entity);
		}
	}
}
