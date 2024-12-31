package net.minecraft.item;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.passive.PigEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.itemgroup.ItemGroup;

public class SaddleItem extends Item {
	public SaddleItem() {
		this.maxCount = 1;
		this.setItemGroup(ItemGroup.TRANSPORTATION);
	}

	@Override
	public boolean canUseOnEntity(ItemStack stack, PlayerEntity player, LivingEntity entity) {
		if (entity instanceof PigEntity) {
			PigEntity pigEntity = (PigEntity)entity;
			if (!pigEntity.isSaddled() && !pigEntity.isBaby()) {
				pigEntity.setSaddled(true);
				pigEntity.world.playSound(pigEntity, "mob.horse.leather", 0.5F, 1.0F);
				stack.count--;
			}

			return true;
		} else {
			return false;
		}
	}

	@Override
	public boolean onEntityHit(ItemStack stack, LivingEntity entity1, LivingEntity entity2) {
		this.canUseOnEntity(stack, null, entity1);
		return true;
	}
}
