package net.minecraft.util;

import java.util.regex.Pattern;
import org.apache.commons.lang3.StringUtils;

public class ChatUtil {
	private static final Pattern FORMATTING_PATTERN = Pattern.compile("(?i)\\u00A7[0-9A-FK-OR]");

	public static String ticksToString(int ticks) {
		int i = ticks / 20;
		int j = i / 60;
		i %= 60;
		return i < 10 ? j + ":0" + i : j + ":" + i;
	}

	public static String stripTextFormat(String text) {
		return FORMATTING_PATTERN.matcher(text).replaceAll("");
	}

	public static boolean isEmpty(String string) {
		return StringUtils.isEmpty(string);
	}
}
