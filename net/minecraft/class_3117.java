package net.minecraft;

import java.io.OutputStream;
import net.minecraft.util.PrintStreamLogger;

public class class_3117 extends PrintStreamLogger {
	public class_3117(String string, OutputStream outputStream) {
		super(string, outputStream);
	}

	@Override
	protected void log(String message) {
		StackTraceElement[] stackTraceElements = Thread.currentThread().getStackTrace();
		StackTraceElement stackTraceElement = stackTraceElements[Math.min(3, stackTraceElements.length)];
		LOGGER.info("[{}]@.({}:{}): {}", new Object[]{this.name, stackTraceElement.getFileName(), stackTraceElement.getLineNumber(), message});
	}
}
