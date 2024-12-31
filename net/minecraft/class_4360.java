package net.minecraft;

import com.google.common.collect.Lists;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import java.util.List;
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

public class class_4360 {
	private static final Logger field_21445 = LogManager.getLogger();
	private final Item field_21446;
	private final int field_21447;
	private final List<Ingredient> field_21448 = Lists.newArrayList();
	private final SimpleAdvancement.TaskAdvancement field_21449 = SimpleAdvancement.TaskAdvancement.method_20248();
	private String field_21450;

	public class_4360(Itemable itemable, int i) {
		this.field_21446 = itemable.getItem();
		this.field_21447 = i;
	}

	public static class_4360 method_20048(Itemable itemable) {
		return new class_4360(itemable, 1);
	}

	public static class_4360 method_20049(Itemable itemable, int i) {
		return new class_4360(itemable, i);
	}

	public class_4360 method_20056(Tag<Item> tag) {
		return this.method_20046(Ingredient.fromTag(tag));
	}

	public class_4360 method_20057(Itemable itemable) {
		return this.method_20058(itemable, 1);
	}

	public class_4360 method_20058(Itemable itemable, int i) {
		for (int j = 0; j < i; j++) {
			this.method_20046(Ingredient.ofItems(itemable));
		}

		return this;
	}

	public class_4360 method_20046(Ingredient ingredient) {
		return this.method_20047(ingredient, 1);
	}

	public class_4360 method_20047(Ingredient ingredient, int i) {
		for (int j = 0; j < i; j++) {
			this.field_21448.add(ingredient);
		}

		return this;
	}

	public class_4360 method_20051(String string, CriterionInstance criterionInstance) {
		this.field_21449.method_20251(string, criterionInstance);
		return this;
	}

	public class_4360 method_20050(String string) {
		this.field_21450 = string;
		return this;
	}

	public void method_20052(Consumer<class_4356> consumer) {
		this.method_20054(consumer, Registry.ITEM.getId(this.field_21446));
	}

	public void method_20053(Consumer<class_4356> consumer, String string) {
		Identifier identifier = Registry.ITEM.getId(this.field_21446);
		if (new Identifier(string).equals(identifier)) {
			throw new IllegalStateException("Shapeless Recipe " + string + " should remove its 'save' argument");
		} else {
			this.method_20054(consumer, new Identifier(string));
		}
	}

	public void method_20054(Consumer<class_4356> consumer, Identifier identifier) {
		this.method_20055(identifier);
		this.field_21449
			.method_20256(new Identifier("minecraft:recipes/root"))
			.method_20251("has_the_recipe", new class_3229.class_3712(identifier))
			.method_20254(AdvancementRewards.class_4395.method_20390(identifier))
			.method_20258(class_4470.field_21947);
		consumer.accept(
			new class_4360.class_4361(
				identifier,
				this.field_21446,
				this.field_21447,
				this.field_21450 == null ? "" : this.field_21450,
				this.field_21448,
				this.field_21449,
				new Identifier(identifier.getNamespace(), "recipes/" + this.field_21446.getItemGroup().method_16034() + "/" + identifier.getPath())
			)
		);
	}

	private void method_20055(Identifier identifier) {
		if (this.field_21449.method_20260().isEmpty()) {
			throw new IllegalStateException("No way of obtaining recipe " + identifier);
		}
	}

	public static class class_4361 implements class_4356 {
		private final Identifier field_21451;
		private final Item field_21452;
		private final int field_21453;
		private final String field_21454;
		private final List<Ingredient> field_21455;
		private final SimpleAdvancement.TaskAdvancement field_21456;
		private final Identifier field_21457;

		public class_4361(
			Identifier identifier, Item item, int i, String string, List<Ingredient> list, SimpleAdvancement.TaskAdvancement taskAdvancement, Identifier identifier2
		) {
			this.field_21451 = identifier;
			this.field_21452 = item;
			this.field_21453 = i;
			this.field_21454 = string;
			this.field_21455 = list;
			this.field_21456 = taskAdvancement;
			this.field_21457 = identifier2;
		}

		@Override
		public JsonObject method_20021() {
			JsonObject jsonObject = new JsonObject();
			jsonObject.addProperty("type", "crafting_shapeless");
			if (!this.field_21454.isEmpty()) {
				jsonObject.addProperty("group", this.field_21454);
			}

			JsonArray jsonArray = new JsonArray();

			for (Ingredient ingredient : this.field_21455) {
				jsonArray.add(ingredient.method_16194());
			}

			jsonObject.add("ingredients", jsonArray);
			JsonObject jsonObject2 = new JsonObject();
			jsonObject2.addProperty("item", Registry.ITEM.getId(this.field_21452).toString());
			if (this.field_21453 > 1) {
				jsonObject2.addProperty("count", this.field_21453);
			}

			jsonObject.add("result", jsonObject2);
			return jsonObject;
		}

		@Override
		public Identifier method_20022() {
			return this.field_21451;
		}

		@Nullable
		@Override
		public JsonObject method_20023() {
			return this.field_21456.method_20259();
		}

		@Nullable
		@Override
		public Identifier method_20024() {
			return this.field_21457;
		}
	}
}
