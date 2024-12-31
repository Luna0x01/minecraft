package net.minecraft.client.resource;

import com.google.common.base.Charsets;
import com.google.common.collect.Maps;
import com.google.common.io.Files;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.Map;
import java.util.Map.Entry;
import net.minecraft.util.JsonHelper;
import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class AssetsIndex {
	private static final Logger LOGGER = LogManager.getLogger();
	private final Map<String, File> index = Maps.newHashMap();

	public AssetsIndex(File file, String string) {
		if (string != null) {
			File file2 = new File(file, "objects");
			File file3 = new File(file, "indexes/" + string + ".json");
			BufferedReader bufferedReader = null;

			try {
				bufferedReader = Files.newReader(file3, Charsets.UTF_8);
				JsonObject jsonObject = new JsonParser().parse(bufferedReader).getAsJsonObject();
				JsonObject jsonObject2 = JsonHelper.getObject(jsonObject, "objects", null);
				if (jsonObject2 != null) {
					for (Entry<String, JsonElement> entry : jsonObject2.entrySet()) {
						JsonObject jsonObject3 = (JsonObject)entry.getValue();
						String string2 = (String)entry.getKey();
						String[] strings = string2.split("/", 2);
						String string3 = strings.length == 1 ? strings[0] : strings[0] + ":" + strings[1];
						String string4 = JsonHelper.getString(jsonObject3, "hash");
						File file4 = new File(file2, string4.substring(0, 2) + "/" + string4);
						this.index.put(string3, file4);
					}
				}
			} catch (JsonParseException var20) {
				LOGGER.error("Unable to parse resource index file: " + file3);
			} catch (FileNotFoundException var21) {
				LOGGER.error("Can't find the resource index file: " + file3);
			} finally {
				IOUtils.closeQuietly(bufferedReader);
			}
		}
	}

	public Map<String, File> getIndex() {
		return this.index;
	}
}
