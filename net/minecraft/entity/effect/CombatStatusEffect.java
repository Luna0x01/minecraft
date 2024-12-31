package net.minecraft.entity.effect;

import net.minecraft.entity.attribute.AttributeModifier;

public class CombatStatusEffect extends StatusEffect {
	protected final double field_14447;

	protected CombatStatusEffect(boolean bl, int i, double d) {
		super(bl, i);
		this.field_14447 = d;
	}

	@Override
	public double adjustModifierAmount(int amplifier, AttributeModifier modifier) {
		return this.field_14447 * (double)(amplifier + 1);
	}
}
