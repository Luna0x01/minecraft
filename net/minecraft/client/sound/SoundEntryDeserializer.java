package net.minecraft.client.sound;

import com.google.common.collect.Lists;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import java.lang.reflect.Type;
import java.util.List;
import net.minecraft.client.class_2906;
import net.minecraft.sound.SoundEntry;
import net.minecraft.util.JsonHelper;
import org.apache.commons.lang3.Validate;

public class SoundEntryDeserializer implements JsonDeserializer<SoundEntry> {
	public SoundEntry deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
		JsonObject jsonObject = JsonHelper.asObject(jsonElement, "entry");
		boolean bl = JsonHelper.getBoolean(jsonObject, "replace", false);
		String string = JsonHelper.getString(jsonObject, "subtitle", null);
		List<class_2906> list = this.method_12529(jsonObject);
		return new SoundEntry(list, bl, string);
	}

	private List<class_2906> method_12529(JsonObject jsonObject) {
		List<class_2906> list = Lists.newArrayList();
		if (jsonObject.has("sounds")) {
			JsonArray jsonArray = JsonHelper.getArray(jsonObject, "sounds");

			for (int i = 0; i < jsonArray.size(); i++) {
				JsonElement jsonElement = jsonArray.get(i);
				if (JsonHelper.isString(jsonElement)) {
					String string = JsonHelper.asString(jsonElement, "sound");
					list.add(new class_2906(string, 1.0F, 1.0F, 1, class_2906.class_1898.FILE, false));
				} else {
					list.add(this.method_12531(JsonHelper.asObject(jsonElement, "sound")));
				}
			}
		}

		return list;
	}

	private class_2906 method_12531(JsonObject jsonObject) {
		String string = JsonHelper.getString(jsonObject, "name");
		class_2906.class_1898 lv = this.method_12530(jsonObject, class_2906.class_1898.FILE);
		float f = JsonHelper.getFloat(jsonObject, "volume", 1.0F);
		Validate.isTrue(f > 0.0F, "Invalid volume", new Object[0]);
		float g = JsonHelper.getFloat(jsonObject, "pitch", 1.0F);
		Validate.isTrue(g > 0.0F, "Invalid pitch", new Object[0]);
		int i = JsonHelper.getInt(jsonObject, "weight", 1);
		Validate.isTrue(i > 0, "Invalid weight", new Object[0]);
		boolean bl = JsonHelper.getBoolean(jsonObject, "stream", false);
		return new class_2906(string, f, g, i, lv, bl);
	}

	private class_2906.class_1898 method_12530(JsonObject jsonObject, class_2906.class_1898 arg) {
		class_2906.class_1898 lv = arg;
		if (jsonObject.has("type")) {
			lv = class_2906.class_1898.method_7071(JsonHelper.getString(jsonObject, "type"));
			Validate.notNull(lv, "Invalid type", new Object[0]);
		}

		return lv;
	}
}
