package net.minecraft;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import java.util.Random;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;

public class class_2793 extends class_2795 {
	private final class_2789 field_13216;
	private final boolean field_13217;

	public class_2793(class_2816[] args, class_2789 arg, boolean bl) {
		super(args);
		this.field_13216 = arg;
		this.field_13217 = bl;
	}

	@Override
	public ItemStack method_12029(ItemStack itemStack, Random random, class_2782 arg) {
		return EnchantmentHelper.enchant(random, itemStack, this.field_13216.method_12015(random), this.field_13217);
	}

	public static class class_2794 extends class_2795.class_2796<class_2793> {
		public class_2794() {
			super(new Identifier("enchant_with_levels"), class_2793.class);
		}

		public void method_12031(JsonObject jsonObject, class_2793 arg, JsonSerializationContext jsonSerializationContext) {
			jsonObject.add("levels", jsonSerializationContext.serialize(arg.field_13216));
			jsonObject.addProperty("treasure", arg.field_13217);
		}

		public class_2793 method_12033(JsonObject jsonObject, JsonDeserializationContext jsonDeserializationContext, class_2816[] args) {
			class_2789 lv = JsonHelper.deserialize(jsonObject, "levels", jsonDeserializationContext, class_2789.class);
			boolean bl = JsonHelper.getBoolean(jsonObject, "treasure", false);
			return new class_2793(args, lv, bl);
		}
	}
}
