package net.minecraft.util;

import com.google.common.collect.Lists;
import java.util.Collections;
import java.util.List;

public class FileIoThread implements Runnable {
	private static final FileIoThread INSTANCE = new FileIoThread();
	private final List<FileIoCallback> callbacks = Collections.synchronizedList(Lists.newArrayList());
	private volatile long callbackCount;
	private volatile long callbacksCompleted;
	private volatile boolean waiting;

	private FileIoThread() {
		Thread thread = new Thread(this, "File IO Thread");
		thread.setPriority(1);
		thread.start();
	}

	public static FileIoThread getInstance() {
		return INSTANCE;
	}

	public void run() {
		while (true) {
			this.runCallbacks();
		}
	}

	private void runCallbacks() {
		for (int i = 0; i < this.callbacks.size(); i++) {
			FileIoCallback fileIoCallback = (FileIoCallback)this.callbacks.get(i);
			boolean bl = fileIoCallback.saveNextChunk();
			if (!bl) {
				this.callbacks.remove(i--);
				this.callbacksCompleted++;
			}

			try {
				Thread.sleep(this.waiting ? 0L : 10L);
			} catch (InterruptedException var6) {
				var6.printStackTrace();
			}
		}

		if (this.callbacks.isEmpty()) {
			try {
				Thread.sleep(25L);
			} catch (InterruptedException var5) {
				var5.printStackTrace();
			}
		}
	}

	public void registerCallback(FileIoCallback callback) {
		if (!this.callbacks.contains(callback)) {
			this.callbackCount++;
			this.callbacks.add(callback);
		}
	}

	public void waitUntilComplete() throws InterruptedException {
		this.waiting = true;

		while (this.callbackCount != this.callbacksCompleted) {
			Thread.sleep(10L);
		}

		this.waiting = false;
	}
}
