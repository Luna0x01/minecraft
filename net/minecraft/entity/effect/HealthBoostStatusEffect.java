package net.minecraft.entity.effect;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.AbstractEntityAttributeContainer;
import net.minecraft.util.Identifier;

public class HealthBoostStatusEffect extends StatusEffect {
	public HealthBoostStatusEffect(int i, Identifier identifier, boolean bl, int j) {
		super(i, identifier, bl, j);
	}

	@Override
	public void onRemoved(LivingEntity entity, AbstractEntityAttributeContainer attributes, int amplifier) {
		super.onRemoved(entity, attributes, amplifier);
		if (entity.getHealth() > entity.getMaxHealth()) {
			entity.setHealth(entity.getMaxHealth());
		}
	}
}
