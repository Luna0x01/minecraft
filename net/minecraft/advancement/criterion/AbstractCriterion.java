package net.minecraft.advancement.criterion;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.gson.JsonObject;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Predicate;
import net.minecraft.advancement.PlayerAdvancementTracker;
import net.minecraft.loot.context.LootContext;
import net.minecraft.predicate.entity.AdvancementEntityPredicateDeserializer;
import net.minecraft.predicate.entity.EntityPredicate;
import net.minecraft.server.network.ServerPlayerEntity;

public abstract class AbstractCriterion<T extends AbstractCriterionConditions> implements Criterion<T> {
	private final Map<PlayerAdvancementTracker, Set<Criterion.ConditionsContainer<T>>> progressions = Maps.newIdentityHashMap();

	@Override
	public final void beginTrackingCondition(PlayerAdvancementTracker manager, Criterion.ConditionsContainer<T> conditions) {
		((Set)this.progressions.computeIfAbsent(manager, managerx -> Sets.newHashSet())).add(conditions);
	}

	@Override
	public final void endTrackingCondition(PlayerAdvancementTracker manager, Criterion.ConditionsContainer<T> conditions) {
		Set<Criterion.ConditionsContainer<T>> set = (Set<Criterion.ConditionsContainer<T>>)this.progressions.get(manager);
		if (set != null) {
			set.remove(conditions);
			if (set.isEmpty()) {
				this.progressions.remove(manager);
			}
		}
	}

	@Override
	public final void endTracking(PlayerAdvancementTracker tracker) {
		this.progressions.remove(tracker);
	}

	protected abstract T conditionsFromJson(JsonObject obj, EntityPredicate.Extended playerPredicate, AdvancementEntityPredicateDeserializer predicateDeserializer);

	public final T conditionsFromJson(JsonObject jsonObject, AdvancementEntityPredicateDeserializer advancementEntityPredicateDeserializer) {
		EntityPredicate.Extended extended = EntityPredicate.Extended.getInJson(jsonObject, "player", advancementEntityPredicateDeserializer);
		return this.conditionsFromJson(jsonObject, extended, advancementEntityPredicateDeserializer);
	}

	protected void test(ServerPlayerEntity player, Predicate<T> tester) {
		PlayerAdvancementTracker playerAdvancementTracker = player.getAdvancementTracker();
		Set<Criterion.ConditionsContainer<T>> set = (Set<Criterion.ConditionsContainer<T>>)this.progressions.get(playerAdvancementTracker);
		if (set != null && !set.isEmpty()) {
			LootContext lootContext = EntityPredicate.createAdvancementEntityLootContext(player, player);
			List<Criterion.ConditionsContainer<T>> list = null;

			for (Criterion.ConditionsContainer<T> conditionsContainer : set) {
				T abstractCriterionConditions = conditionsContainer.getConditions();
				if (tester.test(abstractCriterionConditions) && abstractCriterionConditions.getPlayerPredicate().test(lootContext)) {
					if (list == null) {
						list = Lists.newArrayList();
					}

					list.add(conditionsContainer);
				}
			}

			if (list != null) {
				for (Criterion.ConditionsContainer<T> conditionsContainer2 : list) {
					conditionsContainer2.grant(playerAdvancementTracker);
				}
			}
		}
	}
}
