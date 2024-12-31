package net.minecraft;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import java.util.Random;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;

public class class_2799 extends class_2795 {
	private final class_2789 field_13223;

	public class_2799(class_2816[] args, class_2789 arg) {
		super(args);
		this.field_13223 = arg;
	}

	@Override
	public ItemStack method_12029(ItemStack itemStack, Random random, class_2782 arg) {
		Entity entity = arg.method_11991();
		if (entity instanceof LivingEntity) {
			int i = EnchantmentHelper.getLooting((LivingEntity)entity);
			if (i == 0) {
				return itemStack;
			}

			float f = (float)i * this.field_13223.method_12018(random);
			itemStack.count = itemStack.count + Math.round(f);
		}

		return itemStack;
	}

	public static class class_2800 extends class_2795.class_2796<class_2799> {
		protected class_2800() {
			super(new Identifier("looting_enchant"), class_2799.class);
		}

		public void method_12031(JsonObject jsonObject, class_2799 arg, JsonSerializationContext jsonSerializationContext) {
			jsonObject.add("count", jsonSerializationContext.serialize(arg.field_13223));
		}

		public class_2799 method_12033(JsonObject jsonObject, JsonDeserializationContext jsonDeserializationContext, class_2816[] args) {
			return new class_2799(args, JsonHelper.deserialize(jsonObject, "count", jsonDeserializationContext, class_2789.class));
		}
	}
}
