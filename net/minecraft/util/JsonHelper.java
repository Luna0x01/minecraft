package net.minecraft.util;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSyntaxException;
import com.google.gson.stream.JsonReader;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import javax.annotation.Nullable;
import net.minecraft.item.Item;
import org.apache.commons.lang3.StringUtils;

public class JsonHelper {
	public static boolean hasString(JsonObject object, String element) {
		return !hasPrimitive(object, element) ? false : object.getAsJsonPrimitive(element).isString();
	}

	public static boolean isString(JsonElement element) {
		return !element.isJsonPrimitive() ? false : element.getAsJsonPrimitive().isString();
	}

	public static boolean isNumber(JsonElement element) {
		return !element.isJsonPrimitive() ? false : element.getAsJsonPrimitive().isNumber();
	}

	public static boolean hasBoolean(JsonObject object, String element) {
		return !hasPrimitive(object, element) ? false : object.getAsJsonPrimitive(element).isBoolean();
	}

	public static boolean hasArray(JsonObject object, String element) {
		return !hasElement(object, element) ? false : object.get(element).isJsonArray();
	}

	public static boolean hasPrimitive(JsonObject object, String element) {
		return !hasElement(object, element) ? false : object.get(element).isJsonPrimitive();
	}

	public static boolean hasElement(JsonObject object, String element) {
		return object == null ? false : object.get(element) != null;
	}

	public static String asString(JsonElement element, String name) {
		if (element.isJsonPrimitive()) {
			return element.getAsString();
		} else {
			throw new JsonSyntaxException("Expected " + name + " to be a string, was " + getType(element));
		}
	}

	public static String getString(JsonObject object, String element) {
		if (object.has(element)) {
			return asString(object.get(element), element);
		} else {
			throw new JsonSyntaxException("Missing " + element + ", expected to find a string");
		}
	}

	public static String getString(JsonObject object, String element, String defaultStr) {
		return object.has(element) ? asString(object.get(element), element) : defaultStr;
	}

	public static Item asItem(JsonElement element, String name) {
		if (element.isJsonPrimitive()) {
			String string = element.getAsString();
			Item item = Item.getFromId(string);
			if (item == null) {
				throw new JsonSyntaxException("Expected " + name + " to be an item, was unknown string '" + string + "'");
			} else {
				return item;
			}
		} else {
			throw new JsonSyntaxException("Expected " + name + " to be an item, was " + getType(element));
		}
	}

	public static Item getItem(JsonObject object, String element) {
		if (object.has(element)) {
			return asItem(object.get(element), element);
		} else {
			throw new JsonSyntaxException("Missing " + element + ", expected to find an item");
		}
	}

	public static boolean asBoolean(JsonElement element, String name) {
		if (element.isJsonPrimitive()) {
			return element.getAsBoolean();
		} else {
			throw new JsonSyntaxException("Expected " + name + " to be a Boolean, was " + getType(element));
		}
	}

	public static boolean getBoolean(JsonObject object, String element) {
		if (object.has(element)) {
			return asBoolean(object.get(element), element);
		} else {
			throw new JsonSyntaxException("Missing " + element + ", expected to find a Boolean");
		}
	}

	public static boolean getBoolean(JsonObject object, String element, boolean defaultBoolean) {
		return object.has(element) ? asBoolean(object.get(element), element) : defaultBoolean;
	}

	public static float asFloat(JsonElement element, String name) {
		if (element.isJsonPrimitive() && element.getAsJsonPrimitive().isNumber()) {
			return element.getAsFloat();
		} else {
			throw new JsonSyntaxException("Expected " + name + " to be a Float, was " + getType(element));
		}
	}

	public static float getFloat(JsonObject object, String element) {
		if (object.has(element)) {
			return asFloat(object.get(element), element);
		} else {
			throw new JsonSyntaxException("Missing " + element + ", expected to find a Float");
		}
	}

	public static float getFloat(JsonObject object, String element, float defaultFloat) {
		return object.has(element) ? asFloat(object.get(element), element) : defaultFloat;
	}

	public static int asInt(JsonElement element, String name) {
		if (element.isJsonPrimitive() && element.getAsJsonPrimitive().isNumber()) {
			return element.getAsInt();
		} else {
			throw new JsonSyntaxException("Expected " + name + " to be a Int, was " + getType(element));
		}
	}

	public static int getInt(JsonObject object, String element) {
		if (object.has(element)) {
			return asInt(object.get(element), element);
		} else {
			throw new JsonSyntaxException("Missing " + element + ", expected to find a Int");
		}
	}

	public static int getInt(JsonObject object, String element, int defaultInt) {
		return object.has(element) ? asInt(object.get(element), element) : defaultInt;
	}

	public static JsonObject asObject(JsonElement element, String name) {
		if (element.isJsonObject()) {
			return element.getAsJsonObject();
		} else {
			throw new JsonSyntaxException("Expected " + name + " to be a JsonObject, was " + getType(element));
		}
	}

	public static JsonObject getObject(JsonObject object, String element) {
		if (object.has(element)) {
			return asObject(object.get(element), element);
		} else {
			throw new JsonSyntaxException("Missing " + element + ", expected to find a JsonObject");
		}
	}

	public static JsonObject getObject(JsonObject object, String element, JsonObject defaultObject) {
		return object.has(element) ? asObject(object.get(element), element) : defaultObject;
	}

	public static JsonArray asArray(JsonElement element, String name) {
		if (element.isJsonArray()) {
			return element.getAsJsonArray();
		} else {
			throw new JsonSyntaxException("Expected " + name + " to be a JsonArray, was " + getType(element));
		}
	}

	public static JsonArray getArray(JsonObject object, String element) {
		if (object.has(element)) {
			return asArray(object.get(element), element);
		} else {
			throw new JsonSyntaxException("Missing " + element + ", expected to find a JsonArray");
		}
	}

	public static JsonArray getArray(JsonObject object, String name, @Nullable JsonArray defaultArray) {
		return object.has(name) ? asArray(object.get(name), name) : defaultArray;
	}

	public static <T> T deserialize(@Nullable JsonElement element, String name, JsonDeserializationContext context, Class<? extends T> type) {
		if (element != null) {
			return (T)context.deserialize(element, type);
		} else {
			throw new JsonSyntaxException("Missing " + name);
		}
	}

	public static <T> T deserialize(JsonObject object, String element, JsonDeserializationContext context, Class<? extends T> type) {
		if (object.has(element)) {
			return deserialize(object.get(element), element, context, type);
		} else {
			throw new JsonSyntaxException("Missing " + element);
		}
	}

	public static <T> T deserialize(JsonObject object, String element, T defaultValue, JsonDeserializationContext context, Class<? extends T> type) {
		return object.has(element) ? deserialize(object.get(element), element, context, type) : defaultValue;
	}

	public static String getType(JsonElement element) {
		String string = StringUtils.abbreviateMiddle(String.valueOf(element), "...", 10);
		if (element == null) {
			return "null (missing)";
		} else if (element.isJsonNull()) {
			return "null (json)";
		} else if (element.isJsonArray()) {
			return "an array (" + string + ")";
		} else if (element.isJsonObject()) {
			return "an object (" + string + ")";
		} else {
			if (element.isJsonPrimitive()) {
				JsonPrimitive jsonPrimitive = element.getAsJsonPrimitive();
				if (jsonPrimitive.isNumber()) {
					return "a number (" + string + ")";
				}

				if (jsonPrimitive.isBoolean()) {
					return "a boolean (" + string + ")";
				}
			}

			return string;
		}
	}

	public static <T> T deserialize(Gson gson, Reader reader, Class<T> class_, boolean lenient) {
		try {
			JsonReader jsonReader = new JsonReader(reader);
			jsonReader.setLenient(lenient);
			return (T)gson.getAdapter(class_).read(jsonReader);
		} catch (IOException var5) {
			throw new JsonParseException(var5);
		}
	}

	public static <T> T deserialize(Gson gson, String content, Class<T> class_) {
		return deserialize(gson, content, class_, false);
	}

	public static <T> T deserialize(Gson gson, String content, Class<T> class_, boolean lenient) {
		return deserialize(gson, new StringReader(content), class_, lenient);
	}
}
