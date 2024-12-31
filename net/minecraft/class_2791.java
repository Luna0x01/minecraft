package net.minecraft;

import com.google.common.collect.Lists;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSyntaxException;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentLevelEntry;
import net.minecraft.item.EnchantedBookItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.registry.Registry;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class class_2791 extends class_2795 {
	private static final Logger LOGGER = LogManager.getLogger();
	private final List<Enchantment> field_13215;

	public class_2791(class_2816[] args, @Nullable List<Enchantment> list) {
		super(args);
		this.field_13215 = list == null ? Collections.emptyList() : list;
	}

	@Override
	public ItemStack method_12029(ItemStack itemStack, Random random, class_2782 arg) {
		Enchantment enchantment2;
		if (this.field_13215.isEmpty()) {
			List<Enchantment> list = Lists.newArrayList();

			for (Enchantment enchantment : Registry.ENCHANTMENT) {
				if (itemStack.getItem() == Items.BOOK || enchantment.isAcceptableItem(itemStack)) {
					list.add(enchantment);
				}
			}

			if (list.isEmpty()) {
				LOGGER.warn("Couldn't find a compatible enchantment for {}", itemStack);
				return itemStack;
			}

			enchantment2 = (Enchantment)list.get(random.nextInt(list.size()));
		} else {
			enchantment2 = (Enchantment)this.field_13215.get(random.nextInt(this.field_13215.size()));
		}

		int i = MathHelper.nextInt(random, enchantment2.getMinimumLevel(), enchantment2.getMaximumLevel());
		if (itemStack.getItem() == Items.BOOK) {
			itemStack = new ItemStack(Items.ENCHANTED_BOOK);
			EnchantedBookItem.addEnchantment(itemStack, new EnchantmentLevelEntry(enchantment2, i));
		} else {
			itemStack.addEnchantment(enchantment2, i);
		}

		return itemStack;
	}

	public static class class_2792 extends class_2795.class_2796<class_2791> {
		public class_2792() {
			super(new Identifier("enchant_randomly"), class_2791.class);
		}

		public void method_12031(JsonObject jsonObject, class_2791 arg, JsonSerializationContext jsonSerializationContext) {
			if (!arg.field_13215.isEmpty()) {
				JsonArray jsonArray = new JsonArray();

				for (Enchantment enchantment : arg.field_13215) {
					Identifier identifier = Registry.ENCHANTMENT.getId(enchantment);
					if (identifier == null) {
						throw new IllegalArgumentException("Don't know how to serialize enchantment " + enchantment);
					}

					jsonArray.add(new JsonPrimitive(identifier.toString()));
				}

				jsonObject.add("enchantments", jsonArray);
			}
		}

		public class_2791 method_12033(JsonObject jsonObject, JsonDeserializationContext jsonDeserializationContext, class_2816[] args) {
			List<Enchantment> list = Lists.newArrayList();
			if (jsonObject.has("enchantments")) {
				for (JsonElement jsonElement : JsonHelper.getArray(jsonObject, "enchantments")) {
					String string = JsonHelper.asString(jsonElement, "enchantment");
					Enchantment enchantment = Registry.ENCHANTMENT.getByIdentifier(new Identifier(string));
					if (enchantment == null) {
						throw new JsonSyntaxException("Unknown enchantment '" + string + "'");
					}

					list.add(enchantment);
				}
			}

			return new class_2791(args, list);
		}
	}
}
