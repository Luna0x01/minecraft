package net.minecraft.item;

import net.minecraft.client.sound.SoundCategory;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.passive.PigEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.sound.Sounds;
import net.minecraft.util.Hand;

public class SaddleItem extends Item {
	public SaddleItem(Item.Settings settings) {
		super(settings);
	}

	@Override
	public boolean method_3353(ItemStack itemStack, PlayerEntity playerEntity, LivingEntity livingEntity, Hand hand) {
		if (livingEntity instanceof PigEntity) {
			PigEntity pigEntity = (PigEntity)livingEntity;
			if (!pigEntity.isSaddled() && !pigEntity.isBaby()) {
				pigEntity.setSaddled(true);
				pigEntity.world.playSound(playerEntity, pigEntity.x, pigEntity.y, pigEntity.z, Sounds.ENTITY_PIG_SADDLE, SoundCategory.NEUTRAL, 0.5F, 1.0F);
				itemStack.decrement(1);
			}

			return true;
		} else {
			return false;
		}
	}
}
