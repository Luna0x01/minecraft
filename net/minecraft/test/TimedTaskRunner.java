package net.minecraft.test;

import java.util.Iterator;
import java.util.List;

public class TimedTaskRunner {
	private final GameTest test;
	private final List<TimedTask> tasks;
	private long tick;

	public void runSilently(long l) {
		try {
			this.runTasks(l);
		} catch (Exception var4) {
		}
	}

	public void runReported(long l) {
		try {
			this.runTasks(l);
		} catch (Exception var4) {
			this.test.fail(var4);
		}
	}

	private void runTasks(long l) {
		Iterator<TimedTask> iterator = this.tasks.iterator();

		while (iterator.hasNext()) {
			TimedTask timedTask = (TimedTask)iterator.next();
			timedTask.task.run();
			iterator.remove();
			long m = l - this.tick;
			long n = this.tick;
			this.tick = l;
			if (timedTask.duration != null && timedTask.duration != m) {
				this.test.fail(new TimeMismatchException("Succeeded in invalid tick: expected " + (n + timedTask.duration) + ", but current tick is " + l));
				break;
			}
		}
	}
}
