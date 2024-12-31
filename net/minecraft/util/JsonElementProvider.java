package net.minecraft.util;

import com.google.gson.JsonElement;

public interface JsonElementProvider {
	void read(JsonElement jsonElement);

	JsonElement write();
}
