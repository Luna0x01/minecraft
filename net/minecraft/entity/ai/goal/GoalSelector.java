package net.minecraft.entity.ai.goal;

import com.google.common.collect.Lists;
import java.util.Iterator;
import java.util.List;
import net.minecraft.util.profiler.Profiler;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class GoalSelector {
	private static final Logger LOGGER = LogManager.getLogger();
	private List<GoalSelector.Entry> goals = Lists.newArrayList();
	private List<GoalSelector.Entry> field_3507 = Lists.newArrayList();
	private final Profiler profiler;
	private int field_3509;
	private int timeInterval = 3;

	public GoalSelector(Profiler profiler) {
		this.profiler = profiler;
	}

	public void add(int priority, Goal goal) {
		this.goals.add(new GoalSelector.Entry(priority, goal));
	}

	public void method_4497(Goal goal) {
		Iterator<GoalSelector.Entry> iterator = this.goals.iterator();

		while (iterator.hasNext()) {
			GoalSelector.Entry entry = (GoalSelector.Entry)iterator.next();
			Goal goal2 = entry.goal;
			if (goal2 == goal) {
				if (this.field_3507.contains(entry)) {
					goal2.stop();
					this.field_3507.remove(entry);
				}

				iterator.remove();
			}
		}
	}

	public void tick() {
		this.profiler.push("goalSetup");
		if (this.field_3509++ % this.timeInterval == 0) {
			for (GoalSelector.Entry entry : this.goals) {
				boolean bl = this.field_3507.contains(entry);
				if (bl) {
					if (this.method_11010(entry) && this.shouldContinue(entry)) {
						continue;
					}

					entry.goal.stop();
					this.field_3507.remove(entry);
				}

				if (this.method_11010(entry) && entry.goal.canStart()) {
					entry.goal.start();
					this.field_3507.add(entry);
				}
			}
		} else {
			Iterator<GoalSelector.Entry> iterator2 = this.field_3507.iterator();

			while (iterator2.hasNext()) {
				GoalSelector.Entry entry2 = (GoalSelector.Entry)iterator2.next();
				if (!this.shouldContinue(entry2)) {
					entry2.goal.stop();
					iterator2.remove();
				}
			}
		}

		this.profiler.pop();
		this.profiler.push("goalTick");

		for (GoalSelector.Entry entry3 : this.field_3507) {
			entry3.goal.tick();
		}

		this.profiler.pop();
	}

	private boolean shouldContinue(GoalSelector.Entry goal) {
		return goal.goal.shouldContinue();
	}

	private boolean method_11010(GoalSelector.Entry entry) {
		for (GoalSelector.Entry entry2 : this.goals) {
			if (entry2 != entry) {
				if (entry.priority >= entry2.priority) {
					if (!this.method_2753(entry, entry2) && this.field_3507.contains(entry2)) {
						return false;
					}
				} else if (!entry2.goal.canStop() && this.field_3507.contains(entry2)) {
					return false;
				}
			}
		}

		return true;
	}

	private boolean method_2753(GoalSelector.Entry entry1, GoalSelector.Entry entry2) {
		return (entry1.goal.getCategoryBits() & entry2.goal.getCategoryBits()) == 0;
	}

	class Entry {
		public Goal goal;
		public int priority;

		public Entry(int i, Goal goal) {
			this.priority = i;
			this.goal = goal;
		}
	}
}
