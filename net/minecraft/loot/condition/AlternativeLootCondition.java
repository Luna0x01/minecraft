package net.minecraft.loot.condition;

import com.google.common.collect.Lists;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import java.util.List;
import java.util.function.Predicate;
import net.minecraft.loot.LootTableReporter;
import net.minecraft.loot.context.LootContext;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.JsonSerializer;

public class AlternativeLootCondition implements LootCondition {
	final LootCondition[] terms;
	private final Predicate<LootContext> predicate;

	AlternativeLootCondition(LootCondition[] terms) {
		this.terms = terms;
		this.predicate = LootConditionTypes.joinOr(terms);
	}

	@Override
	public LootConditionType getType() {
		return LootConditionTypes.ALTERNATIVE;
	}

	public final boolean test(LootContext lootContext) {
		return this.predicate.test(lootContext);
	}

	@Override
	public void validate(LootTableReporter reporter) {
		LootCondition.super.validate(reporter);

		for (int i = 0; i < this.terms.length; i++) {
			this.terms[i].validate(reporter.makeChild(".term[" + i + "]"));
		}
	}

	public static AlternativeLootCondition.Builder builder(LootCondition.Builder... terms) {
		return new AlternativeLootCondition.Builder(terms);
	}

	public static class Builder implements LootCondition.Builder {
		private final List<LootCondition> terms = Lists.newArrayList();

		public Builder(LootCondition.Builder... terms) {
			for (LootCondition.Builder builder : terms) {
				this.terms.add(builder.build());
			}
		}

		@Override
		public AlternativeLootCondition.Builder or(LootCondition.Builder condition) {
			this.terms.add(condition.build());
			return this;
		}

		@Override
		public LootCondition build() {
			return new AlternativeLootCondition((LootCondition[])this.terms.toArray(new LootCondition[0]));
		}
	}

	public static class Serializer implements JsonSerializer<AlternativeLootCondition> {
		public void toJson(JsonObject jsonObject, AlternativeLootCondition alternativeLootCondition, JsonSerializationContext jsonSerializationContext) {
			jsonObject.add("terms", jsonSerializationContext.serialize(alternativeLootCondition.terms));
		}

		public AlternativeLootCondition fromJson(JsonObject jsonObject, JsonDeserializationContext jsonDeserializationContext) {
			LootCondition[] lootConditions = JsonHelper.deserialize(jsonObject, "terms", jsonDeserializationContext, LootCondition[].class);
			return new AlternativeLootCondition(lootConditions);
		}
	}
}
