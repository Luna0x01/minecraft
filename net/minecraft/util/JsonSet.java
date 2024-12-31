package net.minecraft.util;

import com.google.common.collect.ForwardingSet;
import com.google.common.collect.Sets;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import java.util.Set;

public class JsonSet extends ForwardingSet<String> implements JsonElementProvider {
	private final Set<String> set = Sets.newHashSet();

	@Override
	public void read(JsonElement jsonElement) {
		if (jsonElement.isJsonArray()) {
			for (JsonElement jsonElement2 : jsonElement.getAsJsonArray()) {
				this.add(jsonElement2.getAsString());
			}
		}
	}

	@Override
	public JsonElement write() {
		JsonArray jsonArray = new JsonArray();

		for (String string : this) {
			jsonArray.add(new JsonPrimitive(string));
		}

		return jsonArray;
	}

	protected Set<String> delegate() {
		return this.set;
	}
}
