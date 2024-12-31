package net.minecraft.datafixer.fix;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import java.lang.reflect.Type;
import net.minecraft.datafixer.DataFix;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.util.ChatUtil;
import net.minecraft.util.JsonHelper;

public class BlockEntitySignTextStrictJsonFix implements DataFix {
	public static final Gson GSON = new GsonBuilder().registerTypeAdapter(Text.class, new JsonDeserializer<Text>() {
		public Text deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
			if (jsonElement.isJsonPrimitive()) {
				return new LiteralText(jsonElement.getAsString());
			} else if (jsonElement.isJsonArray()) {
				JsonArray jsonArray = jsonElement.getAsJsonArray();
				Text text = null;

				for (JsonElement jsonElement2 : jsonArray) {
					Text text2 = this.deserialize(jsonElement2, jsonElement2.getClass(), jsonDeserializationContext);
					if (text == null) {
						text = text2;
					} else {
						text.append(text2);
					}
				}

				return text;
			} else {
				throw new JsonParseException("Don't know how to turn " + jsonElement + " into a Component");
			}
		}
	}).create();

	@Override
	public int getVersion() {
		return 101;
	}

	@Override
	public NbtCompound fixData(NbtCompound tag) {
		if ("Sign".equals(tag.getString("id"))) {
			this.fixText(tag, "Text1");
			this.fixText(tag, "Text2");
			this.fixText(tag, "Text3");
			this.fixText(tag, "Text4");
		}

		return tag;
	}

	private void fixText(NbtCompound old, String lineName) {
		String string = old.getString(lineName);
		Text text = null;
		if (!"null".equals(string) && !ChatUtil.isEmpty(string)) {
			if (string.charAt(0) == '"' && string.charAt(string.length() - 1) == '"' || string.charAt(0) == '{' && string.charAt(string.length() - 1) == '}') {
				try {
					text = JsonHelper.deserialize(GSON, string, Text.class, true);
					if (text == null) {
						text = new LiteralText("");
					}
				} catch (JsonParseException var8) {
				}

				if (text == null) {
					try {
						text = Text.Serializer.deserializeText(string);
					} catch (JsonParseException var7) {
					}
				}

				if (text == null) {
					try {
						text = Text.Serializer.lenientDeserializeText(string);
					} catch (JsonParseException var6) {
					}
				}

				if (text == null) {
					text = new LiteralText(string);
				}
			} else {
				text = new LiteralText(string);
			}
		} else {
			text = new LiteralText("");
		}

		old.putString(lineName, Text.Serializer.serialize(text));
	}
}
