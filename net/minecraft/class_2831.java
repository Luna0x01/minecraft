package net.minecraft;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import java.util.Random;
import net.minecraft.entity.Entity;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;

public class class_2831 implements class_2829 {
	private final boolean field_13255;

	public class_2831(boolean bl) {
		this.field_13255 = bl;
	}

	@Override
	public boolean method_12102(Random random, Entity entity) {
		return entity.isOnFire() == this.field_13255;
	}

	public static class class_2832 extends class_2829.class_2830<class_2831> {
		protected class_2832() {
			super(new Identifier("on_fire"), class_2831.class);
		}

		public JsonElement method_12104(class_2831 arg, JsonSerializationContext jsonSerializationContext) {
			return new JsonPrimitive(arg.field_13255);
		}

		public class_2831 method_12105(JsonElement jsonElement, JsonDeserializationContext jsonDeserializationContext) {
			return new class_2831(JsonHelper.asBoolean(jsonElement, "on_fire"));
		}
	}
}
