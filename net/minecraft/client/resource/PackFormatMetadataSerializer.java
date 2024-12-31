package net.minecraft.client.resource;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import java.lang.reflect.Type;
import net.minecraft.text.Text;
import net.minecraft.util.JsonHelper;

public class PackFormatMetadataSerializer extends ResourceMetadataSerializer<ResourcePackMetadata> implements JsonSerializer<ResourcePackMetadata> {
	public ResourcePackMetadata deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
		JsonObject jsonObject = jsonElement.getAsJsonObject();
		Text text = (Text)jsonDeserializationContext.deserialize(jsonObject.get("description"), Text.class);
		if (text == null) {
			throw new JsonParseException("Invalid/missing description!");
		} else {
			int i = JsonHelper.getInt(jsonObject, "pack_format");
			return new ResourcePackMetadata(text, i);
		}
	}

	public JsonElement serialize(ResourcePackMetadata resourcePackMetadata, Type type, JsonSerializationContext jsonSerializationContext) {
		JsonObject jsonObject = new JsonObject();
		jsonObject.addProperty("pack_format", resourcePackMetadata.getPackFormat());
		jsonObject.add("description", jsonSerializationContext.serialize(resourcePackMetadata.getDescription()));
		return jsonObject;
	}

	@Override
	public String getName() {
		return "pack";
	}
}
