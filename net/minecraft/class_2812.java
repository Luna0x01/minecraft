package net.minecraft;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import java.util.Random;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.SmeltingRecipeRegistry;
import net.minecraft.util.Identifier;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class class_2812 extends class_2795 {
	private static final Logger field_13238 = LogManager.getLogger();

	public class_2812(class_2816[] args) {
		super(args);
	}

	@Override
	public ItemStack method_12029(ItemStack itemStack, Random random, class_2782 arg) {
		ItemStack itemStack2 = SmeltingRecipeRegistry.getInstance().getResult(itemStack);
		if (itemStack2 == null) {
			field_13238.warn("Couldn't smelt " + itemStack + " because there is no smelting recipe");
			return itemStack;
		} else {
			ItemStack itemStack3 = itemStack2.copy();
			itemStack3.count = itemStack.count;
			return itemStack3;
		}
	}

	public static class class_2813 extends class_2795.class_2796<class_2812> {
		protected class_2813() {
			super(new Identifier("furnace_smelt"), class_2812.class);
		}

		public void method_12031(JsonObject jsonObject, class_2812 arg, JsonSerializationContext jsonSerializationContext) {
		}

		public class_2812 method_12033(JsonObject jsonObject, JsonDeserializationContext jsonDeserializationContext, class_2816[] args) {
			return new class_2812(args);
		}
	}
}
