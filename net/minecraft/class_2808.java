package net.minecraft;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import java.util.Random;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class class_2808 extends class_2795 {
	private static final Logger LOGGER = LogManager.getLogger();
	private final class_2789 field_13236;

	public class_2808(class_2816[] args, class_2789 arg) {
		super(args);
		this.field_13236 = arg;
	}

	@Override
	public ItemStack method_12029(ItemStack itemStack, Random random, class_2782 arg) {
		if (itemStack.isDamageable()) {
			LOGGER.warn("Couldn't set data of loot item {}", itemStack);
		} else {
			itemStack.setDamage(this.field_13236.method_12015(random));
		}

		return itemStack;
	}

	public static class class_2809 extends class_2795.class_2796<class_2808> {
		protected class_2809() {
			super(new Identifier("set_data"), class_2808.class);
		}

		public void method_12031(JsonObject jsonObject, class_2808 arg, JsonSerializationContext jsonSerializationContext) {
			jsonObject.add("data", jsonSerializationContext.serialize(arg.field_13236));
		}

		public class_2808 method_12033(JsonObject jsonObject, JsonDeserializationContext jsonDeserializationContext, class_2816[] args) {
			return new class_2808(args, JsonHelper.deserialize(jsonObject, "data", jsonDeserializationContext, class_2789.class));
		}
	}
}
