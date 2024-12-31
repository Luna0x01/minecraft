package net.minecraft.item;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.itemgroup.ItemGroup;
import net.minecraft.util.Hand;

public class NameTagItem extends Item {
	public NameTagItem() {
		this.setItemGroup(ItemGroup.TOOLS);
	}

	@Override
	public boolean method_3353(ItemStack itemStack, PlayerEntity playerEntity, LivingEntity livingEntity, Hand hand) {
		if (!itemStack.hasCustomName()) {
			return false;
		} else if (livingEntity instanceof MobEntity) {
			MobEntity mobEntity = (MobEntity)livingEntity;
			mobEntity.setCustomName(itemStack.getCustomName());
			mobEntity.setPersistent();
			itemStack.count--;
			return true;
		} else {
			return super.method_3353(itemStack, playerEntity, livingEntity, hand);
		}
	}
}
