package net.minecraft.client.resource;

import com.google.common.collect.Lists;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import java.lang.reflect.Type;
import java.util.List;
import net.minecraft.client.resource.metadata.TextureResourceMetadata;
import net.minecraft.util.JsonHelper;

public class TextureMetadataSerializer extends ResourceMetadataSerializer<TextureResourceMetadata> {
	public TextureResourceMetadata deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
		JsonObject jsonObject = jsonElement.getAsJsonObject();
		boolean bl = JsonHelper.getBoolean(jsonObject, "blur", false);
		boolean bl2 = JsonHelper.getBoolean(jsonObject, "clamp", false);
		List<Integer> list = Lists.newArrayList();
		if (jsonObject.has("mipmaps")) {
			try {
				JsonArray jsonArray = jsonObject.getAsJsonArray("mipmaps");

				for (int i = 0; i < jsonArray.size(); i++) {
					JsonElement jsonElement2 = jsonArray.get(i);
					if (jsonElement2.isJsonPrimitive()) {
						try {
							list.add(jsonElement2.getAsInt());
						} catch (NumberFormatException var12) {
							throw new JsonParseException("Invalid texture->mipmap->" + i + ": expected number, was " + jsonElement2, var12);
						}
					} else if (jsonElement2.isJsonObject()) {
						throw new JsonParseException("Invalid texture->mipmap->" + i + ": expected number, was " + jsonElement2);
					}
				}
			} catch (ClassCastException var13) {
				throw new JsonParseException("Invalid texture->mipmaps: expected array, was " + jsonObject.get("mipmaps"), var13);
			}
		}

		return new TextureResourceMetadata(bl, bl2, list);
	}

	@Override
	public String getName() {
		return "texture";
	}
}
