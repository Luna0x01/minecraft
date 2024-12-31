package net.minecraft.client;

import com.google.common.collect.Lists;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import java.lang.reflect.Type;
import java.util.List;

public class class_2877 {
	private final List<Variant> field_13572;

	public class_2877(List<Variant> list) {
		this.field_13572 = list;
	}

	public List<Variant> method_12375() {
		return this.field_13572;
	}

	public boolean equals(Object object) {
		if (this == object) {
			return true;
		} else if (object instanceof class_2877) {
			class_2877 lv = (class_2877)object;
			return this.field_13572.equals(lv.field_13572);
		} else {
			return false;
		}
	}

	public int hashCode() {
		return this.field_13572.hashCode();
	}

	public static class class_2878 implements JsonDeserializer<class_2877> {
		public class_2877 deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
			List<Variant> list = Lists.newArrayList();
			if (jsonElement.isJsonArray()) {
				JsonArray jsonArray = jsonElement.getAsJsonArray();
				if (jsonArray.size() == 0) {
					throw new JsonParseException("Empty variant array");
				}

				for (JsonElement jsonElement2 : jsonArray) {
					list.add((Variant)jsonDeserializationContext.deserialize(jsonElement2, Variant.class));
				}
			} else {
				list.add((Variant)jsonDeserializationContext.deserialize(jsonElement, Variant.class));
			}

			return new class_2877(list);
		}
	}
}
