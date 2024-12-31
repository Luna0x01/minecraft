package net.minecraft.util;

import net.minecraft.block.material.MaterialColor;

public enum DyeColor implements StringIdentifiable {
	WHITE(0, 15, "white", "white", MaterialColor.WHITE, Formatting.WHITE),
	ORANGE(1, 14, "orange", "orange", MaterialColor.ORANGE, Formatting.GOLD),
	MAGENTA(2, 13, "magenta", "magenta", MaterialColor.MAGENTA, Formatting.AQUA),
	LIGHT_BLUE(3, 12, "light_blue", "lightBlue", MaterialColor.LIGHT_BLUE, Formatting.BLUE),
	YELLOW(4, 11, "yellow", "yellow", MaterialColor.YELLOW, Formatting.YELLOW),
	LIME(5, 10, "lime", "lime", MaterialColor.LIME, Formatting.GREEN),
	PINK(6, 9, "pink", "pink", MaterialColor.PINK, Formatting.LIGHT_PURPLE),
	GRAY(7, 8, "gray", "gray", MaterialColor.GRAY, Formatting.DARK_GRAY),
	SILVER(8, 7, "silver", "silver", MaterialColor.LIGHT_GRAY, Formatting.GRAY),
	CYAN(9, 6, "cyan", "cyan", MaterialColor.CYAN, Formatting.DARK_AQUA),
	PURPLE(10, 5, "purple", "purple", MaterialColor.PURPLE, Formatting.DARK_PURPLE),
	BLUE(11, 4, "blue", "blue", MaterialColor.BLUE, Formatting.DARK_BLUE),
	BROWN(12, 3, "brown", "brown", MaterialColor.BROWN, Formatting.GOLD),
	GREEN(13, 2, "green", "green", MaterialColor.GREEN, Formatting.DARK_GREEN),
	RED(14, 1, "red", "red", MaterialColor.RED, Formatting.DARK_RED),
	BLACK(15, 0, "black", "black", MaterialColor.BLACK, Formatting.BLACK);

	private static final DyeColor[] COLORS2 = new DyeColor[values().length];
	private static final DyeColor[] COLORS = new DyeColor[values().length];
	private final int id;
	private final int idSwapped;
	private final String name;
	private final String translationKey;
	private final MaterialColor color;
	private final Formatting formatting;

	private DyeColor(int j, int k, String string2, String string3, MaterialColor materialColor, Formatting formatting) {
		this.id = j;
		this.idSwapped = k;
		this.name = string2;
		this.translationKey = string3;
		this.color = materialColor;
		this.formatting = formatting;
	}

	public int getId() {
		return this.id;
	}

	public int getSwappedId() {
		return this.idSwapped;
	}

	public String getTranslationKey() {
		return this.translationKey;
	}

	public MaterialColor getMaterialColor() {
		return this.color;
	}

	public static DyeColor getById(int id) {
		if (id < 0 || id >= COLORS.length) {
			id = 0;
		}

		return COLORS[id];
	}

	public static DyeColor byId(int id) {
		if (id < 0 || id >= COLORS2.length) {
			id = 0;
		}

		return COLORS2[id];
	}

	public String toString() {
		return this.translationKey;
	}

	@Override
	public String asString() {
		return this.name;
	}

	static {
		for (DyeColor dyeColor : values()) {
			COLORS2[dyeColor.getId()] = dyeColor;
			COLORS[dyeColor.getSwappedId()] = dyeColor;
		}
	}
}
