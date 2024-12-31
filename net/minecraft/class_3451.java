package net.minecraft;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class class_3451 implements ThreadFactory {
	private static final Logger field_16628 = LogManager.getLogger();
	private final ThreadGroup field_16629;
	private final AtomicInteger field_16630 = new AtomicInteger(1);
	private final String field_16631;

	public class_3451(String string) {
		SecurityManager securityManager = System.getSecurityManager();
		this.field_16629 = securityManager != null ? securityManager.getThreadGroup() : Thread.currentThread().getThreadGroup();
		this.field_16631 = string + "-";
	}

	public Thread newThread(Runnable runnable) {
		Thread thread = new Thread(this.field_16629, runnable, this.field_16631 + this.field_16630.getAndIncrement(), 0L);
		thread.setUncaughtExceptionHandler((threadx, throwable) -> {
			field_16628.error("Caught exception in thread {} from {}", threadx, runnable);
			field_16628.error("", throwable);
		});
		if (thread.getPriority() != 5) {
			thread.setPriority(5);
		}

		return thread;
	}
}
