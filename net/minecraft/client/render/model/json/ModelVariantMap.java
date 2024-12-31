package net.minecraft.client.render.model.json;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import java.io.Reader;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import javax.annotation.Nullable;
import net.minecraft.client.Variant;
import net.minecraft.client.class_2877;
import net.minecraft.client.class_2882;
import net.minecraft.client.class_2885;
import net.minecraft.util.JsonHelper;

public class ModelVariantMap {
	@VisibleForTesting
	static final Gson GSON = new GsonBuilder()
		.registerTypeAdapter(ModelVariantMap.class, new ModelVariantMap.Deserializer())
		.registerTypeAdapter(Variant.class, new Variant.VariantDeserializer())
		.registerTypeAdapter(class_2877.class, new class_2877.class_2878())
		.registerTypeAdapter(class_2882.class, new class_2882.class_2883())
		.registerTypeAdapter(class_2885.class, new class_2885.class_2886())
		.create();
	private final Map<String, class_2877> map = Maps.newHashMap();
	private class_2882 field_13554;

	public static ModelVariantMap fromReader(Reader reader) {
		return (ModelVariantMap)GSON.fromJson(reader, ModelVariantMap.class);
	}

	public ModelVariantMap(Map<String, class_2877> map, class_2882 arg) {
		this.field_13554 = arg;
		this.map.putAll(map);
	}

	public ModelVariantMap(List<ModelVariantMap> list) {
		ModelVariantMap modelVariantMap = null;

		for (ModelVariantMap modelVariantMap2 : list) {
			if (modelVariantMap2.method_12357()) {
				this.map.clear();
				modelVariantMap = modelVariantMap2;
			}

			this.map.putAll(modelVariantMap2.map);
		}

		if (modelVariantMap != null) {
			this.field_13554 = modelVariantMap.field_13554;
		}
	}

	public boolean method_12358(String string) {
		return this.map.get(string) != null;
	}

	public class_2877 method_10030(String string) {
		class_2877 lv = (class_2877)this.map.get(string);
		if (lv == null) {
			throw new ModelVariantMap.ModelVariantException();
		} else {
			return lv;
		}
	}

	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		} else {
			if (obj instanceof ModelVariantMap) {
				ModelVariantMap modelVariantMap = (ModelVariantMap)obj;
				if (this.map.equals(modelVariantMap.map)) {
					return this.method_12357() ? this.field_13554.equals(modelVariantMap.field_13554) : !modelVariantMap.method_12357();
				}
			}

			return false;
		}
	}

	public int hashCode() {
		return 31 * this.map.hashCode() + (this.method_12357() ? this.field_13554.hashCode() : 0);
	}

	public Set<class_2877> method_12356() {
		Set<class_2877> set = Sets.newHashSet(this.map.values());
		if (this.method_12357()) {
			set.addAll(this.field_13554.method_12388());
		}

		return set;
	}

	public boolean method_12357() {
		return this.field_13554 != null;
	}

	public class_2882 method_12359() {
		return this.field_13554;
	}

	public static class Deserializer implements JsonDeserializer<ModelVariantMap> {
		public ModelVariantMap deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
			JsonObject jsonObject = jsonElement.getAsJsonObject();
			Map<String, class_2877> map = this.method_12360(jsonDeserializationContext, jsonObject);
			class_2882 lv = this.method_12361(jsonDeserializationContext, jsonObject);
			if (!map.isEmpty() || lv != null && !lv.method_12388().isEmpty()) {
				return new ModelVariantMap(map, lv);
			} else {
				throw new JsonParseException("Neither 'variants' nor 'multipart' found");
			}
		}

		protected Map<String, class_2877> method_12360(JsonDeserializationContext jsonDeserializationContext, JsonObject jsonObject) {
			Map<String, class_2877> map = Maps.newHashMap();
			if (jsonObject.has("variants")) {
				JsonObject jsonObject2 = JsonHelper.getObject(jsonObject, "variants");

				for (Entry<String, JsonElement> entry : jsonObject2.entrySet()) {
					map.put(entry.getKey(), (class_2877)jsonDeserializationContext.deserialize((JsonElement)entry.getValue(), class_2877.class));
				}
			}

			return map;
		}

		@Nullable
		protected class_2882 method_12361(JsonDeserializationContext jsonDeserializationContext, JsonObject jsonObject) {
			if (!jsonObject.has("multipart")) {
				return null;
			} else {
				JsonArray jsonArray = JsonHelper.getArray(jsonObject, "multipart");
				return (class_2882)jsonDeserializationContext.deserialize(jsonArray, class_2882.class);
			}
		}
	}

	public class ModelVariantException extends RuntimeException {
		protected ModelVariantException() {
		}
	}
}
