package net.minecraft.text;

import com.google.common.collect.Lists;
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
import com.google.gson.stream.JsonReader;
import com.mojang.brigadier.Message;
import java.io.IOException;
import java.io.StringReader;
import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Optional;
import java.util.Map.Entry;
import javax.annotation.Nullable;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.LowercaseEnumTypeAdapterFactory;
import net.minecraft.util.Util;

public interface Text extends Message, StringVisitable {
	Style getStyle();

	String asString();

	@Override
	default String getString() {
		return StringVisitable.super.getString();
	}

	default String asTruncatedString(int length) {
		StringBuilder stringBuilder = new StringBuilder();
		this.visit(string -> {
			int j = length - stringBuilder.length();
			if (j <= 0) {
				return TERMINATE_VISIT;
			} else {
				stringBuilder.append(string.length() <= j ? string : string.substring(0, j));
				return Optional.empty();
			}
		});
		return stringBuilder.toString();
	}

	List<Text> getSiblings();

	MutableText copy();

	MutableText shallowCopy();

	OrderedText asOrderedText();

	@Override
	default <T> Optional<T> visit(StringVisitable.StyledVisitor<T> styledVisitor, Style style) {
		Style style2 = this.getStyle().withParent(style);
		Optional<T> optional = this.visitSelf(styledVisitor, style2);
		if (optional.isPresent()) {
			return optional;
		} else {
			for (Text text : this.getSiblings()) {
				Optional<T> optional2 = text.visit(styledVisitor, style2);
				if (optional2.isPresent()) {
					return optional2;
				}
			}

			return Optional.empty();
		}
	}

	@Override
	default <T> Optional<T> visit(StringVisitable.Visitor<T> visitor) {
		Optional<T> optional = this.visitSelf(visitor);
		if (optional.isPresent()) {
			return optional;
		} else {
			for (Text text : this.getSiblings()) {
				Optional<T> optional2 = text.visit(visitor);
				if (optional2.isPresent()) {
					return optional2;
				}
			}

			return Optional.empty();
		}
	}

	default <T> Optional<T> visitSelf(StringVisitable.StyledVisitor<T> visitor, Style style) {
		return visitor.accept(style, this.asString());
	}

	default <T> Optional<T> visitSelf(StringVisitable.Visitor<T> visitor) {
		return visitor.accept(this.asString());
	}

	default List<Text> getWithStyle(Style style) {
		List<Text> list = Lists.newArrayList();
		this.visit((styleOverride, text) -> {
			if (!text.isEmpty()) {
				list.add(new LiteralText(text).fillStyle(styleOverride));
			}

			return Optional.empty();
		}, style);
		return list;
	}

	static Text of(@Nullable String string) {
		return (Text)(string != null ? new LiteralText(string) : LiteralText.EMPTY);
	}

	public static class Serializer implements JsonDeserializer<MutableText>, JsonSerializer<Text> {
		private static final Gson GSON = Util.make(() -> {
			GsonBuilder gsonBuilder = new GsonBuilder();
			gsonBuilder.disableHtmlEscaping();
			gsonBuilder.registerTypeHierarchyAdapter(Text.class, new Text.Serializer());
			gsonBuilder.registerTypeHierarchyAdapter(Style.class, new Style.Serializer());
			gsonBuilder.registerTypeAdapterFactory(new LowercaseEnumTypeAdapterFactory());
			return gsonBuilder.create();
		});
		private static final Field JSON_READER_POS = Util.make(() -> {
			try {
				new JsonReader(new StringReader(""));
				Field field = JsonReader.class.getDeclaredField("pos");
				field.setAccessible(true);
				return field;
			} catch (NoSuchFieldException var1) {
				throw new IllegalStateException("Couldn't get field 'pos' for JsonReader", var1);
			}
		});
		private static final Field JSON_READER_LINE_START = Util.make(() -> {
			try {
				new JsonReader(new StringReader(""));
				Field field = JsonReader.class.getDeclaredField("lineStart");
				field.setAccessible(true);
				return field;
			} catch (NoSuchFieldException var1) {
				throw new IllegalStateException("Couldn't get field 'lineStart' for JsonReader", var1);
			}
		});

		public MutableText deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
			if (jsonElement.isJsonPrimitive()) {
				return new LiteralText(jsonElement.getAsString());
			} else if (!jsonElement.isJsonObject()) {
				if (jsonElement.isJsonArray()) {
					JsonArray jsonArray3 = jsonElement.getAsJsonArray();
					MutableText mutableText13 = null;

					for (JsonElement jsonElement2 : jsonArray3) {
						MutableText mutableText14 = this.deserialize(jsonElement2, jsonElement2.getClass(), jsonDeserializationContext);
						if (mutableText13 == null) {
							mutableText13 = mutableText14;
						} else {
							mutableText13.append(mutableText14);
						}
					}

					return mutableText13;
				} else {
					throw new JsonParseException("Don't know how to turn " + jsonElement + " into a Component");
				}
			} else {
				JsonObject jsonObject = jsonElement.getAsJsonObject();
				MutableText mutableText;
				if (jsonObject.has("text")) {
					mutableText = new LiteralText(JsonHelper.getString(jsonObject, "text"));
				} else if (jsonObject.has("translate")) {
					String string = JsonHelper.getString(jsonObject, "translate");
					if (jsonObject.has("with")) {
						JsonArray jsonArray = JsonHelper.getArray(jsonObject, "with");
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

						mutableText = new TranslatableText(string, objects);
					} else {
						mutableText = new TranslatableText(string);
					}
				} else if (jsonObject.has("score")) {
					JsonObject jsonObject2 = JsonHelper.getObject(jsonObject, "score");
					if (!jsonObject2.has("name") || !jsonObject2.has("objective")) {
						throw new JsonParseException("A score component needs a least a name and an objective");
					}

					mutableText = new ScoreText(JsonHelper.getString(jsonObject2, "name"), JsonHelper.getString(jsonObject2, "objective"));
				} else if (jsonObject.has("selector")) {
					Optional<Text> optional = this.getSeparator(type, jsonDeserializationContext, jsonObject);
					mutableText = new SelectorText(JsonHelper.getString(jsonObject, "selector"), optional);
				} else if (jsonObject.has("keybind")) {
					mutableText = new KeybindText(JsonHelper.getString(jsonObject, "keybind"));
				} else {
					if (!jsonObject.has("nbt")) {
						throw new JsonParseException("Don't know how to turn " + jsonElement + " into a Component");
					}

					String string2 = JsonHelper.getString(jsonObject, "nbt");
					Optional<Text> optional2 = this.getSeparator(type, jsonDeserializationContext, jsonObject);
					boolean bl = JsonHelper.getBoolean(jsonObject, "interpret", false);
					if (jsonObject.has("block")) {
						mutableText = new NbtText.BlockNbtText(string2, bl, JsonHelper.getString(jsonObject, "block"), optional2);
					} else if (jsonObject.has("entity")) {
						mutableText = new NbtText.EntityNbtText(string2, bl, JsonHelper.getString(jsonObject, "entity"), optional2);
					} else {
						if (!jsonObject.has("storage")) {
							throw new JsonParseException("Don't know how to turn " + jsonElement + " into a Component");
						}

						mutableText = new NbtText.StorageNbtText(string2, bl, new Identifier(JsonHelper.getString(jsonObject, "storage")), optional2);
					}
				}

				if (jsonObject.has("extra")) {
					JsonArray jsonArray2 = JsonHelper.getArray(jsonObject, "extra");
					if (jsonArray2.size() <= 0) {
						throw new JsonParseException("Unexpected empty array of components");
					}

					for (int j = 0; j < jsonArray2.size(); j++) {
						mutableText.append(this.deserialize(jsonArray2.get(j), type, jsonDeserializationContext));
					}
				}

				mutableText.setStyle((Style)jsonDeserializationContext.deserialize(jsonElement, Style.class));
				return mutableText;
			}
		}

		private Optional<Text> getSeparator(Type type, JsonDeserializationContext context, JsonObject json) {
			return json.has("separator") ? Optional.of(this.deserialize(json.get("separator"), type, context)) : Optional.empty();
		}

		private void addStyle(Style style, JsonObject json, JsonSerializationContext context) {
			JsonElement jsonElement = context.serialize(style);
			if (jsonElement.isJsonObject()) {
				JsonObject jsonObject = (JsonObject)jsonElement;

				for (Entry<String, JsonElement> entry : jsonObject.entrySet()) {
					json.add((String)entry.getKey(), (JsonElement)entry.getValue());
				}
			}
		}

		public JsonElement serialize(Text text, Type type, JsonSerializationContext jsonSerializationContext) {
			JsonObject jsonObject = new JsonObject();
			if (!text.getStyle().isEmpty()) {
				this.addStyle(text.getStyle(), jsonObject, jsonSerializationContext);
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
			} else if (text instanceof TranslatableText translatableText) {
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
			} else if (text instanceof ScoreText scoreText) {
				JsonObject jsonObject2 = new JsonObject();
				jsonObject2.addProperty("name", scoreText.getName());
				jsonObject2.addProperty("objective", scoreText.getObjective());
				jsonObject.add("score", jsonObject2);
			} else if (text instanceof SelectorText selectorText) {
				jsonObject.addProperty("selector", selectorText.getPattern());
				this.addSeparator(jsonSerializationContext, jsonObject, selectorText.getSeparator());
			} else if (text instanceof KeybindText keybindText) {
				jsonObject.addProperty("keybind", keybindText.getKey());
			} else {
				if (!(text instanceof NbtText nbtText)) {
					throw new IllegalArgumentException("Don't know how to serialize " + text + " as a Component");
				}

				jsonObject.addProperty("nbt", nbtText.getPath());
				jsonObject.addProperty("interpret", nbtText.shouldInterpret());
				this.addSeparator(jsonSerializationContext, jsonObject, nbtText.separator);
				if (text instanceof NbtText.BlockNbtText blockNbtText) {
					jsonObject.addProperty("block", blockNbtText.getPos());
				} else if (text instanceof NbtText.EntityNbtText entityNbtText) {
					jsonObject.addProperty("entity", entityNbtText.getSelector());
				} else {
					if (!(text instanceof NbtText.StorageNbtText storageNbtText)) {
						throw new IllegalArgumentException("Don't know how to serialize " + text + " as a Component");
					}

					jsonObject.addProperty("storage", storageNbtText.getId().toString());
				}
			}

			return jsonObject;
		}

		private void addSeparator(JsonSerializationContext context, JsonObject json, Optional<Text> separator) {
			separator.ifPresent(separatorx -> json.add("separator", this.serialize(separatorx, separatorx.getClass(), context)));
		}

		public static String toJson(Text text) {
			return GSON.toJson(text);
		}

		public static JsonElement toJsonTree(Text text) {
			return GSON.toJsonTree(text);
		}

		@Nullable
		public static MutableText fromJson(String json) {
			return JsonHelper.deserialize(GSON, json, MutableText.class, false);
		}

		@Nullable
		public static MutableText fromJson(JsonElement json) {
			return (MutableText)GSON.fromJson(json, MutableText.class);
		}

		@Nullable
		public static MutableText fromLenientJson(String json) {
			return JsonHelper.deserialize(GSON, json, MutableText.class, true);
		}

		public static MutableText fromJson(com.mojang.brigadier.StringReader reader) {
			try {
				JsonReader jsonReader = new JsonReader(new StringReader(reader.getRemaining()));
				jsonReader.setLenient(false);
				MutableText mutableText = (MutableText)GSON.getAdapter(MutableText.class).read(jsonReader);
				reader.setCursor(reader.getCursor() + getPosition(jsonReader));
				return mutableText;
			} catch (StackOverflowError | IOException var3) {
				throw new JsonParseException(var3);
			}
		}

		private static int getPosition(JsonReader reader) {
			try {
				return JSON_READER_POS.getInt(reader) - JSON_READER_LINE_START.getInt(reader) + 1;
			} catch (IllegalAccessException var2) {
				throw new IllegalStateException("Couldn't read position of JsonReader", var2);
			}
		}
	}
}
