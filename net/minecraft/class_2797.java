package net.minecraft;

import com.google.common.collect.Maps;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.google.gson.JsonSyntaxException;
import java.lang.reflect.Type;
import java.util.Map;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;

public class class_2797 {
	private static final Map<Identifier, class_2795.class_2796<?>> field_13221 = Maps.newHashMap();
	private static final Map<Class<? extends class_2795>, class_2795.class_2796<?>> field_13222 = Maps.newHashMap();

	public static <T extends class_2795> void method_12034(class_2795.class_2796<? extends T> arg) {
		Identifier identifier = arg.method_12030();
		Class<T> class_ = (Class<T>)arg.method_12032();
		if (field_13221.containsKey(identifier)) {
			throw new IllegalArgumentException("Can't re-register item function name " + identifier);
		} else if (field_13222.containsKey(class_)) {
			throw new IllegalArgumentException("Can't re-register item function class " + class_.getName());
		} else {
			field_13221.put(identifier, arg);
			field_13222.put(class_, arg);
		}
	}

	public static class_2795.class_2796<?> method_12036(Identifier identifier) {
		class_2795.class_2796<?> lv = (class_2795.class_2796<?>)field_13221.get(identifier);
		if (lv == null) {
			throw new IllegalArgumentException("Unknown loot item function '" + identifier + "'");
		} else {
			return lv;
		}
	}

	public static <T extends class_2795> class_2795.class_2796<T> method_12035(T arg) {
		class_2795.class_2796<T> lv = (class_2795.class_2796<T>)field_13222.get(arg.getClass());
		if (lv == null) {
			throw new IllegalArgumentException("Unknown loot item function " + arg);
		} else {
			return lv;
		}
	}

	static {
		method_12034(new class_2804.class_2805());
		method_12034(new class_2808.class_2809());
		method_12034(new class_2793.class_2794());
		method_12034(new class_2791.class_2792());
		method_12034(new class_2810.class_2811());
		method_12034(new class_2812.class_2813());
		method_12034(new class_2799.class_2800());
		method_12034(new class_2806.class_2807());
		method_12034(new class_2801.class_2803());
	}

	public static class class_2798 implements JsonDeserializer<class_2795>, JsonSerializer<class_2795> {
		public class_2795 deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
			JsonObject jsonObject = JsonHelper.asObject(jsonElement, "function");
			Identifier identifier = new Identifier(JsonHelper.getString(jsonObject, "function"));

			class_2795.class_2796<?> lv;
			try {
				lv = class_2797.method_12036(identifier);
			} catch (IllegalArgumentException var8) {
				throw new JsonSyntaxException("Unknown function '" + identifier + "'");
			}

			return lv.method_12033(
				jsonObject, jsonDeserializationContext, JsonHelper.deserialize(jsonObject, "conditions", new class_2816[0], jsonDeserializationContext, class_2816[].class)
			);
		}

		public JsonElement serialize(class_2795 arg, Type type, JsonSerializationContext jsonSerializationContext) {
			class_2795.class_2796<class_2795> lv = class_2797.method_12035(arg);
			JsonObject jsonObject = new JsonObject();
			lv.method_12031(jsonObject, arg, jsonSerializationContext);
			jsonObject.addProperty("function", lv.method_12030().toString());
			if (arg.method_12028() != null && arg.method_12028().length > 0) {
				jsonObject.add("conditions", jsonSerializationContext.serialize(arg.method_12028()));
			}

			return jsonObject;
		}
	}
}
