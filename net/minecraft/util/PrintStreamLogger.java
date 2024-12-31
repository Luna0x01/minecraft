package net.minecraft.util;

import java.io.OutputStream;
import java.io.PrintStream;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class PrintStreamLogger extends PrintStream {
	private static final Logger LOGGER = LogManager.getLogger();
	private final String name;

	public PrintStreamLogger(String string, OutputStream outputStream) {
		super(outputStream);
		this.name = string;
	}

	public void println(String message) {
		this.log(message);
	}

	public void println(Object message) {
		this.log(String.valueOf(message));
	}

	private void log(String message) {
		StackTraceElement[] stackTraceElements = Thread.currentThread().getStackTrace();
		StackTraceElement stackTraceElement = stackTraceElements[Math.min(3, stackTraceElements.length)];
		LOGGER.info("[{}]@.({}:{}): {}", new Object[]{this.name, stackTraceElement.getFileName(), stackTraceElement.getLineNumber(), message});
	}
}
