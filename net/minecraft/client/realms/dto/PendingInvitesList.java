package net.minecraft.client.realms.dto;

import com.google.common.collect.Lists;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.util.Iterator;
import java.util.List;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class PendingInvitesList extends ValueObject {
	private static final Logger LOGGER = LogManager.getLogger();
	public List<PendingInvite> pendingInvites = Lists.newArrayList();

	public static PendingInvitesList parse(String json) {
		PendingInvitesList pendingInvitesList = new PendingInvitesList();

		try {
			JsonParser jsonParser = new JsonParser();
			JsonObject jsonObject = jsonParser.parse(json).getAsJsonObject();
			if (jsonObject.get("invites").isJsonArray()) {
				Iterator<JsonElement> iterator = jsonObject.get("invites").getAsJsonArray().iterator();

				while (iterator.hasNext()) {
					pendingInvitesList.pendingInvites.add(PendingInvite.parse(((JsonElement)iterator.next()).getAsJsonObject()));
				}
			}
		} catch (Exception var5) {
			LOGGER.error("Could not parse PendingInvitesList: {}", var5.getMessage());
		}

		return pendingInvitesList;
	}
}
