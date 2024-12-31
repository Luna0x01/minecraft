package net.minecraft.client.render.model.json;

import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import java.lang.reflect.Type;
import net.minecraft.class_4306;
import net.minecraft.util.JsonHelper;

public class Transformation {
	public static final Transformation DEFAULT = new Transformation(new class_4306(), new class_4306(), new class_4306(1.0F, 1.0F, 1.0F));
	public final class_4306 field_20807;
	public final class_4306 field_20808;
	public final class_4306 field_20809;

	public Transformation(class_4306 arg, class_4306 arg2, class_4306 arg3) {
		this.field_20807 = new class_4306(arg);
		this.field_20808 = new class_4306(arg2);
		this.field_20809 = new class_4306(arg3);
	}

	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		} else if (this.getClass() != obj.getClass()) {
			return false;
		} else {
			Transformation transformation = (Transformation)obj;
			return this.field_20807.equals(transformation.field_20807)
				&& this.field_20809.equals(transformation.field_20809)
				&& this.field_20808.equals(transformation.field_20808);
		}
	}

	public int hashCode() {
		int i = this.field_20807.hashCode();
		i = 31 * i + this.field_20808.hashCode();
		return 31 * i + this.field_20809.hashCode();
	}

	static class Deserializer implements JsonDeserializer<Transformation> {
		private static final class_4306 field_20810 = new class_4306(0.0F, 0.0F, 0.0F);
		private static final class_4306 field_20811 = new class_4306(0.0F, 0.0F, 0.0F);
		private static final class_4306 field_20812 = new class_4306(1.0F, 1.0F, 1.0F);

		public Transformation deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
			JsonObject jsonObject = jsonElement.getAsJsonObject();
			class_4306 lv = this.method_19256(jsonObject, "rotation", field_20810);
			class_4306 lv2 = this.method_19256(jsonObject, "translation", field_20811);
			lv2.method_19663(0.0625F);
			lv2.method_19664(-5.0F, 5.0F);
			class_4306 lv3 = this.method_19256(jsonObject, "scale", field_20812);
			lv3.method_19664(-4.0F, 4.0F);
			return new Transformation(lv, lv2, lv3);
		}

		private class_4306 method_19256(JsonObject jsonObject, String string, class_4306 arg) {
			if (!jsonObject.has(string)) {
				return arg;
			} else {
				JsonArray jsonArray = JsonHelper.getArray(jsonObject, string);
				if (jsonArray.size() != 3) {
					throw new JsonParseException("Expected 3 " + string + " values, found: " + jsonArray.size());
				} else {
					float[] fs = new float[3];

					for (int i = 0; i < fs.length; i++) {
						fs[i] = JsonHelper.asFloat(jsonArray.get(i), string + "[" + i + "]");
					}

					return new class_4306(fs[0], fs[1], fs[2]);
				}
			}
		}
	}
}
