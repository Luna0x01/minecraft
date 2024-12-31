package net.minecraft.text;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Map.Entry;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.LowercaseEnumTypeAdapterFactory;

public interface Text extends Iterable<Text> {
	Text setStyle(Style style);

	Style getStyle();

	Text append(String text);

	Text append(Text text);

	String computeValue();

	String asUnformattedString();

	String asFormattedString();

	List<Text> getSiblings();

	Text copy();

	public static class Serializer implements JsonDeserializer<Text>, JsonSerializer<Text> {
		private static final Gson GSON;

		public Text deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
			if (jsonElement.isJsonPrimitive()) {
				return new LiteralText(jsonElement.getAsString());
			} else if (!jsonElement.isJsonObject()) {
				if (jsonElement.isJsonArray()) {
					JsonArray jsonArray3 = jsonElement.getAsJsonArray();
					Text text8 = null;

					for (JsonElement jsonElement2 : jsonArray3) {
						Text text9 = this.deserialize(jsonElement2, jsonElement2.getClass(), jsonDeserializationContext);
						if (text8 == null) {
							text8 = text9;
						} else {
							text8.append(text9);
						}
					}

					return text8;
				} else {
					throw new JsonParseException("Don't know how to turn " + jsonElement + " into a Component");
				}
			} else {
				JsonObject jsonObject = jsonElement.getAsJsonObject();
				Text text;
				if (jsonObject.has("text")) {
					text = new LiteralText(jsonObject.get("text").getAsString());
				} else if (jsonObject.has("translate")) {
					String string = jsonObject.get("translate").getAsString();
					if (jsonObject.has("with")) {
						JsonArray jsonArray = jsonObject.getAsJsonArray("with");
						Object[] objects = new Object[jsonArray.size()];

						for (int i = 0; i < objects.length; i++) {
							objects[i] = this.deserialize(jsonArray.get(i), type, jsonDeserializationContext);
							if (objects[i] instanceof LiteralText) {
								LiteralText literalText = (LiteralText)objects[i];
								if (literalText.getStyle().isEmpty() && literalText.getSiblings().isEmpty()) {
									objects[i] = literalText.getRawString();
								}
							}
						}

						text = new TranslatableText(string, objects);
					} else {
						text = new TranslatableText(string);
					}
				} else if (jsonObject.has("score")) {
					JsonObject jsonObject2 = jsonObject.getAsJsonObject("score");
					if (!jsonObject2.has("name") || !jsonObject2.has("objective")) {
						throw new JsonParseException("A score component needs a least a name and an objective");
					}

					text = new ScoreText(JsonHelper.getString(jsonObject2, "name"), JsonHelper.getString(jsonObject2, "objective"));
					if (jsonObject2.has("value")) {
						((ScoreText)text).setScore(JsonHelper.getString(jsonObject2, "value"));
					}
				} else {
					if (!jsonObject.has("selector")) {
						throw new JsonParseException("Don't know how to turn " + jsonElement + " into a Component");
					}

					text = new SelectorText(JsonHelper.getString(jsonObject, "selector"));
				}

				if (jsonObject.has("extra")) {
					JsonArray jsonArray2 = jsonObject.getAsJsonArray("extra");
					if (jsonArray2.size() <= 0) {
						throw new JsonParseException("Unexpected empty array of components");
					}

					for (int j = 0; j < jsonArray2.size(); j++) {
						text.append(this.deserialize(jsonArray2.get(j), type, jsonDeserializationContext));
					}
				}

				text.setStyle((Style)jsonDeserializationContext.deserialize(jsonElement, Style.class));
				return text;
			}
		}

		private void serializeStyle(Style style, JsonObject object, JsonSerializationContext ctx) {
			JsonElement jsonElement = ctx.serialize(style);
			if (jsonElement.isJsonObject()) {
				JsonObject jsonObject = (JsonObject)jsonElement;

				for (Entry<String, JsonElement> entry : jsonObject.entrySet()) {
					object.add((String)entry.getKey(), (JsonElement)entry.getValue());
				}
			}
		}

		public JsonElement serialize(Text text, Type type, JsonSerializationContext jsonSerializationContext) {
			JsonObject jsonObject = new JsonObject();
			if (!text.getStyle().isEmpty()) {
				this.serializeStyle(text.getStyle(), jsonObject, jsonSerializationContext);
			}

			if (!text.getSiblings().isEmpty()) {
				JsonArray jsonArray = new JsonArray();

				for (Text text2 : text.getSiblings()) {
					jsonArray.add(this.serialize(text2, text2.getClass(), jsonSerializationContext));
				}

				jsonObject.add("extra", jsonArray);
			}

			if (text instanceof LiteralText) {
				jsonObject.addProperty("text", ((LiteralText)text).getRawString());
			} else if (text instanceof TranslatableText) {
				TranslatableText translatableText = (TranslatableText)text;
				jsonObject.addProperty("translate", translatableText.getKey());
				if (translatableText.getArgs() != null && translatableText.getArgs().length > 0) {
					JsonArray jsonArray2 = new JsonArray();

					for (Object object : translatableText.getArgs()) {
						if (object instanceof Text) {
							jsonArray2.add(this.serialize((Text)object, object.getClass(), jsonSerializationContext));
						} else {
							jsonArray2.add(new JsonPrimitive(String.valueOf(object)));
						}
					}

					jsonObject.add("with", jsonArray2);
				}
			} else if (text instanceof ScoreText) {
				ScoreText scoreText = (ScoreText)text;
				JsonObject jsonObject2 = new JsonObject();
				jsonObject2.addProperty("name", scoreText.getName());
				jsonObject2.addProperty("objective", scoreText.getObjective());
				jsonObject2.addProperty("value", scoreText.computeValue());
				jsonObject.add("score", jsonObject2);
			} else {
				if (!(text instanceof SelectorText)) {
					throw new IllegalArgumentException("Don't know how to serialize " + text + " as a Component");
				}

				SelectorText selectorText = (SelectorText)text;
				jsonObject.addProperty("selector", selectorText.getPattern());
			}

			return jsonObject;
		}

		public static String serialize(Text text) {
			return GSON.toJson(text);
		}

		public static Text deserializeText(String string) {
			return JsonHelper.deserialize(GSON, string, Text.class, false);
		}

		public static Text lenientDeserializeText(String string) {
			return JsonHelper.deserialize(GSON, string, Text.class, true);
		}

		static {
			GsonBuilder gsonBuilder = new GsonBuilder();
			gsonBuilder.registerTypeHierarchyAdapter(Text.class, new Text.Serializer());
			gsonBuilder.registerTypeHierarchyAdapter(Style.class, new Style.Serializer());
			gsonBuilder.registerTypeAdapterFactory(new LowercaseEnumTypeAdapterFactory());
			GSON = gsonBuilder.create();
		}
	}
}
