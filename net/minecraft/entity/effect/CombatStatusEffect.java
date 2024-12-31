package net.minecraft.entity.effect;

import net.minecraft.entity.attribute.AttributeModifier;
import net.minecraft.util.Identifier;

public class CombatStatusEffect extends StatusEffect {
	protected CombatStatusEffect(int i, Identifier identifier, boolean bl, int j) {
		super(i, identifier, bl, j);
	}

	@Override
	public double adjustModifierAmount(int amplifier, AttributeModifier modifier) {
		return this.id == StatusEffect.WEAKNESS.id ? (double)(-0.5F * (float)(amplifier + 1)) : 1.3 * (double)(amplifier + 1);
	}
}
