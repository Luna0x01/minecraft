package net.minecraft.client.resource;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import java.lang.reflect.Type;
import net.minecraft.util.JsonHelper;
import org.apache.commons.lang3.Validate;

public class FontMetadataSerializer extends ResourceMetadataSerializer<FontMetadata> {
	public FontMetadata deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
		JsonObject jsonObject = jsonElement.getAsJsonObject();
		float[] fs = new float[256];
		float[] gs = new float[256];
		float[] hs = new float[256];
		float f = 1.0F;
		float g = 0.0F;
		float h = 0.0F;
		if (jsonObject.has("characters")) {
			if (!jsonObject.get("characters").isJsonObject()) {
				throw new JsonParseException("Invalid font->characters: expected object, was " + jsonObject.get("characters"));
			}

			JsonObject jsonObject2 = jsonObject.getAsJsonObject("characters");
			if (jsonObject2.has("default")) {
				if (!jsonObject2.get("default").isJsonObject()) {
					throw new JsonParseException("Invalid font->characters->default: expected object, was " + jsonObject2.get("default"));
				}

				JsonObject jsonObject3 = jsonObject2.getAsJsonObject("default");
				f = JsonHelper.getFloat(jsonObject3, "width", f);
				Validate.inclusiveBetween(0.0, Float.MAX_VALUE, (double)f, "Invalid default width");
				g = JsonHelper.getFloat(jsonObject3, "spacing", g);
				Validate.inclusiveBetween(0.0, Float.MAX_VALUE, (double)g, "Invalid default spacing");
				h = JsonHelper.getFloat(jsonObject3, "left", g);
				Validate.inclusiveBetween(0.0, Float.MAX_VALUE, (double)h, "Invalid default left");
			}

			for (int i = 0; i < 256; i++) {
				JsonElement jsonElement2 = jsonObject2.get(Integer.toString(i));
				float j = f;
				float k = g;
				float l = h;
				if (jsonElement2 != null) {
					JsonObject jsonObject4 = JsonHelper.asObject(jsonElement2, "characters[" + i + "]");
					j = JsonHelper.getFloat(jsonObject4, "width", f);
					Validate.inclusiveBetween(0.0, Float.MAX_VALUE, (double)j, "Invalid width");
					k = JsonHelper.getFloat(jsonObject4, "spacing", g);
					Validate.inclusiveBetween(0.0, Float.MAX_VALUE, (double)k, "Invalid spacing");
					l = JsonHelper.getFloat(jsonObject4, "left", h);
					Validate.inclusiveBetween(0.0, Float.MAX_VALUE, (double)l, "Invalid left");
				}

				fs[i] = j;
				gs[i] = k;
				hs[i] = l;
			}
		}

		return new FontMetadata(fs, hs, gs);
	}

	@Override
	public String getName() {
		return "font";
	}
}
