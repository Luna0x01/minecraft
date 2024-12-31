package net.minecraft.util;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import javax.annotation.Nullable;

public enum Formatting {
	BLACK("BLACK", '0', 0),
	DARK_BLUE("DARK_BLUE", '1', 1),
	DARK_GREEN("DARK_GREEN", '2', 2),
	DARK_AQUA("DARK_AQUA", '3', 3),
	DARK_RED("DARK_RED", '4', 4),
	DARK_PURPLE("DARK_PURPLE", '5', 5),
	GOLD("GOLD", '6', 6),
	GRAY("GRAY", '7', 7),
	DARK_GRAY("DARK_GRAY", '8', 8),
	BLUE("BLUE", '9', 9),
	GREEN("GREEN", 'a', 10),
	AQUA("AQUA", 'b', 11),
	RED("RED", 'c', 12),
	LIGHT_PURPLE("LIGHT_PURPLE", 'd', 13),
	YELLOW("YELLOW", 'e', 14),
	WHITE("WHITE", 'f', 15),
	OBFUSCATED("OBFUSCATED", 'k', true),
	BOLD("BOLD", 'l', true),
	STRIKETHROUGH("STRIKETHROUGH", 'm', true),
	UNDERLINE("UNDERLINE", 'n', true),
	ITALIC("ITALIC", 'o', true),
	RESET("RESET", 'r', -1);

	private static final Map<String, Formatting> BY_NAME = Maps.newHashMap();
	private static final Pattern FORMATTING_CODE_PATTERN = Pattern.compile("(?i)" + String.valueOf('§') + "[0-9A-FK-OR]");
	private final String name;
	private final char code;
	private final boolean modifier;
	private final String stringValue;
	private final int colorIndex;

	private static String sanitize(String name) {
		return name.toLowerCase().replaceAll("[^a-z]", "");
	}

	private Formatting(String string2, char c, int j) {
		this(string2, c, false, j);
	}

	private Formatting(String string2, char c, boolean bl) {
		this(string2, c, bl, -1);
	}

	private Formatting(String string2, char c, boolean bl, int j) {
		this.name = string2;
		this.code = c;
		this.modifier = bl;
		this.colorIndex = j;
		this.stringValue = "§" + c;
	}

	public int getColorIndex() {
		return this.colorIndex;
	}

	public boolean isModifier() {
		return this.modifier;
	}

	public boolean isColor() {
		return !this.modifier && this != RESET;
	}

	public String getName() {
		return this.name().toLowerCase();
	}

	public String toString() {
		return this.stringValue;
	}

	@Nullable
	public static String strip(@Nullable String string) {
		return string == null ? null : FORMATTING_CODE_PATTERN.matcher(string).replaceAll("");
	}

	@Nullable
	public static Formatting byName(@Nullable String name) {
		return name == null ? null : (Formatting)BY_NAME.get(sanitize(name));
	}

	@Nullable
	public static Formatting byColorIndex(int colorIndex) {
		if (colorIndex < 0) {
			return RESET;
		} else {
			for (Formatting formatting : values()) {
				if (formatting.getColorIndex() == colorIndex) {
					return formatting;
				}
			}

			return null;
		}
	}

	public static Collection<String> getNames(boolean colors, boolean modifiers) {
		List<String> list = Lists.newArrayList();

		for (Formatting formatting : values()) {
			if ((!formatting.isColor() || colors) && (!formatting.isModifier() || modifiers)) {
				list.add(formatting.getName());
			}
		}

		return list;
	}

	static {
		for (Formatting formatting : values()) {
			BY_NAME.put(sanitize(formatting.name), formatting);
		}
	}
}
