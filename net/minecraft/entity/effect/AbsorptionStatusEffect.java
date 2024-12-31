package net.minecraft.entity.effect;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.AbstractEntityAttributeContainer;

public class AbsorptionStatusEffect extends StatusEffect {
	protected AbsorptionStatusEffect(boolean bl, int i) {
		super(bl, i);
	}

	@Override
	public void onRemoved(LivingEntity entity, AbstractEntityAttributeContainer attributes, int amplifier) {
		entity.setAbsorption(entity.getAbsorption() - (float)(4 * (amplifier + 1)));
		super.onRemoved(entity, attributes, amplifier);
	}

	@Override
	public void method_6091(LivingEntity entity, AbstractEntityAttributeContainer attributes, int i) {
		entity.setAbsorption(entity.getAbsorption() + (float)(4 * (i + 1)));
		super.method_6091(entity, attributes, i);
	}
}
