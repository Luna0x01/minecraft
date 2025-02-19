package net.minecraft.loot.function;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSyntaxException;
import java.util.Collection;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentLevelEntry;
import net.minecraft.item.EnchantedBookItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.loot.condition.LootCondition;
import net.minecraft.loot.context.LootContext;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.registry.Registry;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class EnchantRandomlyLootFunction extends ConditionalLootFunction {
	private static final Logger LOGGER = LogManager.getLogger();
	final List<Enchantment> enchantments;

	EnchantRandomlyLootFunction(LootCondition[] lootConditions, Collection<Enchantment> collection) {
		super(lootConditions);
		this.enchantments = ImmutableList.copyOf(collection);
	}

	@Override
	public LootFunctionType getType() {
		return LootFunctionTypes.ENCHANT_RANDOMLY;
	}

	@Override
	public ItemStack process(ItemStack stack, LootContext context) {
		Random random = context.getRandom();
		Enchantment enchantment;
		if (this.enchantments.isEmpty()) {
			boolean bl = stack.isOf(Items.BOOK);
			List<Enchantment> list = (List<Enchantment>)Registry.ENCHANTMENT
				.stream()
				.filter(Enchantment::isAvailableForRandomSelection)
				.filter(enchantmentx -> bl || enchantmentx.isAcceptableItem(stack))
				.collect(Collectors.toList());
			if (list.isEmpty()) {
				LOGGER.warn("Couldn't find a compatible enchantment for {}", stack);
				return stack;
			}

			enchantment = (Enchantment)list.get(random.nextInt(list.size()));
		} else {
			enchantment = (Enchantment)this.enchantments.get(random.nextInt(this.enchantments.size()));
		}

		return addEnchantmentToStack(stack, enchantment, random);
	}

	private static ItemStack addEnchantmentToStack(ItemStack stack, Enchantment enchantment, Random random) {
		int i = MathHelper.nextInt(random, enchantment.getMinLevel(), enchantment.getMaxLevel());
		if (stack.isOf(Items.BOOK)) {
			stack = new ItemStack(Items.ENCHANTED_BOOK);
			EnchantedBookItem.addEnchantment(stack, new EnchantmentLevelEntry(enchantment, i));
		} else {
			stack.addEnchantment(enchantment, i);
		}

		return stack;
	}

	public static EnchantRandomlyLootFunction.Builder create() {
		return new EnchantRandomlyLootFunction.Builder();
	}

	public static ConditionalLootFunction.Builder<?> builder() {
		return builder(conditions -> new EnchantRandomlyLootFunction(conditions, ImmutableList.of()));
	}

	public static class Builder extends ConditionalLootFunction.Builder<EnchantRandomlyLootFunction.Builder> {
		private final Set<Enchantment> enchantments = Sets.newHashSet();

		protected EnchantRandomlyLootFunction.Builder getThisBuilder() {
			return this;
		}

		public EnchantRandomlyLootFunction.Builder add(Enchantment enchantment) {
			this.enchantments.add(enchantment);
			return this;
		}

		@Override
		public LootFunction build() {
			return new EnchantRandomlyLootFunction(this.getConditions(), this.enchantments);
		}
	}

	public static class Serializer extends ConditionalLootFunction.Serializer<EnchantRandomlyLootFunction> {
		public void toJson(JsonObject jsonObject, EnchantRandomlyLootFunction enchantRandomlyLootFunction, JsonSerializationContext jsonSerializationContext) {
			super.toJson(jsonObject, enchantRandomlyLootFunction, jsonSerializationContext);
			if (!enchantRandomlyLootFunction.enchantments.isEmpty()) {
				JsonArray jsonArray = new JsonArray();

				for (Enchantment enchantment : enchantRandomlyLootFunction.enchantments) {
					Identifier identifier = Registry.ENCHANTMENT.getId(enchantment);
					if (identifier == null) {
						throw new IllegalArgumentException("Don't know how to serialize enchantment " + enchantment);
					}

					jsonArray.add(new JsonPrimitive(identifier.toString()));
				}

				jsonObject.add("enchantments", jsonArray);
			}
		}

		public EnchantRandomlyLootFunction fromJson(JsonObject jsonObject, JsonDeserializationContext jsonDeserializationContext, LootCondition[] lootConditions) {
			List<Enchantment> list = Lists.newArrayList();
			if (jsonObject.has("enchantments")) {
				for (JsonElement jsonElement : JsonHelper.getArray(jsonObject, "enchantments")) {
					String string = JsonHelper.asString(jsonElement, "enchantment");
					Enchantment enchantment = (Enchantment)Registry.ENCHANTMENT
						.getOrEmpty(new Identifier(string))
						.orElseThrow(() -> new JsonSyntaxException("Unknown enchantment '" + string + "'"));
					list.add(enchantment);
				}
			}

			return new EnchantRandomlyLootFunction(lootConditions, list);
		}
	}
}
