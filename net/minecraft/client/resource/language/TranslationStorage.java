package net.minecraft.client.resource.language;

import com.google.common.base.Splitter;
import com.google.common.collect.Iterables;
import com.google.common.collect.Maps;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.IllegalFormatException;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import net.minecraft.resource.Resource;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;
import org.apache.commons.io.IOUtils;

public class TranslationStorage {
	private static final Splitter TOKEN_SPLITTER = Splitter.on('=').limit(2);
	private static final Pattern TOKEN_PATTERN = Pattern.compile("%(\\d+\\$)?[\\d\\.]*[df]");
	Map<String, String> translations = Maps.newHashMap();
	private boolean rightToLeft;

	public synchronized void load(ResourceManager resourceManager, List<String> languages) {
		this.translations.clear();

		for (String string : languages) {
			String string2 = String.format("lang/%s.lang", string);

			for (String string3 : resourceManager.getAllNamespaces()) {
				try {
					this.load(resourceManager.getAllResources(new Identifier(string3, string2)));
				} catch (IOException var9) {
				}
			}
		}

		this.setRightToLeft();
	}

	public boolean isRightToLeft() {
		return this.rightToLeft;
	}

	private void setRightToLeft() {
		this.rightToLeft = false;
		int i = 0;
		int j = 0;

		for (String string : this.translations.values()) {
			int k = string.length();
			j += k;

			for (int l = 0; l < k; l++) {
				if (string.charAt(l) >= 256) {
					i++;
				}
			}
		}

		float f = (float)i / (float)j;
		this.rightToLeft = (double)f > 0.1;
	}

	private void load(List<Resource> resources) throws IOException {
		for (Resource resource : resources) {
			InputStream inputStream = resource.getInputStream();

			try {
				this.load(inputStream);
			} finally {
				IOUtils.closeQuietly(inputStream);
			}
		}
	}

	private void load(InputStream stream) throws IOException {
		for (String string : IOUtils.readLines(stream, StandardCharsets.UTF_8)) {
			if (!string.isEmpty() && string.charAt(0) != '#') {
				String[] strings = (String[])Iterables.toArray(TOKEN_SPLITTER.split(string), String.class);
				if (strings != null && strings.length == 2) {
					String string2 = strings[0];
					String string3 = TOKEN_PATTERN.matcher(strings[1]).replaceAll("%$1s");
					this.translations.put(string2, string3);
				}
			}
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
