package net.minecraft.entity.effect;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.AbstractEntityAttributeContainer;

public class HealthBoostStatusEffect extends StatusEffect {
	public HealthBoostStatusEffect(boolean bl, int i) {
		super(bl, i);
	}

	@Override
	public void onRemoved(LivingEntity entity, AbstractEntityAttributeContainer attributes, int amplifier) {
		super.onRemoved(entity, attributes, amplifier);
		if (entity.getHealth() > entity.getMaxHealth()) {
			entity.setHealth(entity.getMaxHealth());
		}
	}
}
