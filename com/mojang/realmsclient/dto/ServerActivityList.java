package com.mojang.realmsclient.dto;

import com.google.common.collect.Lists;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mojang.realmsclient.util.JsonUtils;
import java.util.List;

public class ServerActivityList extends ValueObject {
	public long periodInMillis;
	public List<ServerActivity> serverActivities = Lists.newArrayList();

	public static ServerActivityList parse(String string) {
		ServerActivityList serverActivityList = new ServerActivityList();
		JsonParser jsonParser = new JsonParser();

		try {
			JsonElement jsonElement = jsonParser.parse(string);
			JsonObject jsonObject = jsonElement.getAsJsonObject();
			serverActivityList.periodInMillis = JsonUtils.getLongOr("periodInMillis", jsonObject, -1L);
			JsonElement jsonElement2 = jsonObject.get("playerActivityDto");
			if (jsonElement2 != null && jsonElement2.isJsonArray()) {
				for (JsonElement jsonElement3 : jsonElement2.getAsJsonArray()) {
					ServerActivity serverActivity = ServerActivity.parse(jsonElement3.getAsJsonObject());
					serverActivityList.serverActivities.add(serverActivity);
				}
			}
		} catch (Exception var10) {
		}

		return serverActivityList;
	}
}
