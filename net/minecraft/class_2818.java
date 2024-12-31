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
import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;

public class class_2818 {
	private static final Map<Identifier, class_2816.class_2817<?>> field_13243 = Maps.newHashMap();
	private static final Map<Class<? extends class_2816>, class_2816.class_2817<?>> field_13244 = Maps.newHashMap();

	public static <T extends class_2816> void method_12079(class_2816.class_2817<? extends T> arg) {
		Identifier identifier = arg.method_12075();
		Class<T> class_ = (Class<T>)arg.method_12077();
		if (field_13243.containsKey(identifier)) {
			throw new IllegalArgumentException("Can't re-register item condition name " + identifier);
		} else if (field_13244.containsKey(class_)) {
			throw new IllegalArgumentException("Can't re-register item condition class " + class_.getName());
		} else {
			field_13243.put(identifier, arg);
			field_13244.put(class_, arg);
		}
	}

	public static boolean method_12082(@Nullable class_2816[] args, Random random, class_2782 arg) {
		if (args == null) {
			return true;
		} else {
			int i = 0;

			for (int j = args.length; i < j; i++) {
				class_2816 lv = args[i];
				if (!lv.method_12074(random, arg)) {
					return false;
				}
			}

			return true;
		}
	}

	public static class_2816.class_2817<?> method_12081(Identifier identifier) {
		class_2816.class_2817<?> lv = (class_2816.class_2817<?>)field_13243.get(identifier);
		if (lv == null) {
			throw new IllegalArgumentException("Unknown loot item condition '" + identifier + "'");
		} else {
			return lv;
		}
	}

	public static <T extends class_2816> class_2816.class_2817<T> method_12080(T arg) {
		class_2816.class_2817<T> lv = (class_2816.class_2817<T>)field_13244.get(arg.getClass());
		if (lv == null) {
			throw new IllegalArgumentException("Unknown loot item condition " + arg);
		} else {
			return lv;
		}
	}

	static {
		method_12079(new class_2824.class_2825());
		method_12079(new class_2826.class_2827());
		method_12079(new class_2820.class_2821());
		method_12079(new class_2822.class_2823());
		method_12079(new class_2814.class_2815());
	}

	public static class class_2819 implements JsonDeserializer<class_2816>, JsonSerializer<class_2816> {
		public class_2816 deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
			JsonObject jsonObject = JsonHelper.asObject(jsonElement, "condition");
			Identifier identifier = new Identifier(JsonHelper.getString(jsonObject, "condition"));

			class_2816.class_2817<?> lv;
			try {
				lv = class_2818.method_12081(identifier);
			} catch (IllegalArgumentException var8) {
				throw new JsonSyntaxException("Unknown condition '" + identifier + "'");
			}

			return lv.method_12078(jsonObject, jsonDeserializationContext);
		}

		public JsonElement serialize(class_2816 arg, Type type, JsonSerializationContext jsonSerializationContext) {
			class_2816.class_2817<class_2816> lv = class_2818.method_12080(arg);
			JsonObject jsonObject = new JsonObject();
			lv.method_12076(jsonObject, arg, jsonSerializationContext);
			jsonObject.addProperty("condition", lv.method_12075().toString());
			return jsonObject;
		}
	}
}
