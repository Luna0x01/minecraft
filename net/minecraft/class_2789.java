package net.minecraft;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import java.lang.reflect.Type;
import java.util.Random;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.math.MathHelper;

public class class_2789 {
	private final float min;
	private final float max;

	public class_2789(float f, float g) {
		this.min = f;
		this.max = g;
	}

	public class_2789(float f) {
		this.min = f;
		this.max = f;
	}

	public float getMin() {
		return this.min;
	}

	public float getMax() {
		return this.max;
	}

	public int method_12015(Random random) {
		return MathHelper.nextInt(random, MathHelper.floor(this.min), MathHelper.floor(this.max));
	}

	public float method_12018(Random random) {
		return MathHelper.nextFloat(random, this.min, this.max);
	}

	public boolean inRangeInclusive(int value) {
		return (float)value <= this.max && (float)value >= this.min;
	}

	public static class class_2790 implements JsonDeserializer<class_2789>, JsonSerializer<class_2789> {
		public class_2789 deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
			if (JsonHelper.isNumber(jsonElement)) {
				return new class_2789(JsonHelper.asFloat(jsonElement, "value"));
			} else {
				JsonObject jsonObject = JsonHelper.asObject(jsonElement, "value");
				float f = JsonHelper.getFloat(jsonObject, "min");
				float g = JsonHelper.getFloat(jsonObject, "max");
				return new class_2789(f, g);
			}
		}

		public JsonElement serialize(class_2789 arg, Type type, JsonSerializationContext jsonSerializationContext) {
			if (arg.min == arg.max) {
				return new JsonPrimitive(arg.min);
			} else {
				JsonObject jsonObject = new JsonObject();
				jsonObject.addProperty("min", arg.min);
				jsonObject.addProperty("max", arg.max);
				return jsonObject;
			}
		}
	}
}
