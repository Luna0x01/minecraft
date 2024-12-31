package net.minecraft;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.google.gson.JsonSyntaxException;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.Random;
import net.minecraft.item.ItemStack;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.math.MathHelper;

public abstract class class_2778 {
	protected final int field_13182;
	protected final int field_13183;
	protected final class_2816[] field_13184;

	protected class_2778(int i, int j, class_2816[] args) {
		this.field_13182 = i;
		this.field_13183 = j;
		this.field_13184 = args;
	}

	public int method_11974(float f) {
		return Math.max(MathHelper.floor((float)this.field_13182 + (float)this.field_13183 * f), 0);
	}

	public abstract void method_11976(Collection<ItemStack> collection, Random random, class_2782 arg);

	protected abstract void method_11975(JsonObject jsonObject, JsonSerializationContext jsonSerializationContext);

	public static class class_2779 implements JsonDeserializer<class_2778>, JsonSerializer<class_2778> {
		public class_2778 deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
			JsonObject jsonObject = JsonHelper.asObject(jsonElement, "loot item");
			String string = JsonHelper.getString(jsonObject, "type");
			int i = JsonHelper.getInt(jsonObject, "weight", 1);
			int j = JsonHelper.getInt(jsonObject, "quality", 0);
			class_2816[] lvs;
			if (jsonObject.has("conditions")) {
				lvs = JsonHelper.deserialize(jsonObject, "conditions", jsonDeserializationContext, class_2816[].class);
			} else {
				lvs = new class_2816[0];
			}

			if ("item".equals(string)) {
				return class_2775.method_11965(jsonObject, jsonDeserializationContext, i, j, lvs);
			} else if ("loot_table".equals(string)) {
				return class_2786.method_12003(jsonObject, jsonDeserializationContext, i, j, lvs);
			} else if ("empty".equals(string)) {
				return class_2774.method_11964(jsonObject, jsonDeserializationContext, i, j, lvs);
			} else {
				throw new JsonSyntaxException("Unknown loot entry type '" + string + "'");
			}
		}

		public JsonElement serialize(class_2778 arg, Type type, JsonSerializationContext jsonSerializationContext) {
			JsonObject jsonObject = new JsonObject();
			jsonObject.addProperty("weight", arg.field_13182);
			jsonObject.addProperty("quality", arg.field_13183);
			if (arg.field_13184.length > 0) {
				jsonObject.add("conditions", jsonSerializationContext.serialize(arg.field_13184));
			}

			if (arg instanceof class_2775) {
				jsonObject.addProperty("type", "item");
			} else if (arg instanceof class_2786) {
				jsonObject.addProperty("type", "item");
			} else {
				if (!(arg instanceof class_2774)) {
					throw new IllegalArgumentException("Don't know how to serialize " + arg);
				}

				jsonObject.addProperty("type", "empty");
			}

			arg.method_11975(jsonObject, jsonSerializationContext);
			return jsonObject;
		}
	}
}
