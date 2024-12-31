package net.minecraft.entity.effect;

import net.minecraft.entity.attribute.EntityAttributeModifier;

public class DamageModifierStatusEffect extends StatusEffect {
	protected final double modifier;

	protected DamageModifierStatusEffect(StatusEffectType statusEffectType, int i, double d) {
		super(statusEffectType, i);
		this.modifier = d;
	}

	@Override
	public double adjustModifierAmount(int i, EntityAttributeModifier entityAttributeModifier) {
		return this.modifier * (double)(i + 1);
	}
}
