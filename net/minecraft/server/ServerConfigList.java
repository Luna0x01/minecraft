package net.minecraft.server;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.io.Files;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ServerConfigList<K, V extends ServerConfigEntry<K>> {
	protected static final Logger LOGGER = LogManager.getLogger();
	protected final Gson GSON;
	private final File file;
	private final Map<String, V> map = Maps.newHashMap();
	private boolean enabled = true;
	private static final ParameterizedType field_9019 = new ParameterizedType() {
		public Type[] getActualTypeArguments() {
			return new Type[]{ServerConfigEntry.class};
		}

		public Type getRawType() {
			return List.class;
		}

		public Type getOwnerType() {
			return null;
		}
	};

	public ServerConfigList(File file) {
		this.file = file;
		GsonBuilder gsonBuilder = new GsonBuilder().setPrettyPrinting();
		gsonBuilder.registerTypeHierarchyAdapter(ServerConfigEntry.class, new ServerConfigList.DeSerializer());
		this.GSON = gsonBuilder.create();
	}

	public boolean isEnabled() {
		return this.enabled;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	public void add(V configEntry) {
		this.map.put(this.toString(configEntry.getKey()), configEntry);

		try {
			this.save();
		} catch (IOException var3) {
			LOGGER.warn("Could not save the list after adding a user.", var3);
		}
	}

	public V get(K object) {
		this.removeInvalidEntries();
		return (V)this.map.get(this.toString(object));
	}

	public void remove(K object) {
		this.map.remove(this.toString(object));

		try {
			this.save();
		} catch (IOException var3) {
			LOGGER.warn("Could not save the list after removing a user.", var3);
		}
	}

	public String[] getNames() {
		return (String[])this.map.keySet().toArray(new String[this.map.size()]);
	}

	protected String toString(K profile) {
		return profile.toString();
	}

	protected boolean contains(K object) {
		return this.map.containsKey(this.toString(object));
	}

	private void removeInvalidEntries() {
		List<K> list = Lists.newArrayList();

		for (V serverConfigEntry : this.map.values()) {
			if (serverConfigEntry.isInvalid()) {
				list.add(serverConfigEntry.getKey());
			}
		}

		for (K object : list) {
			this.map.remove(object);
		}
	}

	protected ServerConfigEntry<K> fromJson(JsonObject jsonObject) {
		return new ServerConfigEntry<>(null, jsonObject);
	}

	protected Map<String, V> values() {
		return this.map;
	}

	public void save() throws IOException {
		Collection<V> collection = this.map.values();
		String string = this.GSON.toJson(collection);
		BufferedWriter bufferedWriter = null;

		try {
			bufferedWriter = Files.newWriter(this.file, StandardCharsets.UTF_8);
			bufferedWriter.write(string);
		} finally {
			IOUtils.closeQuietly(bufferedWriter);
		}
	}

	class DeSerializer implements JsonDeserializer<ServerConfigEntry<K>>, JsonSerializer<ServerConfigEntry<K>> {
		private DeSerializer() {
		}

		public JsonElement serialize(ServerConfigEntry<K> serverConfigEntry, Type type, JsonSerializationContext jsonSerializationContext) {
			JsonObject jsonObject = new JsonObject();
			serverConfigEntry.serialize(jsonObject);
			return jsonObject;
		}

		public ServerConfigEntry<K> deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
			if (jsonElement.isJsonObject()) {
				JsonObject jsonObject = jsonElement.getAsJsonObject();
				return ServerConfigList.this.fromJson(jsonObject);
			} else {
				return null;
			}
		}
	}
}
