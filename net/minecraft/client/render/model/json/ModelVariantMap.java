package net.minecraft.client.render.model.json;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import java.io.Reader;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;

public class ModelVariantMap {
	static final Gson GSON = new GsonBuilder()
		.registerTypeAdapter(ModelVariantMap.class, new ModelVariantMap.Deserializer())
		.registerTypeAdapter(ModelVariantMap.Entry.class, new ModelVariantMap.Entry.Deserializer())
		.create();
	private final Map<String, ModelVariantMap.Variant> map = Maps.newHashMap();

	public static ModelVariantMap fromReader(Reader reader) {
		return (ModelVariantMap)GSON.fromJson(reader, ModelVariantMap.class);
	}

	public ModelVariantMap(Collection<ModelVariantMap.Variant> collection) {
		for (ModelVariantMap.Variant variant : collection) {
			this.map.put(variant.name, variant);
		}
	}

	public ModelVariantMap(List<ModelVariantMap> list) {
		for (ModelVariantMap modelVariantMap : list) {
			this.map.putAll(modelVariantMap.map);
		}
	}

	public ModelVariantMap.Variant getVariant(String name) {
		ModelVariantMap.Variant variant = (ModelVariantMap.Variant)this.map.get(name);
		if (variant == null) {
			throw new ModelVariantMap.ModelVariantException();
		} else {
			return variant;
		}
	}

	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		} else if (obj instanceof ModelVariantMap) {
			ModelVariantMap modelVariantMap = (ModelVariantMap)obj;
			return this.map.equals(modelVariantMap.map);
		} else {
			return false;
		}
	}

	public int hashCode() {
		return this.map.hashCode();
	}

	public static class Deserializer implements JsonDeserializer<ModelVariantMap> {
		public ModelVariantMap deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
			JsonObject jsonObject = jsonElement.getAsJsonObject();
			List<ModelVariantMap.Variant> list = this.deserializeVariants(jsonDeserializationContext, jsonObject);
			return new ModelVariantMap(list);
		}

		protected List<ModelVariantMap.Variant> deserializeVariants(JsonDeserializationContext ctx, JsonObject json) {
			JsonObject jsonObject = JsonHelper.getObject(json, "variants");
			List<ModelVariantMap.Variant> list = Lists.newArrayList();

			for (java.util.Map.Entry<String, JsonElement> entry : jsonObject.entrySet()) {
				list.add(this.deserializeVariant(ctx, entry));
			}

			return list;
		}

		protected ModelVariantMap.Variant deserializeVariant(JsonDeserializationContext ctx, java.util.Map.Entry<String, JsonElement> entry) {
			String string = (String)entry.getKey();
			List<ModelVariantMap.Entry> list = Lists.newArrayList();
			JsonElement jsonElement = (JsonElement)entry.getValue();
			if (jsonElement.isJsonArray()) {
				for (JsonElement jsonElement2 : jsonElement.getAsJsonArray()) {
					list.add((ModelVariantMap.Entry)ctx.deserialize(jsonElement2, ModelVariantMap.Entry.class));
				}
			} else {
				list.add((ModelVariantMap.Entry)ctx.deserialize(jsonElement, ModelVariantMap.Entry.class));
			}

			return new ModelVariantMap.Variant(string, list);
		}
	}

	public static class Entry {
		private final Identifier id;
		private final net.minecraft.client.render.model.ModelRotation rotation;
		private final boolean uvLock;
		private final int weight;

		public Entry(Identifier identifier, net.minecraft.client.render.model.ModelRotation modelRotation, boolean bl, int i) {
			this.id = identifier;
			this.rotation = modelRotation;
			this.uvLock = bl;
			this.weight = i;
		}

		public Identifier getId() {
			return this.id;
		}

		public net.minecraft.client.render.model.ModelRotation getRotation() {
			return this.rotation;
		}

		public boolean hasUvLock() {
			return this.uvLock;
		}

		public int getWeight() {
			return this.weight;
		}

		public boolean equals(Object obj) {
			if (this == obj) {
				return true;
			} else if (!(obj instanceof ModelVariantMap.Entry)) {
				return false;
			} else {
				ModelVariantMap.Entry entry = (ModelVariantMap.Entry)obj;
				return this.id.equals(entry.id) && this.rotation == entry.rotation && this.uvLock == entry.uvLock;
			}
		}

		public int hashCode() {
			int i = this.id.hashCode();
			i = 31 * i + (this.rotation != null ? this.rotation.hashCode() : 0);
			return 31 * i + (this.uvLock ? 1 : 0);
		}

		public static class Deserializer implements JsonDeserializer<ModelVariantMap.Entry> {
			public ModelVariantMap.Entry deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
				JsonObject jsonObject = jsonElement.getAsJsonObject();
				String string = this.getModel(jsonObject);
				net.minecraft.client.render.model.ModelRotation modelRotation = this.getRotation(jsonObject);
				boolean bl = this.getUvLock(jsonObject);
				int i = this.getWeight(jsonObject);
				return new ModelVariantMap.Entry(this.derelativizeId(string), modelRotation, bl, i);
			}

			private Identifier derelativizeId(String id) {
				Identifier identifier = new Identifier(id);
				return new Identifier(identifier.getNamespace(), "block/" + identifier.getPath());
			}

			private boolean getUvLock(JsonObject json) {
				return JsonHelper.getBoolean(json, "uvlock", false);
			}

			protected net.minecraft.client.render.model.ModelRotation getRotation(JsonObject json) {
				int i = JsonHelper.getInt(json, "x", 0);
				int j = JsonHelper.getInt(json, "y", 0);
				net.minecraft.client.render.model.ModelRotation modelRotation = net.minecraft.client.render.model.ModelRotation.get(i, j);
				if (modelRotation == null) {
					throw new JsonParseException("Invalid BlockModelRotation x: " + i + ", y: " + j);
				} else {
					return modelRotation;
				}
			}

			protected String getModel(JsonObject json) {
				return JsonHelper.getString(json, "model");
			}

			protected int getWeight(JsonObject json) {
				return JsonHelper.getInt(json, "weight", 1);
			}
		}
	}

	public class ModelVariantException extends RuntimeException {
		protected ModelVariantException() {
		}
	}

	public static class Variant {
		private final String name;
		private final List<ModelVariantMap.Entry> entries;

		public Variant(String string, List<ModelVariantMap.Entry> list) {
			this.name = string;
			this.entries = list;
		}

		public List<ModelVariantMap.Entry> getEntries() {
			return this.entries;
		}

		public boolean equals(Object obj) {
			if (this == obj) {
				return true;
			} else if (!(obj instanceof ModelVariantMap.Variant)) {
				return false;
			} else {
				ModelVariantMap.Variant variant = (ModelVariantMap.Variant)obj;
				return !this.name.equals(variant.name) ? false : this.entries.equals(variant.entries);
			}
		}

		public int hashCode() {
			int i = this.name.hashCode();
			return 31 * i + this.entries.hashCode();
		}
	}
}
