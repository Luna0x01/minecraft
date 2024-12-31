package net.minecraft;

import java.lang.Thread.UncaughtExceptionHandler;
import org.apache.logging.log4j.Logger;

public class class_4325 implements UncaughtExceptionHandler {
	private final Logger field_21252;

	public class_4325(Logger logger) {
		this.field_21252 = logger;
	}

	public void uncaughtException(Thread thread, Throwable throwable) {
		this.field_21252.error("Caught previously unhandled exception :");
		this.field_21252.error(throwable);
	}
}
