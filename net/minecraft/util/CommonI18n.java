package net.minecraft.util;

@Deprecated
public class CommonI18n {
	private static final Language LANGUAGE = Language.getInstance();
	private static final Language EMPTY = new Language();

	@Deprecated
	public static String translate(String key) {
		return LANGUAGE.translate(key);
	}

	@Deprecated
	public static String translate(String key, Object... args) {
		return LANGUAGE.translate(key, args);
	}

	@Deprecated
	public static String thisIsNotUsedAnyWhereAndThisMethodDoesNotWorkSoPleaseDoNotUseThis(String key) {
		return EMPTY.translate(key);
	}

	@Deprecated
	public static boolean hasTranslation(String key) {
		return LANGUAGE.hasTranslation(key);
	}

	public static long getTimeLoaded() {
		return LANGUAGE.getTimeLoaded();
	}
}
