package net.minecraft.advancement.criterion;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.entity.passive.AbstractTraderEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.predicate.entity.EntityPredicate;
import net.minecraft.predicate.item.ItemPredicate;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;

public class VillagerTradeCriterion extends AbstractCriterion<VillagerTradeCriterion.Conditions> {
	private static final Identifier ID = new Identifier("villager_trade");

	@Override
	public Identifier getId() {
		return ID;
	}

	public VillagerTradeCriterion.Conditions conditionsFromJson(JsonObject jsonObject, JsonDeserializationContext jsonDeserializationContext) {
		EntityPredicate entityPredicate = EntityPredicate.fromJson(jsonObject.get("villager"));
		ItemPredicate itemPredicate = ItemPredicate.fromJson(jsonObject.get("item"));
		return new VillagerTradeCriterion.Conditions(entityPredicate, itemPredicate);
	}

	public void handle(ServerPlayerEntity serverPlayerEntity, AbstractTraderEntity abstractTraderEntity, ItemStack itemStack) {
		this.test(serverPlayerEntity.getAdvancementTracker(), conditions -> conditions.matches(serverPlayerEntity, abstractTraderEntity, itemStack));
	}

	public static class Conditions extends AbstractCriterionConditions {
		private final EntityPredicate villager;
		private final ItemPredicate item;

		public Conditions(EntityPredicate entityPredicate, ItemPredicate itemPredicate) {
			super(VillagerTradeCriterion.ID);
			this.villager = entityPredicate;
			this.item = itemPredicate;
		}

		public static VillagerTradeCriterion.Conditions any() {
			return new VillagerTradeCriterion.Conditions(EntityPredicate.ANY, ItemPredicate.ANY);
		}

		public boolean matches(ServerPlayerEntity serverPlayerEntity, AbstractTraderEntity abstractTraderEntity, ItemStack itemStack) {
			return !this.villager.test(serverPlayerEntity, abstractTraderEntity) ? false : this.item.test(itemStack);
		}

		@Override
		public JsonElement toJson() {
			JsonObject jsonObject = new JsonObject();
			jsonObject.add("item", this.item.toJson());
			jsonObject.add("villager", this.villager.serialize());
			return jsonObject;
		}
	}
}
