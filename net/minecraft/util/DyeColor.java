package net.minecraft.util;

import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Map;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import net.minecraft.block.material.MaterialColor;

public enum DyeColor implements StringIdentifiable {
	WHITE(0, "white", 16383998, MaterialColor.WHITE, 15790320),
	ORANGE(1, "orange", 16351261, MaterialColor.ORANGE, 15435844),
	MAGENTA(2, "magenta", 13061821, MaterialColor.MAGENTA, 12801229),
	LIGHT_BLUE(3, "light_blue", 3847130, MaterialColor.field_19529, 6719955),
	YELLOW(4, "yellow", 16701501, MaterialColor.YELLOW, 14602026),
	LIME(5, "lime", 8439583, MaterialColor.field_19530, 4312372),
	PINK(6, "pink", 15961002, MaterialColor.field_19531, 14188952),
	GRAY(7, "gray", 4673362, MaterialColor.field_19532, 4408131),
	LIGHT_GRAY(8, "light_gray", 10329495, MaterialColor.field_19533, 11250603),
	CYAN(9, "cyan", 1481884, MaterialColor.field_19534, 2651799),
	PURPLE(10, "purple", 8991416, MaterialColor.PURPLE, 8073150),
	BLUE(11, "blue", 3949738, MaterialColor.field_19510, 2437522),
	BROWN(12, "brown", 8606770, MaterialColor.BROWN, 5320730),
	GREEN(13, "green", 6192150, MaterialColor.GREEN, 3887386),
	RED(14, "red", 11546150, MaterialColor.RED, 11743532),
	BLACK(15, "black", 1908001, MaterialColor.BLACK, 1973019);

	private static final DyeColor[] DYE_COLORS = (DyeColor[])Arrays.stream(values()).sorted(Comparator.comparingInt(DyeColor::getId)).toArray(DyeColor[]::new);
	private static final Int2ObjectOpenHashMap<DyeColor> DyeColorToFireworkColor = new Int2ObjectOpenHashMap(
		(Map)Arrays.stream(values()).collect(Collectors.toMap(dyeColor -> dyeColor.idSwapped, dyeColor -> dyeColor))
	);
	private final int id;
	private final String translationKey;
	private final MaterialColor materialColor;
	private final int rawColor;
	private final int field_17166;
	private final float[] colorComponents;
	private final int idSwapped;

	private DyeColor(int j, String string2, int k, MaterialColor materialColor, int l) {
		this.id = j;
		this.translationKey = string2;
		this.rawColor = k;
		this.materialColor = materialColor;
		int m = (k & 0xFF0000) >> 16;
		int n = (k & 0xFF00) >> 8;
		int o = (k & 0xFF) >> 0;
		this.field_17166 = o << 16 | n << 8 | m << 0;
		this.colorComponents = new float[]{(float)m / 255.0F, (float)n / 255.0F, (float)o / 255.0F};
		this.idSwapped = l;
	}

	public int getId() {
		return this.id;
	}

	public String getTranslationKey() {
		return this.translationKey;
	}

	public int method_14222() {
		return this.field_17166;
	}

	public float[] getColorComponents() {
		return this.colorComponents;
	}

	public MaterialColor getColorOfMaterial() {
		return this.materialColor;
	}

	public int getSwappedId() {
		return this.idSwapped;
	}

	public static DyeColor byId(int id) {
		if (id < 0 || id >= DYE_COLORS.length) {
			id = 0;
		}

		return DYE_COLORS[id];
	}

	public static DyeColor getByTranslationKey(String translationKey) {
		for (DyeColor dyeColor : values()) {
			if (dyeColor.translationKey.equals(translationKey)) {
				return dyeColor;
			}
		}

		return WHITE;
	}

	@Nullable
	public static DyeColor getByFireworkColor(int rawId) {
		return (DyeColor)DyeColorToFireworkColor.get(rawId);
	}

	public String toString() {
		return this.translationKey;
	}

	@Override
	public String asString() {
		return this.translationKey;
	}
}
