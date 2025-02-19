package net.minecraft.client.realms.dto;

import com.google.common.collect.Lists;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.util.Iterator;
import java.util.List;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class RealmsServerPlayerLists extends ValueObject {
	private static final Logger LOGGER = LogManager.getLogger();
	public List<RealmsServerPlayerList> servers;

	public static RealmsServerPlayerLists parse(String json) {
		RealmsServerPlayerLists realmsServerPlayerLists = new RealmsServerPlayerLists();
		realmsServerPlayerLists.servers = Lists.newArrayList();

		try {
			JsonParser jsonParser = new JsonParser();
			JsonObject jsonObject = jsonParser.parse(json).getAsJsonObject();
			if (jsonObject.get("lists").isJsonArray()) {
				JsonArray jsonArray = jsonObject.get("lists").getAsJsonArray();
				Iterator<JsonElement> iterator = jsonArray.iterator();

				while (iterator.hasNext()) {
					realmsServerPlayerLists.servers.add(RealmsServerPlayerList.parse(((JsonElement)iterator.next()).getAsJsonObject()));
				}
			}
		} catch (Exception var6) {
			LOGGER.error("Could not parse RealmsServerPlayerLists: {}", var6.getMessage());
		}

		return realmsServerPlayerLists;
	}
}
