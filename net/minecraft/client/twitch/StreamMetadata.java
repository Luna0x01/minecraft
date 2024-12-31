package net.minecraft.client.twitch;

import com.google.common.base.Objects;
import com.google.common.collect.Maps;
import com.google.gson.Gson;
import java.util.Map;

public class StreamMetadata {
	private static final Gson GSON = new Gson();
	private final String name;
	private String desc;
	private Map<String, String> map;

	public StreamMetadata(String string, String string2) {
		this.name = string;
		this.desc = string2;
	}

	public StreamMetadata(String string) {
		this(string, null);
	}

	public void setDescription(String desc) {
		this.desc = desc;
	}

	public String getDescription() {
		return this.desc == null ? this.name : this.desc;
	}

	public void put(String key, String value) {
		if (this.map == null) {
			this.map = Maps.newHashMap();
		}

		if (this.map.size() > 50) {
			throw new IllegalArgumentException("Metadata payload is full, cannot add more to it!");
		} else if (key == null) {
			throw new IllegalArgumentException("Metadata payload key cannot be null!");
		} else if (key.length() > 255) {
			throw new IllegalArgumentException("Metadata payload key is too long!");
		} else if (value == null) {
			throw new IllegalArgumentException("Metadata payload value cannot be null!");
		} else if (value.length() > 255) {
			throw new IllegalArgumentException("Metadata payload value is too long!");
		} else {
			this.map.put(key, value);
		}
	}

	public String toJson() {
		return this.map != null && !this.map.isEmpty() ? GSON.toJson(this.map) : null;
	}

	public String getName() {
		return this.name;
	}

	public String toString() {
		return Objects.toStringHelper(this).add("name", this.name).add("description", this.desc).add("data", this.toJson()).toString();
	}
}
