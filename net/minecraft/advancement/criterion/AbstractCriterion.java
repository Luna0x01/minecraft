package net.minecraft.advancement.criterion;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.common.collect.UnmodifiableIterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Predicate;
import net.minecraft.advancement.PlayerAdvancementTracker;

public abstract class AbstractCriterion<T extends CriterionConditions> implements Criterion<T> {
	private final Map<PlayerAdvancementTracker, Set<Criterion.ConditionsContainer<T>>> progressions = Maps.newIdentityHashMap();

	@Override
	public final void beginTrackingCondition(PlayerAdvancementTracker playerAdvancementTracker, Criterion.ConditionsContainer<T> conditionsContainer) {
		((Set)this.progressions.computeIfAbsent(playerAdvancementTracker, playerAdvancementTrackerx -> Sets.newHashSet())).add(conditionsContainer);
	}

	@Override
	public final void endTrackingCondition(PlayerAdvancementTracker playerAdvancementTracker, Criterion.ConditionsContainer<T> conditionsContainer) {
		Set<Criterion.ConditionsContainer<T>> set = (Set<Criterion.ConditionsContainer<T>>)this.progressions.get(playerAdvancementTracker);
		if (set != null) {
			set.remove(conditionsContainer);
			if (set.isEmpty()) {
				this.progressions.remove(playerAdvancementTracker);
			}
		}
	}

	@Override
	public final void endTracking(PlayerAdvancementTracker playerAdvancementTracker) {
		this.progressions.remove(playerAdvancementTracker);
	}

	protected void test(PlayerAdvancementTracker playerAdvancementTracker, Predicate<T> predicate) {
		Set<Criterion.ConditionsContainer<T>> set = (Set<Criterion.ConditionsContainer<T>>)this.progressions.get(playerAdvancementTracker);
		if (set != null) {
			List<Criterion.ConditionsContainer<T>> list = null;

			for (Criterion.ConditionsContainer<T> conditionsContainer : set) {
				if (predicate.test(conditionsContainer.getConditions())) {
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

	protected void grant(PlayerAdvancementTracker playerAdvancementTracker) {
		Set<Criterion.ConditionsContainer<T>> set = (Set<Criterion.ConditionsContainer<T>>)this.progressions.get(playerAdvancementTracker);
		if (set != null && !set.isEmpty()) {
			UnmodifiableIterator var3 = ImmutableSet.copyOf(set).iterator();

			while (var3.hasNext()) {
				Criterion.ConditionsContainer<T> conditionsContainer = (Criterion.ConditionsContainer<T>)var3.next();
				conditionsContainer.grant(playerAdvancementTracker);
			}
		}
	}
}
