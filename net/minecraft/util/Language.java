package net.minecraft.util;

import com.google.common.base.Splitter;
import com.google.common.collect.Iterables;
import com.google.common.collect.Maps;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.IllegalFormatException;
import java.util.Map;
import java.util.regex.Pattern;
import org.apache.commons.io.IOUtils;

public class Language {
	private static final Pattern TOKEN_PATTERN = Pattern.compile("%(\\d+\\$)?[\\d\\.]*[df]");
	private static final Splitter SPLITTER = Splitter.on('=').limit(2);
	private static final Language INSTANCE = new Language();
	private final Map<String, String> translations = Maps.newHashMap();
	private long timeLoaded;

	public Language() {
		try {
			InputStream inputStream = Language.class.getResourceAsStream("/assets/minecraft/lang/en_us.lang");

			for (String string : IOUtils.readLines(inputStream, StandardCharsets.UTF_8)) {
				if (!string.isEmpty() && string.charAt(0) != '#') {
					String[] strings = (String[])Iterables.toArray(SPLITTER.split(string), String.class);
					if (strings != null && strings.length == 2) {
						String string2 = strings[0];
						String string3 = TOKEN_PATTERN.matcher(strings[1]).replaceAll("%$1s");
						this.translations.put(string2, string3);
					}
				}
			}

			this.timeLoaded = System.currentTimeMillis();
		} catch (IOException var7) {
		}
	}

	static Language getInstance() {
		return INSTANCE;
	}

	public static synchronized void load(Map<String, String> map) {
		INSTANCE.translations.clear();
		INSTANCE.translations.putAll(map);
		INSTANCE.timeLoaded = System.currentTimeMillis();
	}

	public synchronized String translate(String key) {
		return this.translateNullSafe(key);
	}

	public synchronized String translate(String key, Object... args) {
		String string = this.translateNullSafe(key);

		try {
			return String.format(string, args);
		} catch (IllegalFormatException var5) {
			return "Format error: " + string;
		}
	}

	private String translateNullSafe(String key) {
		String string = (String)this.translations.get(key);
		return string == null ? key : string;
	}

	public synchronized boolean hasTranslation(String translation) {
		return this.translations.containsKey(translation);
	}

	public long getTimeLoaded() {
		return this.timeLoaded;
	}
}
