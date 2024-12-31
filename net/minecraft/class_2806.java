package net.minecraft;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import java.util.Random;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.math.MathHelper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class class_2806 extends class_2795 {
	private static final Logger field_13233 = LogManager.getLogger();
	private final class_2789 field_13234;

	public class_2806(class_2816[] args, class_2789 arg) {
		super(args);
		this.field_13234 = arg;
	}

	@Override
	public ItemStack method_12029(ItemStack itemStack, Random random, class_2782 arg) {
		if (itemStack.isDamageable()) {
			float f = 1.0F - this.field_13234.method_12018(random);
			itemStack.setDamage(MathHelper.floor(f * (float)itemStack.getMaxDamage()));
		} else {
			field_13233.warn("Couldn't set damage of loot item {}", itemStack);
		}

		return itemStack;
	}

	public static class class_2807 extends class_2795.class_2796<class_2806> {
		protected class_2807() {
			super(new Identifier("set_damage"), class_2806.class);
		}

		public void method_12031(JsonObject jsonObject, class_2806 arg, JsonSerializationContext jsonSerializationContext) {
			jsonObject.add("damage", jsonSerializationContext.serialize(arg.field_13234));
		}

		public class_2806 method_12033(JsonObject jsonObject, JsonDeserializationContext jsonDeserializationContext, class_2816[] args) {
			return new class_2806(args, JsonHelper.deserialize(jsonObject, "damage", jsonDeserializationContext, class_2789.class));
		}
	}
}
