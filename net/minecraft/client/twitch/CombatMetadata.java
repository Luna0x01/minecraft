package net.minecraft.client.twitch;

import net.minecraft.entity.LivingEntity;

public class CombatMetadata extends StreamMetadata {
	public CombatMetadata(LivingEntity livingEntity, LivingEntity livingEntity2) {
		super("player_combat");
		this.put("player", livingEntity.getTranslationKey());
		if (livingEntity2 != null) {
			this.put("primary_opponent", livingEntity2.getTranslationKey());
		}

		if (livingEntity2 != null) {
			this.setDescription("Combat between " + livingEntity.getTranslationKey() + " and " + livingEntity2.getTranslationKey());
		} else {
			this.setDescription("Combat between " + livingEntity.getTranslationKey() + " and others");
		}
	}
}
