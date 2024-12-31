package net.minecraft.client.realms;

import com.google.gson.Gson;
import javax.annotation.Nullable;

public class CheckedGson {
	private final Gson GSON = new Gson();

	public String toJson(RealmsSerializable serializable) {
		return this.GSON.toJson(serializable);
	}

	@Nullable
	public <T extends RealmsSerializable> T fromJson(String json, Class<T> type) {
		return (T)this.GSON.fromJson(json, type);
	}
}
