package net.minecraft.util.thread;

import it.unimi.dsi.fastutil.ints.Int2BooleanFunction;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Executor;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.atomic.AtomicInteger;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class TaskExecutor<T> implements MessageListener<T>, AutoCloseable, Runnable {
	private static final Logger LOGGER = LogManager.getLogger();
	private final AtomicInteger stateFlags = new AtomicInteger(0);
	public final TaskQueue<? super T, ? extends Runnable> queue;
	private final Executor executor;
	private final String name;

	public static TaskExecutor<Runnable> create(Executor executor, String string) {
		return new TaskExecutor<>(new TaskQueue.Simple<>(new ConcurrentLinkedQueue()), executor, string);
	}

	public TaskExecutor(TaskQueue<? super T, ? extends Runnable> taskQueue, Executor executor, String string) {
		this.executor = executor;
		this.queue = taskQueue;
		this.name = string;
	}

	private boolean unpause() {
		int i;
		do {
			i = this.stateFlags.get();
			if ((i & 3) != 0) {
				return false;
			}
		} while (!this.stateFlags.compareAndSet(i, i | 2));

		return true;
	}

	private void pause() {
		int i;
		do {
			i = this.stateFlags.get();
		} while (!this.stateFlags.compareAndSet(i, i & -3));
	}

	private boolean hasMessages() {
		return (this.stateFlags.get() & 1) != 0 ? false : !this.queue.isEmpty();
	}

	@Override
	public void close() {
		int i;
		do {
			i = this.stateFlags.get();
		} while (!this.stateFlags.compareAndSet(i, i | 1));
	}

	private boolean isUnpaused() {
		return (this.stateFlags.get() & 2) != 0;
	}

	private boolean runNext() {
		if (!this.isUnpaused()) {
			return false;
		} else {
			Runnable runnable = this.queue.poll();
			if (runnable == null) {
				return false;
			} else {
				runnable.run();
				return true;
			}
		}
	}

	public void run() {
		try {
			this.runWhile(i -> i == 0);
		} finally {
			this.pause();
			this.execute();
		}
	}

	@Override
	public void send(T object) {
		this.queue.add(object);
		this.execute();
	}

	private void execute() {
		if (this.hasMessages() && this.unpause()) {
			try {
				this.executor.execute(this);
			} catch (RejectedExecutionException var4) {
				try {
					this.executor.execute(this);
				} catch (RejectedExecutionException var3) {
					LOGGER.error("Cound not schedule mailbox", var3);
				}
			}
		}
	}

	private int runWhile(Int2BooleanFunction int2BooleanFunction) {
		int i = 0;

		while (int2BooleanFunction.get(i) && this.runNext()) {
			i++;
		}

		return i;
	}

	public String toString() {
		return this.name + " " + this.stateFlags.get() + " " + this.queue.isEmpty();
	}

	@Override
	public String getName() {
		return this.name;
	}
}
