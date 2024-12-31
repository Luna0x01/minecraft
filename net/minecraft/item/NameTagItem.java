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
		if (itemStack.hasCustomName() && !(livingEntity instanceof PlayerEntity)) {
			livingEntity.setCustomName(itemStack.getCustomName());
			if (livingEntity instanceof MobEntity) {
				((MobEntity)livingEntity).setPersistent();
			}

			itemStack.decrement(1);
			return true;
		} else {
			return false;
		}
	}
}
