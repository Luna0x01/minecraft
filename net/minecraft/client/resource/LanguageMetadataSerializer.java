package net.minecraft.client.resource;

import com.google.common.collect.Sets;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import java.lang.reflect.Type;
import java.util.Set;
import java.util.Map.Entry;
import net.minecraft.client.resource.language.LanguageDefinition;
import net.minecraft.client.resource.metadata.LanguageResourceMetadata;
import net.minecraft.util.JsonHelper;

public class LanguageMetadataSerializer extends ResourceMetadataSerializer<LanguageResourceMetadata> {
	public LanguageResourceMetadata deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
		JsonObject jsonObject = jsonElement.getAsJsonObject();
		Set<LanguageDefinition> set = Sets.newHashSet();

		for (Entry<String, JsonElement> entry : jsonObject.entrySet()) {
			String string = (String)entry.getKey();
			JsonObject jsonObject2 = JsonHelper.asObject((JsonElement)entry.getValue(), "language");
			String string2 = JsonHelper.getString(jsonObject2, "region");
			String string3 = JsonHelper.getString(jsonObject2, "name");
			boolean bl = JsonHelper.getBoolean(jsonObject2, "bidirectional", false);
			if (string2.isEmpty()) {
				throw new JsonParseException("Invalid language->'" + string + "'->region: empty value");
			}

			if (string3.isEmpty()) {
				throw new JsonParseException("Invalid language->'" + string + "'->name: empty value");
			}

			if (!set.add(new LanguageDefinition(string, string2, string3, bl))) {
				throw new JsonParseException("Duplicate language->'" + string + "' defined");
			}
		}

		return new LanguageResourceMetadata(set);
	}

	@Override
	public String getName() {
		return "language";
	}
}
