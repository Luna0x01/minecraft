package net.minecraft.util;

import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Pattern;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Language {
	private static final Logger field_21486 = LogManager.getLogger();
	private static final Pattern TOKEN_PATTERN = Pattern.compile("%(\\d+\\$)?[\\d\\.]*[df]");
	private static final Language INSTANCE = new Language();
	private final Map<String, String> translations = Maps.newHashMap();
	private long timeLoaded;

	public Language() {
		try {
			InputStream inputStream = Language.class.getResourceAsStream("/assets/minecraft/lang/en_us.json");
			JsonElement jsonElement = (JsonElement)new Gson().fromJson(new InputStreamReader(inputStream, StandardCharsets.UTF_8), JsonElement.class);
			JsonObject jsonObject = JsonHelper.asObject(jsonElement, "strings");

			for (Entry<String, JsonElement> entry : jsonObject.entrySet()) {
				String string = TOKEN_PATTERN.matcher(JsonHelper.asString((JsonElement)entry.getValue(), (String)entry.getKey())).replaceAll("%$1s");
				this.translations.put(entry.getKey(), string);
			}

			this.timeLoaded = Util.method_20227();
		} catch (JsonParseException var7) {
			field_21486.error("Couldn't read strings from /assets/minecraft/lang/en_us.json", var7);
		}
	}

	public static Language getInstance() {
		return INSTANCE;
	}

	public static synchronized void load(Map<String, String> map) {
		INSTANCE.translations.clear();
		INSTANCE.translations.putAll(map);
		INSTANCE.timeLoaded = Util.method_20227();
	}

	public synchronized String translate(String key) {
		return this.translateNullSafe(key);
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
