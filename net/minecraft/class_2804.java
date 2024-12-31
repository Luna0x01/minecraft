package net.minecraft;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import java.util.Random;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;

public class class_2804 extends class_2795 {
	private final class_2789 field_13232;

	public class_2804(class_2816[] args, class_2789 arg) {
		super(args);
		this.field_13232 = arg;
	}

	@Override
	public ItemStack method_12029(ItemStack itemStack, Random random, class_2782 arg) {
		itemStack.setCount(this.field_13232.method_12015(random));
		return itemStack;
	}

	public static class class_2805 extends class_2795.class_2796<class_2804> {
		protected class_2805() {
			super(new Identifier("set_count"), class_2804.class);
		}

		public void method_12031(JsonObject jsonObject, class_2804 arg, JsonSerializationContext jsonSerializationContext) {
			jsonObject.add("count", jsonSerializationContext.serialize(arg.field_13232));
		}

		public class_2804 method_12033(JsonObject jsonObject, JsonDeserializationContext jsonDeserializationContext, class_2816[] args) {
			return new class_2804(args, JsonHelper.deserialize(jsonObject, "count", jsonDeserializationContext, class_2789.class));
		}
	}
}
