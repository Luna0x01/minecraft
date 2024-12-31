package net.minecraft;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import java.util.Random;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;

public class class_2824 implements class_2816 {
	private final float field_13248;

	public class_2824(float f) {
		this.field_13248 = f;
	}

	@Override
	public boolean method_12074(Random random, class_2782 arg) {
		return random.nextFloat() < this.field_13248;
	}

	public static class class_2825 extends class_2816.class_2817<class_2824> {
		protected class_2825() {
			super(new Identifier("random_chance"), class_2824.class);
		}

		public void method_12076(JsonObject jsonObject, class_2824 arg, JsonSerializationContext jsonSerializationContext) {
			jsonObject.addProperty("chance", arg.field_13248);
		}

		public class_2824 method_12078(JsonObject jsonObject, JsonDeserializationContext jsonDeserializationContext) {
			return new class_2824(JsonHelper.getFloat(jsonObject, "chance"));
		}
	}
}
