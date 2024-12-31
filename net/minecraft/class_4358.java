package net.minecraft;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import java.util.function.Consumer;
import javax.annotation.Nullable;
import net.minecraft.advancement.AdvancementRewards;
import net.minecraft.advancement.SimpleAdvancement;
import net.minecraft.advancement.criterion.CriterionInstance;
import net.minecraft.item.Item;
import net.minecraft.item.Itemable;
import net.minecraft.recipe.Ingredient;
import net.minecraft.tag.Tag;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class class_4358 {
	private static final Logger field_21429 = LogManager.getLogger();
	private final Item field_21430;
	private final int field_21431;
	private final List<String> field_21432 = Lists.newArrayList();
	private final Map<Character, Ingredient> field_21433 = Maps.newLinkedHashMap();
	private final SimpleAdvancement.TaskAdvancement field_21434 = SimpleAdvancement.TaskAdvancement.method_20248();
	private String field_21435;

	public class_4358(Itemable itemable, int i) {
		this.field_21430 = itemable.getItem();
		this.field_21431 = i;
	}

	public static class_4358 method_20034(Itemable itemable) {
		return method_20035(itemable, 1);
	}

	public static class_4358 method_20035(Itemable itemable, int i) {
		return new class_4358(itemable, i);
	}

	public class_4358 method_20038(Character character, Tag<Item> tag) {
		return this.method_20036(character, Ingredient.fromTag(tag));
	}

	public class_4358 method_20037(Character character, Itemable itemable) {
		return this.method_20036(character, Ingredient.ofItems(itemable));
	}

	public class_4358 method_20036(Character character, Ingredient ingredient) {
		if (this.field_21433.containsKey(character)) {
			throw new IllegalArgumentException("Symbol '" + character + "' is already defined!");
		} else if (character == ' ') {
			throw new IllegalArgumentException("Symbol ' ' (whitespace) is reserved and cannot be defined");
		} else {
			this.field_21433.put(character, ingredient);
			return this;
		}
	}

	public class_4358 method_20039(String string) {
		if (!this.field_21432.isEmpty() && string.length() != ((String)this.field_21432.get(0)).length()) {
			throw new IllegalArgumentException("Pattern must be the same width on every line!");
		} else {
			this.field_21432.add(string);
			return this;
		}
	}

	public class_4358 method_20040(String string, CriterionInstance criterionInstance) {
		this.field_21434.method_20251(string, criterionInstance);
		return this;
	}

	public class_4358 method_20045(String string) {
		this.field_21435 = string;
		return this;
	}

	public void method_20041(Consumer<class_4356> consumer) {
		this.method_20043(consumer, Registry.ITEM.getId(this.field_21430));
	}

	public void method_20042(Consumer<class_4356> consumer, String string) {
		Identifier identifier = Registry.ITEM.getId(this.field_21430);
		if (new Identifier(string).equals(identifier)) {
			throw new IllegalStateException("Shaped Recipe " + string + " should remove its 'save' argument");
		} else {
			this.method_20043(consumer, new Identifier(string));
		}
	}

	public void method_20043(Consumer<class_4356> consumer, Identifier identifier) {
		this.method_20044(identifier);
		this.field_21434
			.method_20256(new Identifier("minecraft:recipes/root"))
			.method_20251("has_the_recipe", new class_3229.class_3712(identifier))
			.method_20254(AdvancementRewards.class_4395.method_20390(identifier))
			.method_20258(class_4470.field_21947);
		consumer.accept(
			new class_4358.class_4359(
				identifier,
				this.field_21430,
				this.field_21431,
				this.field_21435 == null ? "" : this.field_21435,
				this.field_21432,
				this.field_21433,
				this.field_21434,
				new Identifier(identifier.getNamespace(), "recipes/" + this.field_21430.getItemGroup().method_16034() + "/" + identifier.getPath())
			)
		);
	}

	private void method_20044(Identifier identifier) {
		if (this.field_21432.isEmpty()) {
			throw new IllegalStateException("No pattern is defined for shaped recipe " + identifier + "!");
		} else {
			Set<Character> set = Sets.newHashSet(this.field_21433.keySet());
			set.remove(' ');

			for (String string : this.field_21432) {
				for (int i = 0; i < string.length(); i++) {
					char c = string.charAt(i);
					if (!this.field_21433.containsKey(c) && c != ' ') {
						throw new IllegalStateException("Pattern in recipe " + identifier + " uses undefined symbol '" + c + "'");
					}

					set.remove(c);
				}
			}

			if (!set.isEmpty()) {
				throw new IllegalStateException("Ingredients are defined but not used in pattern for recipe " + identifier);
			} else if (this.field_21432.size() == 1 && ((String)this.field_21432.get(0)).length() == 1) {
				throw new IllegalStateException("Shaped recipe " + identifier + " only takes in a single item - should it be a shapeless recipe instead?");
			} else if (this.field_21434.method_20260().isEmpty()) {
				throw new IllegalStateException("No way of obtaining recipe " + identifier);
			}
		}
	}

	class class_4359 implements class_4356 {
		private final Identifier field_21437;
		private final Item field_21438;
		private final int field_21439;
		private final String field_21440;
		private final List<String> field_21441;
		private final Map<Character, Ingredient> field_21442;
		private final SimpleAdvancement.TaskAdvancement field_21443;
		private final Identifier field_21444;

		public class_4359(
			Identifier identifier,
			Item item,
			int i,
			String string,
			List<String> list,
			Map<Character, Ingredient> map,
			SimpleAdvancement.TaskAdvancement taskAdvancement,
			Identifier identifier2
		) {
			this.field_21437 = identifier;
			this.field_21438 = item;
			this.field_21439 = i;
			this.field_21440 = string;
			this.field_21441 = list;
			this.field_21442 = map;
			this.field_21443 = taskAdvancement;
			this.field_21444 = identifier2;
		}

		@Override
		public JsonObject method_20021() {
			JsonObject jsonObject = new JsonObject();
			jsonObject.addProperty("type", "crafting_shaped");
			if (!this.field_21440.isEmpty()) {
				jsonObject.addProperty("group", this.field_21440);
			}

			JsonArray jsonArray = new JsonArray();

			for (String string : this.field_21441) {
				jsonArray.add(string);
			}

			jsonObject.add("pattern", jsonArray);
			JsonObject jsonObject2 = new JsonObject();

			for (Entry<Character, Ingredient> entry : this.field_21442.entrySet()) {
				jsonObject2.add(String.valueOf(entry.getKey()), ((Ingredient)entry.getValue()).method_16194());
			}

			jsonObject.add("key", jsonObject2);
			JsonObject jsonObject3 = new JsonObject();
			jsonObject3.addProperty("item", Registry.ITEM.getId(this.field_21438).toString());
			if (this.field_21439 > 1) {
				jsonObject3.addProperty("count", this.field_21439);
			}

			jsonObject.add("result", jsonObject3);
			return jsonObject;
		}

		@Override
		public Identifier method_20022() {
			return this.field_21437;
		}

		@Nullable
		@Override
		public JsonObject method_20023() {
			return this.field_21443.method_20259();
		}

		@Nullable
		@Override
		public Identifier method_20024() {
			return this.field_21444;
		}
	}
}
