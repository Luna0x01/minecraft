package net.minecraft.client.resource;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import java.lang.reflect.Type;
import net.minecraft.client.resource.metadata.TextureResourceMetadata;
import net.minecraft.util.JsonHelper;

public class TextureMetadataSerializer extends ResourceMetadataSerializer<TextureResourceMetadata> {
	public TextureResourceMetadata deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
		JsonObject jsonObject = jsonElement.getAsJsonObject();
		boolean bl = JsonHelper.getBoolean(jsonObject, "blur", false);
		boolean bl2 = JsonHelper.getBoolean(jsonObject, "clamp", false);
		return new TextureResourceMetadata(bl, bl2);
	}

	@Override
	public String getName() {
		return "texture";
	}
}
