package net.minecraft.util;

import org.apache.logging.log4j.Logger;

public class UncaughtExceptionLogger implements java.lang.Thread.UncaughtExceptionHandler {
	private final Logger logger;

	public UncaughtExceptionLogger(Logger logger) {
		this.logger = logger;
	}

	public void uncaughtException(Thread thread, Throwable throwable) {
		this.logger.error("Caught previously unhandled exception :", throwable);
	}
}
