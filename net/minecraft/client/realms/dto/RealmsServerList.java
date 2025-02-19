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

public class RealmsServerList extends ValueObject {
	private static final Logger LOGGER = LogManager.getLogger();
	public List<RealmsServer> servers;

	public static RealmsServerList parse(String json) {
		RealmsServerList realmsServerList = new RealmsServerList();
		realmsServerList.servers = Lists.newArrayList();

		try {
			JsonParser jsonParser = new JsonParser();
			JsonObject jsonObject = jsonParser.parse(json).getAsJsonObject();
			if (jsonObject.get("servers").isJsonArray()) {
				JsonArray jsonArray = jsonObject.get("servers").getAsJsonArray();
				Iterator<JsonElement> iterator = jsonArray.iterator();

				while (iterator.hasNext()) {
					realmsServerList.servers.add(RealmsServer.parse(((JsonElement)iterator.next()).getAsJsonObject()));
				}
			}
		} catch (Exception var6) {
			LOGGER.error("Could not parse McoServerList: {}", var6.getMessage());
		}

		return realmsServerList;
	}
}
