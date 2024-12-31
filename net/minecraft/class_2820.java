package net.minecraft;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import java.util.Random;
import java.util.Set;
import java.util.Map.Entry;
import net.minecraft.entity.Entity;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;

public class class_2820 implements class_2816 {
	private final class_2829[] field_13245;
	private final class_2782.class_2784 field_13246;

	public class_2820(class_2829[] args, class_2782.class_2784 arg) {
		this.field_13245 = args;
		this.field_13246 = arg;
	}

	@Override
	public boolean method_12074(Random random, class_2782 arg) {
		Entity entity = arg.method_11988(this.field_13246);
		if (entity == null) {
			return false;
		} else {
			int i = 0;

			for (int j = this.field_13245.length; i < j; i++) {
				if (!this.field_13245[i].method_12102(random, entity)) {
					return false;
				}
			}

			return true;
		}
	}

	public static class class_2821 extends class_2816.class_2817<class_2820> {
		protected class_2821() {
			super(new Identifier("entity_properties"), class_2820.class);
		}

		public void method_12076(JsonObject jsonObject, class_2820 arg, JsonSerializationContext jsonSerializationContext) {
			JsonObject jsonObject2 = new JsonObject();

			for (class_2829 lv : arg.field_13245) {
				class_2829.class_2830<class_2829> lv2 = class_2828.method_12100(lv);
				jsonObject2.add(lv2.method_12103().toString(), lv2.method_12104(lv, jsonSerializationContext));
			}

			jsonObject.add("properties", jsonObject2);
			jsonObject.add("entity", jsonSerializationContext.serialize(arg.field_13246));
		}

		public class_2820 method_12078(JsonObject jsonObject, JsonDeserializationContext jsonDeserializationContext) {
			Set<Entry<String, JsonElement>> set = JsonHelper.getObject(jsonObject, "properties").entrySet();
			class_2829[] lvs = new class_2829[set.size()];
			int i = 0;

			for (Entry<String, JsonElement> entry : set) {
				lvs[i++] = class_2828.method_12101(new Identifier((String)entry.getKey())).method_12105((JsonElement)entry.getValue(), jsonDeserializationContext);
			}

			return new class_2820(lvs, JsonHelper.deserialize(jsonObject, "entity", jsonDeserializationContext, class_2782.class_2784.class));
		}
	}
}
