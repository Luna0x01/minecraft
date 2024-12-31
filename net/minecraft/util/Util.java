package net.minecraft.util;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;
import org.apache.logging.log4j.Logger;

public class Util {
	public static Util.OperatingSystem getOperatingSystem() {
		String string = System.getProperty("os.name").toLowerCase();
		if (string.contains("win")) {
			return Util.OperatingSystem.WINDOWS;
		} else if (string.contains("mac")) {
			return Util.OperatingSystem.MACOS;
		} else if (string.contains("solaris")) {
			return Util.OperatingSystem.SOLARIS;
		} else if (string.contains("sunos")) {
			return Util.OperatingSystem.SOLARIS;
		} else if (string.contains("linux")) {
			return Util.OperatingSystem.LINUX;
		} else {
			return string.contains("unix") ? Util.OperatingSystem.LINUX : Util.OperatingSystem.OTHER;
		}
	}

	public static <V> V executeTask(FutureTask<V> task, Logger logger) {
		try {
			task.run();
			return (V)task.get();
		} catch (ExecutionException var3) {
			logger.fatal("Error executing task", var3);
		} catch (InterruptedException var4) {
			logger.fatal("Error executing task", var4);
		}

		return null;
	}

	public static enum OperatingSystem {
		LINUX,
		SOLARIS,
		WINDOWS,
		MACOS,
		OTHER;
	}
}
