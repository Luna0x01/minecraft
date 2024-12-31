package net.minecraft;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.RecipeType;
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
		if (itemStack.isEmpty()) {
			return itemStack;
		} else {
			RecipeType recipeType = method_17996(arg, itemStack);
			if (recipeType != null) {
				ItemStack itemStack2 = recipeType.getOutput();
				if (!itemStack2.isEmpty()) {
					ItemStack itemStack3 = itemStack2.copy();
					itemStack3.setCount(itemStack.getCount());
					return itemStack3;
				}
			}

			field_13238.warn("Couldn't smelt {} because there is no smelting recipe", itemStack);
			return itemStack;
		}
	}

	@Nullable
	public static RecipeType method_17996(class_2782 arg, ItemStack itemStack) {
		for (RecipeType recipeType : arg.method_17980().method_16313().method_16208()) {
			if (recipeType instanceof class_3584 && recipeType.method_14252().get(0).test(itemStack)) {
				return recipeType;
			}
		}

		return null;
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
