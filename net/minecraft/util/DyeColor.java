package net.minecraft.util;

public enum DyeColor implements StringIdentifiable {
	WHITE(0, 15, "white", "white", 16383998, Formatting.WHITE),
	ORANGE(1, 14, "orange", "orange", 16351261, Formatting.GOLD),
	MAGENTA(2, 13, "magenta", "magenta", 13061821, Formatting.AQUA),
	LIGHT_BLUE(3, 12, "light_blue", "lightBlue", 3847130, Formatting.BLUE),
	YELLOW(4, 11, "yellow", "yellow", 16701501, Formatting.YELLOW),
	LIME(5, 10, "lime", "lime", 8439583, Formatting.GREEN),
	PINK(6, 9, "pink", "pink", 15961002, Formatting.LIGHT_PURPLE),
	GRAY(7, 8, "gray", "gray", 4673362, Formatting.DARK_GRAY),
	SILVER(8, 7, "silver", "silver", 10329495, Formatting.GRAY),
	CYAN(9, 6, "cyan", "cyan", 1481884, Formatting.DARK_AQUA),
	PURPLE(10, 5, "purple", "purple", 8991416, Formatting.DARK_PURPLE),
	BLUE(11, 4, "blue", "blue", 3949738, Formatting.DARK_BLUE),
	BROWN(12, 3, "brown", "brown", 8606770, Formatting.GOLD),
	GREEN(13, 2, "green", "green", 6192150, Formatting.DARK_GREEN),
	RED(14, 1, "red", "red", 11546150, Formatting.DARK_RED),
	BLACK(15, 0, "black", "black", 1908001, Formatting.BLACK);

	private static final DyeColor[] COLORS2 = new DyeColor[values().length];
	private static final DyeColor[] COLORS = new DyeColor[values().length];
	private final int id;
	private final int idSwapped;
	private final String name;
	private final String translationKey;
	private final int rawColor;
	private final float[] colorComponents;
	private final Formatting formatting;

	private DyeColor(int j, int k, String string2, String string3, int l, Formatting formatting) {
		this.id = j;
		this.idSwapped = k;
		this.name = string2;
		this.translationKey = string3;
		this.rawColor = l;
		this.formatting = formatting;
		int m = (l & 0xFF0000) >> 16;
		int n = (l & 0xFF00) >> 8;
		int o = (l & 0xFF) >> 0;
		this.colorComponents = new float[]{(float)m / 255.0F, (float)n / 255.0F, (float)o / 255.0F};
	}

	public int getId() {
		return this.id;
	}

	public int getSwappedId() {
		return this.idSwapped;
	}

	public String getName() {
		return this.name;
	}

	public String getTranslationKey() {
		return this.translationKey;
	}

	public int method_14222() {
		return this.rawColor;
	}

	public float[] getColorComponents() {
		return this.colorComponents;
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
