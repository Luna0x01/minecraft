package net.minecraft;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import java.util.Random;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;

public class class_2826 implements class_2816 {
	private final float field_13249;
	private final float field_13250;

	public class_2826(float f, float g) {
		this.field_13249 = f;
		this.field_13250 = g;
	}

	@Override
	public boolean method_12074(Random random, class_2782 arg) {
		int i = 0;
		if (arg.method_11991() instanceof LivingEntity) {
			i = EnchantmentHelper.getLooting((LivingEntity)arg.method_11991());
		}

		return random.nextFloat() < this.field_13249 + (float)i * this.field_13250;
	}

	public static class class_2827 extends class_2816.class_2817<class_2826> {
		protected class_2827() {
			super(new Identifier("random_chance_with_looting"), class_2826.class);
		}

		public void method_12076(JsonObject jsonObject, class_2826 arg, JsonSerializationContext jsonSerializationContext) {
			jsonObject.addProperty("chance", arg.field_13249);
			jsonObject.addProperty("looting_multiplier", arg.field_13250);
		}

		public class_2826 method_12078(JsonObject jsonObject, JsonDeserializationContext jsonDeserializationContext) {
			return new class_2826(JsonHelper.getFloat(jsonObject, "chance"), JsonHelper.getFloat(jsonObject, "looting_multiplier"));
		}
	}
}
