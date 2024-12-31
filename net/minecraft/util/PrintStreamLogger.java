package net.minecraft.util;

import java.io.OutputStream;
import java.io.PrintStream;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class PrintStreamLogger extends PrintStream {
	protected static final Logger LOGGER = LogManager.getLogger();
	protected final String name;

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

	protected void log(String message) {
		LOGGER.info("[{}]: {}", new Object[]{this.name, message});
	}
}
