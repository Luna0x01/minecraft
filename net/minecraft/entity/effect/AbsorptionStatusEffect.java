package net.minecraft.entity.effect;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.AbstractEntityAttributeContainer;
import net.minecraft.util.Identifier;

public class AbsorptionStatusEffect extends StatusEffect {
	protected AbsorptionStatusEffect(int i, Identifier identifier, boolean bl, int j) {
		super(i, identifier, bl, j);
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
