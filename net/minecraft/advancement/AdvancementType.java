package net.minecraft.advancement;

import net.minecraft.util.Formatting;

public enum AdvancementType {
	TASK("task", 0, Formatting.GREEN),
	CHALLENGE("challenge", 26, Formatting.DARK_PURPLE),
	GOAL("goal", 52, Formatting.GREEN);

	private final String type;
	private final int textureOffset;
	private final Formatting color;

	private AdvancementType(String string2, int j, Formatting formatting) {
		this.type = string2;
		this.textureOffset = j;
		this.color = formatting;
	}

	public String getType() {
		return this.type;
	}

	public int getTextureOffset() {
		return this.textureOffset;
	}

	public static AdvancementType fromString(String type) {
		for (AdvancementType advancementType : values()) {
			if (advancementType.type.equals(type)) {
				return advancementType;
			}
		}

		throw new IllegalArgumentException("Unknown frame type '" + type + "'");
	}

	public Formatting getColor() {
		return this.color;
	}
}
