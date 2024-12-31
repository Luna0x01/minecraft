package net.minecraft.client.twitch;

import net.minecraft.entity.LivingEntity;

public class PlayerDeathData extends StreamMetadata {
	public PlayerDeathData(LivingEntity livingEntity, LivingEntity livingEntity2) {
		super("player_death");
		if (livingEntity != null) {
			this.put("player", livingEntity.getTranslationKey());
		}

		if (livingEntity2 != null) {
			this.put("killer", livingEntity2.getTranslationKey());
		}
	}
}
