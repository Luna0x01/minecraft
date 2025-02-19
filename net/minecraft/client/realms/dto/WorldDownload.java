package net.minecraft.client.realms.dto;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.minecraft.client.realms.util.JsonUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class WorldDownload extends ValueObject {
	private static final Logger LOGGER = LogManager.getLogger();
	public String downloadLink;
	public String resourcePackUrl;
	public String resourcePackHash;

	public static WorldDownload parse(String json) {
		JsonParser jsonParser = new JsonParser();
		JsonObject jsonObject = jsonParser.parse(json).getAsJsonObject();
		WorldDownload worldDownload = new WorldDownload();

		try {
			worldDownload.downloadLink = JsonUtils.getStringOr("downloadLink", jsonObject, "");
			worldDownload.resourcePackUrl = JsonUtils.getStringOr("resourcePackUrl", jsonObject, "");
			worldDownload.resourcePackHash = JsonUtils.getStringOr("resourcePackHash", jsonObject, "");
		} catch (Exception var5) {
			LOGGER.error("Could not parse WorldDownload: {}", var5.getMessage());
		}

		return worldDownload;
	}
}
