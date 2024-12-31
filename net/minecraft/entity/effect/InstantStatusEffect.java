package net.minecraft.entity.effect;

import net.minecraft.util.Identifier;

public class InstantStatusEffect extends StatusEffect {
	public InstantStatusEffect(int i, Identifier identifier, boolean bl, int j) {
		super(i, identifier, bl, j);
	}

	@Override
	public boolean isInstant() {
		return true;
	}

	@Override
	public boolean canApplyUpdateEffect(int duration, int amplifier) {
		return duration >= 1;
	}
}
