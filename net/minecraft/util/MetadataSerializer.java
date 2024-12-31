package net.minecraft.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import net.minecraft.client.resource.ResourceMetadataProvider;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.registry.MutableRegistry;
import net.minecraft.util.registry.Registry;

public class MetadataSerializer {
	private final Registry<String, MetadataSerializer.Section<? extends ResourceMetadataProvider>> REGISTRY = new MutableRegistry<>();
	private final GsonBuilder GSON_BUILDER = new GsonBuilder();
	private Gson gson;

	public MetadataSerializer() {
		this.GSON_BUILDER.registerTypeHierarchyAdapter(Text.class, new Text.Serializer());
		this.GSON_BUILDER.registerTypeHierarchyAdapter(Style.class, new Style.Serializer());
		this.GSON_BUILDER.registerTypeAdapterFactory(new LowercaseEnumTypeAdapterFactory());
	}

	public <T extends ResourceMetadataProvider> void register(net.minecraft.client.resource.MetadataSerializer<T> serializer, Class<T> classType) {
		this.REGISTRY.put(serializer.getName(), new MetadataSerializer.Section<>(serializer, classType));
		this.GSON_BUILDER.registerTypeAdapter(classType, serializer);
		this.gson = null;
	}

	public <T extends ResourceMetadataProvider> T fromJson(String name, JsonObject jsonObject) {
		if (name == null) {
			throw new IllegalArgumentException("Metadata section name cannot be null");
		} else if (!jsonObject.has(name)) {
			return null;
		} else if (!jsonObject.get(name).isJsonObject()) {
			throw new IllegalArgumentException("Invalid metadata for '" + name + "' - expected object, found " + jsonObject.get(name));
		} else {
			MetadataSerializer.Section<?> section = this.REGISTRY.get(name);
			if (section == null) {
				throw new IllegalArgumentException("Don't know how to handle metadata section '" + name + "'");
			} else {
				return (T)this.getGson().fromJson(jsonObject.getAsJsonObject(name), section.clazz);
			}
		}
	}

	private Gson getGson() {
		if (this.gson == null) {
			this.gson = this.GSON_BUILDER.create();
		}

		return this.gson;
	}

	class Section<T extends ResourceMetadataProvider> {
		final net.minecraft.client.resource.MetadataSerializer<T> serializer;
		final Class<T> clazz;

		private Section(net.minecraft.client.resource.MetadataSerializer<T> metadataSerializer2, Class<T> class_) {
			this.serializer = metadataSerializer2;
			this.clazz = class_;
		}
	}
}
