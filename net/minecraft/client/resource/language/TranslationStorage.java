package net.minecraft.client.resource.language;

import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.IllegalFormatException;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Pattern;
import net.minecraft.resource.Resource;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class TranslationStorage {
	private static final Gson field_21046 = new Gson();
	private static final Logger field_21047 = LogManager.getLogger();
	private static final Pattern TOKEN_PATTERN = Pattern.compile("%(\\d+\\$)?[\\d\\.]*[df]");
	Map<String, String> translations = Maps.newHashMap();

	public synchronized void method_19557(ResourceManager resourceManager, List<String> list) {
		this.translations.clear();

		for (String string : list) {
			String string2 = String.format("lang/%s.json", string);

			for (String string3 : resourceManager.getAllNamespaces()) {
				try {
					Identifier identifier = new Identifier(string3, string2);
					this.load(resourceManager.getAllResources(identifier));
				} catch (FileNotFoundException var9) {
				} catch (Exception var10) {
					field_21047.warn("Skipped language file: {}:{} ({})", string3, string2, var10.toString());
				}
			}
		}
	}

	private void load(List<Resource> resources) {
		for (Resource resource : resources) {
			InputStream inputStream = resource.getInputStream();

			try {
				this.load(inputStream);
			} finally {
				IOUtils.closeQuietly(inputStream);
			}
		}
	}

	private void load(InputStream stream) {
		JsonElement jsonElement = (JsonElement)field_21046.fromJson(new InputStreamReader(stream, StandardCharsets.UTF_8), JsonElement.class);
		JsonObject jsonObject = JsonHelper.asObject(jsonElement, "strings");

		for (Entry<String, JsonElement> entry : jsonObject.entrySet()) {
			String string = TOKEN_PATTERN.matcher(JsonHelper.asString((JsonElement)entry.getValue(), (String)entry.getKey())).replaceAll("%$1s");
			this.translations.put(entry.getKey(), string);
		}
	}

	private String translate(String key) {
		String string = (String)this.translations.get(key);
		return string == null ? key : string;
	}

	public String translateAndFormat(String key, Object[] args) {
		String string = this.translate(key);

		try {
			return String.format(string, args);
		} catch (IllegalFormatException var5) {
			return "Format error: " + string;
		}
	}

	public boolean method_12501(String string) {
		return this.translations.containsKey(string);
	}
}
