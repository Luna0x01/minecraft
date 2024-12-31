package net.minecraft.client.resource;

import com.google.common.collect.Lists;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import java.lang.reflect.Type;
import java.util.List;
import net.minecraft.client.resource.metadata.AnimationFrameResourceMetadata;
import net.minecraft.util.JsonHelper;
import org.apache.commons.lang3.Validate;

public class AnimationMetadataSerializer extends ResourceMetadataSerializer<AnimationMetadata> implements JsonSerializer<AnimationMetadata> {
	public AnimationMetadata deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
		List<AnimationFrameResourceMetadata> list = Lists.newArrayList();
		JsonObject jsonObject = JsonHelper.asObject(jsonElement, "metadata section");
		int i = JsonHelper.getInt(jsonObject, "frametime", 1);
		if (i != 1) {
			Validate.inclusiveBetween(1L, 2147483647L, (long)i, "Invalid default frame time");
		}

		if (jsonObject.has("frames")) {
			try {
				JsonArray jsonArray = JsonHelper.getArray(jsonObject, "frames");

				for (int j = 0; j < jsonArray.size(); j++) {
					JsonElement jsonElement2 = jsonArray.get(j);
					AnimationFrameResourceMetadata animationFrameResourceMetadata = this.method_7048(j, jsonElement2);
					if (animationFrameResourceMetadata != null) {
						list.add(animationFrameResourceMetadata);
					}
				}
			} catch (ClassCastException var11) {
				throw new JsonParseException("Invalid animation->frames: expected array, was " + jsonObject.get("frames"), var11);
			}
		}

		int k = JsonHelper.getInt(jsonObject, "width", -1);
		int l = JsonHelper.getInt(jsonObject, "height", -1);
		if (k != -1) {
			Validate.inclusiveBetween(1L, 2147483647L, (long)k, "Invalid width");
		}

		if (l != -1) {
			Validate.inclusiveBetween(1L, 2147483647L, (long)l, "Invalid height");
		}

		boolean bl = JsonHelper.getBoolean(jsonObject, "interpolate", false);
		return new AnimationMetadata(list, k, l, i, bl);
	}

	private AnimationFrameResourceMetadata method_7048(int i, JsonElement jsonElement) {
		if (jsonElement.isJsonPrimitive()) {
			return new AnimationFrameResourceMetadata(JsonHelper.asInt(jsonElement, "frames[" + i + "]"));
		} else if (jsonElement.isJsonObject()) {
			JsonObject jsonObject = JsonHelper.asObject(jsonElement, "frames[" + i + "]");
			int j = JsonHelper.getInt(jsonObject, "time", -1);
			if (jsonObject.has("time")) {
				Validate.inclusiveBetween(1L, 2147483647L, (long)j, "Invalid frame time");
			}

			int k = JsonHelper.getInt(jsonObject, "index");
			Validate.inclusiveBetween(0L, 2147483647L, (long)k, "Invalid frame index");
			return new AnimationFrameResourceMetadata(k, j);
		} else {
			return null;
		}
	}

	public JsonElement serialize(AnimationMetadata animationMetadata, Type type, JsonSerializationContext jsonSerializationContext) {
		JsonObject jsonObject = new JsonObject();
		jsonObject.addProperty("frametime", animationMetadata.getTime());
		if (animationMetadata.getWidth() != -1) {
			jsonObject.addProperty("width", animationMetadata.getWidth());
		}

		if (animationMetadata.getHeight() != -1) {
			jsonObject.addProperty("height", animationMetadata.getHeight());
		}

		if (animationMetadata.getMetadataListSize() > 0) {
			JsonArray jsonArray = new JsonArray();

			for (int i = 0; i < animationMetadata.getMetadataListSize(); i++) {
				if (animationMetadata.method_5964(i)) {
					JsonObject jsonObject2 = new JsonObject();
					jsonObject2.addProperty("index", animationMetadata.getIndex(i));
					jsonObject2.addProperty("time", animationMetadata.getTime(i));
					jsonArray.add(jsonObject2);
				} else {
					jsonArray.add(new JsonPrimitive(animationMetadata.getIndex(i)));
				}
			}

			jsonObject.add("frames", jsonArray);
		}

		return jsonObject;
	}

	@Override
	public String getName() {
		return "animation";
	}
}
