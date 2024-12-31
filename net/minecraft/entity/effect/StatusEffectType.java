package net.minecraft.entity.effect;

import net.minecraft.util.Formatting;

public enum StatusEffectType {
	field_18271(Formatting.field_1078),
	field_18272(Formatting.field_1061),
	field_18273(Formatting.field_1078);

	private final Formatting formatting;

	private StatusEffectType(Formatting formatting) {
		this.formatting = formatting;
	}

	public Formatting getFormatting() {
		return this.formatting;
	}
}
