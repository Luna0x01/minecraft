package net.minecraft;

import com.google.gson.JsonObject;
import java.util.function.Consumer;
import javax.annotation.Nullable;
import net.minecraft.advancement.AdvancementRewards;
import net.minecraft.advancement.SimpleAdvancement;
import net.minecraft.advancement.criterion.CriterionInstance;
import net.minecraft.item.Item;
import net.minecraft.item.Itemable;
import net.minecraft.recipe.Ingredient;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class class_4362 {
	private static final Logger field_21458 = LogManager.getLogger();
	private final Item field_21459;
	private final Ingredient field_21460;
	private final float field_21461;
	private final int field_21462;
	private final SimpleAdvancement.TaskAdvancement field_21463 = SimpleAdvancement.TaskAdvancement.method_20248();
	private String field_21464;

	public class_4362(Ingredient ingredient, Itemable itemable, float f, int i) {
		this.field_21459 = itemable.getItem();
		this.field_21460 = ingredient;
		this.field_21461 = f;
		this.field_21462 = i;
	}

	public static class_4362 method_20059(Ingredient ingredient, Itemable itemable, float f, int i) {
		return new class_4362(ingredient, itemable, f, i);
	}

	public class_4362 method_20060(String string, CriterionInstance criterionInstance) {
		this.field_21463.method_20251(string, criterionInstance);
		return this;
	}

	public void method_20061(Consumer<class_4356> consumer) {
		this.method_20063(consumer, Registry.ITEM.getId(this.field_21459));
	}

	public void method_20062(Consumer<class_4356> consumer, String string) {
		Identifier identifier = Registry.ITEM.getId(this.field_21459);
		if (new Identifier(string).equals(identifier)) {
			throw new IllegalStateException("Smelting Recipe " + string + " should remove its 'save' argument");
		} else {
			this.method_20063(consumer, new Identifier(string));
		}
	}

	public void method_20063(Consumer<class_4356> consumer, Identifier identifier) {
		this.method_20064(identifier);
		this.field_21463
			.method_20256(new Identifier("minecraft:recipes/root"))
			.method_20251("has_the_recipe", new class_3229.class_3712(identifier))
			.method_20254(AdvancementRewards.class_4395.method_20390(identifier))
			.method_20258(class_4470.field_21947);
		consumer.accept(
			new class_4362.class_4363(
				identifier,
				this.field_21464 == null ? "" : this.field_21464,
				this.field_21460,
				this.field_21459,
				this.field_21461,
				this.field_21462,
				this.field_21463,
				new Identifier(identifier.getNamespace(), "recipes/" + this.field_21459.getItemGroup().method_16034() + "/" + identifier.getPath())
			)
		);
	}

	private void method_20064(Identifier identifier) {
		if (this.field_21463.method_20260().isEmpty()) {
			throw new IllegalStateException("No way of obtaining recipe " + identifier);
		}
	}

	public static class class_4363 implements class_4356 {
		private final Identifier field_21465;
		private final String field_21466;
		private final Ingredient field_21467;
		private final Item field_21468;
		private final float field_21469;
		private final int field_21470;
		private final SimpleAdvancement.TaskAdvancement field_21471;
		private final Identifier field_21472;

		public class_4363(
			Identifier identifier,
			String string,
			Ingredient ingredient,
			Item item,
			float f,
			int i,
			SimpleAdvancement.TaskAdvancement taskAdvancement,
			Identifier identifier2
		) {
			this.field_21465 = identifier;
			this.field_21466 = string;
			this.field_21467 = ingredient;
			this.field_21468 = item;
			this.field_21469 = f;
			this.field_21470 = i;
			this.field_21471 = taskAdvancement;
			this.field_21472 = identifier2;
		}

		@Override
		public JsonObject method_20021() {
			JsonObject jsonObject = new JsonObject();
			jsonObject.addProperty("type", "smelting");
			if (!this.field_21466.isEmpty()) {
				jsonObject.addProperty("group", this.field_21466);
			}

			jsonObject.add("ingredient", this.field_21467.method_16194());
			jsonObject.addProperty("result", Registry.ITEM.getId(this.field_21468).toString());
			jsonObject.addProperty("experience", this.field_21469);
			jsonObject.addProperty("cookingtime", this.field_21470);
			return jsonObject;
		}

		@Override
		public Identifier method_20022() {
			return this.field_21465;
		}

		@Nullable
		@Override
		public JsonObject method_20023() {
			return this.field_21471.method_20259();
		}

		@Nullable
		@Override
		public Identifier method_20024() {
			return this.field_21472;
		}
	}
}
