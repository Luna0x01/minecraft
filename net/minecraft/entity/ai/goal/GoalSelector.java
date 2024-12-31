package net.minecraft.entity.ai.goal;

import com.google.common.collect.Sets;
import java.util.Iterator;
import java.util.Set;
import javax.annotation.Nullable;
import net.minecraft.util.profiler.Profiler;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class GoalSelector {
	private static final Logger LOGGER = LogManager.getLogger();
	private final Set<GoalSelector.Entry> field_14577 = Sets.newLinkedHashSet();
	private final Set<GoalSelector.Entry> field_14578 = Sets.newLinkedHashSet();
	private final Profiler profiler;
	private int field_3509;
	private int timeInterval = 3;
	private int field_14579 = 0;

	public GoalSelector(Profiler profiler) {
		this.profiler = profiler;
	}

	public void add(int priority, Goal goal) {
		this.field_14577.add(new GoalSelector.Entry(priority, goal));
	}

	public void method_4497(Goal goal) {
		Iterator<GoalSelector.Entry> iterator = this.field_14577.iterator();

		while (iterator.hasNext()) {
			GoalSelector.Entry entry = (GoalSelector.Entry)iterator.next();
			Goal goal2 = entry.goal;
			if (goal2 == goal) {
				if (entry.field_14580) {
					entry.field_14580 = false;
					entry.goal.stop();
					this.field_14578.remove(entry);
				}

				iterator.remove();
				return;
			}
		}
	}

	public void tick() {
		this.profiler.push("goalSetup");
		if (this.field_3509++ % this.timeInterval == 0) {
			for (GoalSelector.Entry entry : this.field_14577) {
				if (entry.field_14580) {
					if (!this.method_11010(entry) || !this.shouldContinue(entry)) {
						entry.field_14580 = false;
						entry.goal.stop();
						this.field_14578.remove(entry);
					}
				} else if (this.method_11010(entry) && entry.goal.canStart()) {
					entry.field_14580 = true;
					entry.goal.start();
					this.field_14578.add(entry);
				}
			}
		} else {
			Iterator<GoalSelector.Entry> iterator2 = this.field_14578.iterator();

			while (iterator2.hasNext()) {
				GoalSelector.Entry entry2 = (GoalSelector.Entry)iterator2.next();
				if (!this.shouldContinue(entry2)) {
					entry2.field_14580 = false;
					entry2.goal.stop();
					iterator2.remove();
				}
			}
		}

		this.profiler.pop();
		if (!this.field_14578.isEmpty()) {
			this.profiler.push("goalTick");

			for (GoalSelector.Entry entry3 : this.field_14578) {
				entry3.goal.tick();
			}

			this.profiler.pop();
		}
	}

	private boolean shouldContinue(GoalSelector.Entry goal) {
		return goal.goal.shouldContinue();
	}

	private boolean method_11010(GoalSelector.Entry entry) {
		if (this.field_14578.isEmpty()) {
			return true;
		} else if (this.method_13097(entry.goal.getCategoryBits())) {
			return false;
		} else {
			for (GoalSelector.Entry entry2 : this.field_14578) {
				if (entry2 != entry) {
					if (entry.priority >= entry2.priority) {
						if (!this.method_2753(entry, entry2)) {
							return false;
						}
					} else if (!entry2.goal.canStop()) {
						return false;
					}
				}
			}

			return true;
		}
	}

	private boolean method_2753(GoalSelector.Entry entry1, GoalSelector.Entry entry2) {
		return (entry1.goal.getCategoryBits() & entry2.goal.getCategoryBits()) == 0;
	}

	public boolean method_13097(int i) {
		return (this.field_14579 & i) > 0;
	}

	public void method_13098(int i) {
		this.field_14579 |= i;
	}

	public void method_13099(int i) {
		this.field_14579 &= ~i;
	}

	public void method_13096(int i, boolean bl) {
		if (bl) {
			this.method_13099(i);
		} else {
			this.method_13098(i);
		}
	}

	class Entry {
		public final Goal goal;
		public final int priority;
		public boolean field_14580;

		public Entry(int i, Goal goal) {
			this.priority = i;
			this.goal = goal;
		}

		public boolean equals(@Nullable Object object) {
			if (this == object) {
				return true;
			} else {
				return object != null && this.getClass() == object.getClass() ? this.goal.equals(((GoalSelector.Entry)object).goal) : false;
			}
		}

		public int hashCode() {
			return this.goal.hashCode();
		}
	}
}
