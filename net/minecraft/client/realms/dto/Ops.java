package net.minecraft.client.realms.dto;

import com.google.common.collect.Sets;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.util.Set;

public class Ops extends ValueObject {
	public Set<String> ops = Sets.newHashSet();

	public static Ops parse(String json) {
		Ops ops = new Ops();
		JsonParser jsonParser = new JsonParser();

		try {
			JsonElement jsonElement = jsonParser.parse(json);
			JsonObject jsonObject = jsonElement.getAsJsonObject();
			JsonElement jsonElement2 = jsonObject.get("ops");
			if (jsonElement2.isJsonArray()) {
				for (JsonElement jsonElement3 : jsonElement2.getAsJsonArray()) {
					ops.ops.add(jsonElement3.getAsString());
				}
			}
		} catch (Exception var8) {
		}

		return ops;
	}
}
