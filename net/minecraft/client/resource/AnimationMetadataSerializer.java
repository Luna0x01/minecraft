package net.minecraft.client.resource;

import com.google.common.collect.Lists;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import java.util.List;
import net.minecraft.class_4457;
import net.minecraft.client.resource.metadata.AnimationFrameResourceMetadata;
import net.minecraft.util.JsonHelper;
import org.apache.commons.lang3.Validate;

public class AnimationMetadataSerializer implements class_4457<AnimationMetadata> {
	public AnimationMetadata method_21335(JsonObject jsonObject) {
		List<AnimationFrameResourceMetadata> list = Lists.newArrayList();
		int i = JsonHelper.getInt(jsonObject, "frametime", 1);
		if (i != 1) {
			Validate.inclusiveBetween(1L, 2147483647L, (long)i, "Invalid default frame time");
		}

		if (jsonObject.has("frames")) {
			try {
				JsonArray jsonArray = JsonHelper.getArray(jsonObject, "frames");

				for (int j = 0; j < jsonArray.size(); j++) {
					JsonElement jsonElement = jsonArray.get(j);
					AnimationFrameResourceMetadata animationFrameResourceMetadata = this.method_7048(j, jsonElement);
					if (animationFrameResourceMetadata != null) {
						list.add(animationFrameResourceMetadata);
					}
				}
			} catch (ClassCastException var8) {
				throw new JsonParseException("Invalid animation->frames: expected array, was " + jsonObject.get("frames"), var8);
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

	@Override
	public String method_5956() {
		return "animation";
	}
}
