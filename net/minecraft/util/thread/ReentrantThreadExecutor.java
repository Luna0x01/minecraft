package net.minecraft.util.thread;

public abstract class ReentrantThreadExecutor<R extends Runnable> extends ThreadExecutor<R> {
	private int runningTasks;

	public ReentrantThreadExecutor(String string) {
		super(string);
	}

	@Override
	protected boolean shouldExecuteAsync() {
		return this.hasRunningTasks() || super.shouldExecuteAsync();
	}

	protected boolean hasRunningTasks() {
		return this.runningTasks != 0;
	}

	@Override
	protected void executeTask(R runnable) {
		this.runningTasks++;

		try {
			super.executeTask(runnable);
		} finally {
			this.runningTasks--;
		}
	}
}
