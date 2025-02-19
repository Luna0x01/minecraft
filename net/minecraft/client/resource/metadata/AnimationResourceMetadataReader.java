package net.minecraft.client.resource.metadata;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableList.Builder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import javax.annotation.Nullable;
import net.minecraft.resource.metadata.ResourceMetadataReader;
import net.minecraft.util.JsonHelper;
import org.apache.commons.lang3.Validate;

public class AnimationResourceMetadataReader implements ResourceMetadataReader<AnimationResourceMetadata> {
	public AnimationResourceMetadata fromJson(JsonObject jsonObject) {
		Builder<AnimationFrameResourceMetadata> builder = ImmutableList.builder();
		int i = JsonHelper.getInt(jsonObject, "frametime", 1);
		if (i != 1) {
			Validate.inclusiveBetween(1L, 2147483647L, (long)i, "Invalid default frame time");
		}

		if (jsonObject.has("frames")) {
			try {
				JsonArray jsonArray = JsonHelper.getArray(jsonObject, "frames");

				for (int j = 0; j < jsonArray.size(); j++) {
					JsonElement jsonElement = jsonArray.get(j);
					AnimationFrameResourceMetadata animationFrameResourceMetadata = this.readFrameMetadata(j, jsonElement);
					if (animationFrameResourceMetadata != null) {
						builder.add(animationFrameResourceMetadata);
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
		return new AnimationResourceMetadata(builder.build(), k, l, i, bl);
	}

	@Nullable
	private AnimationFrameResourceMetadata readFrameMetadata(int frame, JsonElement json) {
		if (json.isJsonPrimitive()) {
			return new AnimationFrameResourceMetadata(JsonHelper.asInt(json, "frames[" + frame + "]"));
		} else if (json.isJsonObject()) {
			JsonObject jsonObject = JsonHelper.asObject(json, "frames[" + frame + "]");
			int i = JsonHelper.getInt(jsonObject, "time", -1);
			if (jsonObject.has("time")) {
				Validate.inclusiveBetween(1L, 2147483647L, (long)i, "Invalid frame time");
			}

			int j = JsonHelper.getInt(jsonObject, "index");
			Validate.inclusiveBetween(0L, 2147483647L, (long)j, "Invalid frame index");
			return new AnimationFrameResourceMetadata(j, i);
		} else {
			return null;
		}
	}

	@Override
	public String getKey() {
		return "animation";
	}
}
