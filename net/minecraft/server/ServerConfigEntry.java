package net.minecraft.server;

import com.google.gson.JsonObject;

public class ServerConfigEntry<T> {
	private final T object;

	public ServerConfigEntry(T object) {
		this.object = object;
	}

	protected ServerConfigEntry(T object, JsonObject jsonObject) {
		this.object = object;
	}

	T getKey() {
		return this.object;
	}

	boolean isInvalid() {
		return false;
	}

	protected void serialize(JsonObject jsonObject) {
	}
}
