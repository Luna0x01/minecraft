package net.minecraft.util;

public enum Rarity {
	COMMON(Formatting.WHITE, "Common"),
	UNCOMMON(Formatting.YELLOW, "Uncommon"),
	RARE(Formatting.AQUA, "Rare"),
	EPIC(Formatting.LIGHT_PURPLE, "Epic");

	public final Formatting formatting;
	public final String name;

	private Rarity(Formatting formatting, String string2) {
		this.formatting = formatting;
		this.name = string2;
	}
}
