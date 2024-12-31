package net.minecraft.client.resource.language;

public class I18n {
	private static TranslationStorage storage;

	static void setTranslationStorage(TranslationStorage storage) {
		I18n.storage = storage;
	}

	public static String translate(String key, Object... args) {
		return storage.translateAndFormat(key, args);
	}

	public static boolean method_12500(String string) {
		return storage.method_12501(string);
	}
}
