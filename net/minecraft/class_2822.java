package net.minecraft;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import java.util.Random;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;

public class class_2822 implements class_2816 {
	private final boolean field_13247;

	public class_2822(boolean bl) {
		this.field_13247 = bl;
	}

	@Override
	public boolean method_12074(Random random, class_2782 arg) {
		boolean bl = arg.method_11989() != null;
		return bl == !this.field_13247;
	}

	public static class class_2823 extends class_2816.class_2817<class_2822> {
		protected class_2823() {
			super(new Identifier("killed_by_player"), class_2822.class);
		}

		public void method_12076(JsonObject jsonObject, class_2822 arg, JsonSerializationContext jsonSerializationContext) {
			jsonObject.addProperty("inverse", arg.field_13247);
		}

		public class_2822 method_12078(JsonObject jsonObject, JsonDeserializationContext jsonDeserializationContext) {
			return new class_2822(JsonHelper.getBoolean(jsonObject, "inverse", false));
		}
	}
}
