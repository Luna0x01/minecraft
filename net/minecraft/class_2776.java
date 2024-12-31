package net.minecraft;

import com.google.common.collect.Lists;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.List;
import java.util.Random;
import net.minecraft.item.ItemStack;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.math.MathHelper;
import org.apache.commons.lang3.ArrayUtils;

public class class_2776 {
	private final class_2778[] field_13178;
	private final class_2816[] field_13179;
	private final class_2789 field_13180;
	private final class_2789 field_13181;

	public class_2776(class_2778[] args, class_2816[] args2, class_2789 arg, class_2789 arg2) {
		this.field_13178 = args;
		this.field_13179 = args2;
		this.field_13180 = arg;
		this.field_13181 = arg2;
	}

	protected void method_11967(Collection<ItemStack> collection, Random random, class_2782 arg) {
		List<class_2778> list = Lists.newArrayList();
		int i = 0;

		for (class_2778 lv : this.field_13178) {
			if (class_2818.method_12082(lv.field_13184, random, arg)) {
				int j = lv.method_11974(arg.method_11993());
				if (j > 0) {
					list.add(lv);
					i += j;
				}
			}
		}

		if (i != 0 && !list.isEmpty()) {
			int k = random.nextInt(i);

			for (class_2778 lv2 : list) {
				k -= lv2.method_11974(arg.method_11993());
				if (k < 0) {
					lv2.method_11976(collection, random, arg);
					return;
				}
			}
		}
	}

	public void method_11969(Collection<ItemStack> collection, Random random, class_2782 arg) {
		if (class_2818.method_12082(this.field_13179, random, arg)) {
			int i = this.field_13180.method_12015(random) + MathHelper.floor(this.field_13181.method_12018(random) * arg.method_11993());

			for (int j = 0; j < i; j++) {
				this.method_11967(collection, random, arg);
			}
		}
	}

	public static class class_2777 implements JsonDeserializer<class_2776>, JsonSerializer<class_2776> {
		public class_2776 deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
			JsonObject jsonObject = JsonHelper.asObject(jsonElement, "loot pool");
			class_2778[] lvs = JsonHelper.deserialize(jsonObject, "entries", jsonDeserializationContext, class_2778[].class);
			class_2816[] lvs2 = JsonHelper.deserialize(jsonObject, "conditions", new class_2816[0], jsonDeserializationContext, class_2816[].class);
			class_2789 lv = JsonHelper.deserialize(jsonObject, "rolls", jsonDeserializationContext, class_2789.class);
			class_2789 lv2 = JsonHelper.deserialize(jsonObject, "bonus_rolls", new class_2789(0.0F, 0.0F), jsonDeserializationContext, class_2789.class);
			return new class_2776(lvs, lvs2, lv, lv2);
		}

		public JsonElement serialize(class_2776 arg, Type type, JsonSerializationContext jsonSerializationContext) {
			JsonObject jsonObject = new JsonObject();
			jsonObject.add("entries", jsonSerializationContext.serialize(arg.field_13178));
			jsonObject.add("rolls", jsonSerializationContext.serialize(arg.field_13180));
			if (arg.field_13181.getMin() != 0.0F && arg.field_13181.getMax() != 0.0F) {
				jsonObject.add("bonus_rolls", jsonSerializationContext.serialize(arg.field_13181));
			}

			if (!ArrayUtils.isEmpty(arg.field_13179)) {
				jsonObject.add("conditions", jsonSerializationContext.serialize(arg.field_13179));
			}

			return jsonObject;
		}
	}
}
