package net.minecraft.client.sound;

import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import java.lang.reflect.Type;
import net.minecraft.sound.SoundEntry;
import net.minecraft.util.JsonHelper;
import org.apache.commons.lang3.Validate;

public class SoundEntryDeserializer implements JsonDeserializer<SoundEntry> {
	public SoundEntry deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
		JsonObject jsonObject = JsonHelper.asObject(jsonElement, "entry");
		SoundEntry soundEntry = new SoundEntry();
		soundEntry.setReplace(JsonHelper.getBoolean(jsonObject, "replace", false));
		SoundCategory soundCategory = SoundCategory.byName(JsonHelper.getString(jsonObject, "category", SoundCategory.MASTER.getName()));
		soundEntry.setCategory(soundCategory);
		Validate.notNull(soundCategory, "Invalid category", new Object[0]);
		if (jsonObject.has("sounds")) {
			JsonArray jsonArray = JsonHelper.getArray(jsonObject, "sounds");

			for (int i = 0; i < jsonArray.size(); i++) {
				JsonElement jsonElement2 = jsonArray.get(i);
				SoundEntry.Entry entry = new SoundEntry.Entry();
				if (JsonHelper.isString(jsonElement2)) {
					entry.method_7063(JsonHelper.asString(jsonElement2, "sound"));
				} else {
					JsonObject jsonObject2 = JsonHelper.asObject(jsonElement2, "sound");
					entry.method_7063(JsonHelper.getString(jsonObject2, "name"));
					if (jsonObject2.has("type")) {
						SoundEntry.Entry.SoundEntryType soundEntryType = SoundEntry.Entry.SoundEntryType.getTypeByName(JsonHelper.getString(jsonObject2, "type"));
						Validate.notNull(soundEntryType, "Invalid type", new Object[0]);
						entry.method_7062(soundEntryType);
					}

					if (jsonObject2.has("volume")) {
						float f = JsonHelper.getFloat(jsonObject2, "volume");
						Validate.isTrue(f > 0.0F, "Invalid volume", new Object[0]);
						entry.method_7060(f);
					}

					if (jsonObject2.has("pitch")) {
						float g = JsonHelper.getFloat(jsonObject2, "pitch");
						Validate.isTrue(g > 0.0F, "Invalid pitch", new Object[0]);
						entry.method_7066(g);
					}

					if (jsonObject2.has("weight")) {
						int j = JsonHelper.getInt(jsonObject2, "weight");
						Validate.isTrue(j > 0, "Invalid weight", new Object[0]);
						entry.method_7061(j);
					}

					if (jsonObject2.has("stream")) {
						entry.method_7064(JsonHelper.getBoolean(jsonObject2, "stream"));
					}
				}

				soundEntry.getSounds().add(entry);
			}
		}

		return soundEntry;
	}
}
