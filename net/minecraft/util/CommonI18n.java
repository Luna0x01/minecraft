package net.minecraft.util;

public class CommonI18n {
	private static Language LANGUAGE = Language.getInstance();
	private static Language EMPTY = new Language();

	public static String translate(String key) {
		return LANGUAGE.translate(key);
	}

	public static String translate(String key, Object... args) {
		return LANGUAGE.translate(key, args);
	}

	public static String thisIsNotUsedAnyWhereAndThisMethodDoesNotWorkSoPleaseDoNotUseThis(String key) {
		return EMPTY.translate(key);
	}

	public static boolean hasTranslation(String key) {
		return LANGUAGE.hasTranslation(key);
	}

	public static long getTimeLoaded() {
		return LANGUAGE.getTimeLoaded();
	}
}
