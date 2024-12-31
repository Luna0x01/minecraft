package net.minecraft.client.render.model.json;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.Maps;
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
import java.util.Map.Entry;
import javax.annotation.Nullable;
import net.minecraft.class_4234;
import net.minecraft.class_4236;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.client.Variant;
import net.minecraft.client.class_2885;
import net.minecraft.state.StateManager;
import net.minecraft.util.JsonHelper;

public class ModelVariantMap {
	private final Map<String, class_4234> map = Maps.newLinkedHashMap();
	private class_4236 field_13554;

	public static ModelVariantMap method_19233(ModelVariantMap.class_4232 arg, Reader reader) {
		return JsonHelper.deserialize(arg.field_20797, reader, ModelVariantMap.class);
	}

	public ModelVariantMap(Map<String, class_4234> map, class_4236 arg) {
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

	public Map<String, class_4234> method_19232() {
		return this.map;
	}

	public boolean method_12357() {
		return this.field_13554 != null;
	}

	public class_4236 method_12359() {
		return this.field_13554;
	}

	public static class Deserializer implements JsonDeserializer<ModelVariantMap> {
		public ModelVariantMap deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
			JsonObject jsonObject = jsonElement.getAsJsonObject();
			Map<String, class_4234> map = this.method_12360(jsonDeserializationContext, jsonObject);
			class_4236 lv = this.method_12361(jsonDeserializationContext, jsonObject);
			if (!map.isEmpty() || lv != null && !lv.method_19272().isEmpty()) {
				return new ModelVariantMap(map, lv);
			} else {
				throw new JsonParseException("Neither 'variants' nor 'multipart' found");
			}
		}

		protected Map<String, class_4234> method_12360(JsonDeserializationContext jsonDeserializationContext, JsonObject jsonObject) {
			Map<String, class_4234> map = Maps.newHashMap();
			if (jsonObject.has("variants")) {
				JsonObject jsonObject2 = JsonHelper.getObject(jsonObject, "variants");

				for (Entry<String, JsonElement> entry : jsonObject2.entrySet()) {
					map.put(entry.getKey(), jsonDeserializationContext.deserialize((JsonElement)entry.getValue(), class_4234.class));
				}
			}

			return map;
		}

		@Nullable
		protected class_4236 method_12361(JsonDeserializationContext jsonDeserializationContext, JsonObject jsonObject) {
			if (!jsonObject.has("multipart")) {
				return null;
			} else {
				JsonArray jsonArray = JsonHelper.getArray(jsonObject, "multipart");
				return (class_4236)jsonDeserializationContext.deserialize(jsonArray, class_4236.class);
			}
		}
	}

	public static final class class_4232 {
		@VisibleForTesting
		final Gson field_20797 = new GsonBuilder()
			.registerTypeAdapter(ModelVariantMap.class, new ModelVariantMap.Deserializer())
			.registerTypeAdapter(Variant.class, new Variant.VariantDeserializer())
			.registerTypeAdapter(class_4234.class, new class_4234.class_2878())
			.registerTypeAdapter(class_4236.class, new class_4236.class_2883(this))
			.registerTypeAdapter(class_2885.class, new class_2885.class_4237())
			.create();
		private StateManager<Block, BlockState> field_20798;

		public StateManager<Block, BlockState> method_19234() {
			return this.field_20798;
		}

		public void method_19235(StateManager<Block, BlockState> stateManager) {
			this.field_20798 = stateManager;
		}
	}
}
