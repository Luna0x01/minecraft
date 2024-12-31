package net.minecraft.util.crash;

import com.google.common.collect.Lists;
import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import net.minecraft.util.collection.IntArrayCache;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class CrashReport {
	private static final Logger LOGGER = LogManager.getLogger();
	private final String message;
	private final Throwable cause;
	private final CrashReportSection systemDetailsSection = new CrashReportSection(this, "System Details");
	private final List<CrashReportSection> otherSections = Lists.newArrayList();
	private File file;
	private boolean hasStackTrace = true;
	private StackTraceElement[] stackTrace = new StackTraceElement[0];

	public CrashReport(String string, Throwable throwable) {
		this.message = string;
		this.cause = throwable;
		this.fillSystemDetails();
	}

	private void fillSystemDetails() {
		this.systemDetailsSection.add("Minecraft Version", new CrashCallable<String>() {
			public String call() {
				return "1.10.2";
			}
		});
		this.systemDetailsSection.add("Operating System", new CrashCallable<String>() {
			public String call() {
				return System.getProperty("os.name") + " (" + System.getProperty("os.arch") + ") version " + System.getProperty("os.version");
			}
		});
		this.systemDetailsSection.add("Java Version", new CrashCallable<String>() {
			public String call() {
				return System.getProperty("java.version") + ", " + System.getProperty("java.vendor");
			}
		});
		this.systemDetailsSection.add("Java VM Version", new CrashCallable<String>() {
			public String call() {
				return System.getProperty("java.vm.name") + " (" + System.getProperty("java.vm.info") + "), " + System.getProperty("java.vm.vendor");
			}
		});
		this.systemDetailsSection.add("Memory", new CrashCallable<String>() {
			public String call() {
				Runtime runtime = Runtime.getRuntime();
				long l = runtime.maxMemory();
				long m = runtime.totalMemory();
				long n = runtime.freeMemory();
				long o = l / 1024L / 1024L;
				long p = m / 1024L / 1024L;
				long q = n / 1024L / 1024L;
				return n + " bytes (" + q + " MB) / " + m + " bytes (" + p + " MB) up to " + l + " bytes (" + o + " MB)";
			}
		});
		this.systemDetailsSection.add("JVM Flags", new CrashCallable<String>() {
			public String call() {
				RuntimeMXBean runtimeMXBean = ManagementFactory.getRuntimeMXBean();
				List<String> list = runtimeMXBean.getInputArguments();
				int i = 0;
				StringBuilder stringBuilder = new StringBuilder();

				for (String string : list) {
					if (string.startsWith("-X")) {
						if (i++ > 0) {
							stringBuilder.append(" ");
						}

						stringBuilder.append(string);
					}
				}

				return String.format("%d total; %s", i, stringBuilder.toString());
			}
		});
		this.systemDetailsSection.add("IntCache", new CrashCallable<String>() {
			public String call() throws Exception {
				return IntArrayCache.asString();
			}
		});
	}

	public String getMessage() {
		return this.message;
	}

	public Throwable getCause() {
		return this.cause;
	}

	public void addStackTrace(StringBuilder stringBuilder) {
		if ((this.stackTrace == null || this.stackTrace.length <= 0) && !this.otherSections.isEmpty()) {
			this.stackTrace = (StackTraceElement[])ArrayUtils.subarray(((CrashReportSection)this.otherSections.get(0)).getStackTrace(), 0, 1);
		}

		if (this.stackTrace != null && this.stackTrace.length > 0) {
			stringBuilder.append("-- Head --\n");
			stringBuilder.append("Thread: ").append(Thread.currentThread().getName()).append("\n");
			stringBuilder.append("Stacktrace:\n");

			for (StackTraceElement stackTraceElement : this.stackTrace) {
				stringBuilder.append("\t").append("at ").append(stackTraceElement);
				stringBuilder.append("\n");
			}

			stringBuilder.append("\n");
		}

		for (CrashReportSection crashReportSection : this.otherSections) {
			crashReportSection.addStackTrace(stringBuilder);
			stringBuilder.append("\n\n");
		}

		this.systemDetailsSection.addStackTrace(stringBuilder);
	}

	public String getCauseAsString() {
		StringWriter stringWriter = null;
		PrintWriter printWriter = null;
		Throwable throwable = this.cause;
		if (throwable.getMessage() == null) {
			if (throwable instanceof NullPointerException) {
				throwable = new NullPointerException(this.message);
			} else if (throwable instanceof StackOverflowError) {
				throwable = new StackOverflowError(this.message);
			} else if (throwable instanceof OutOfMemoryError) {
				throwable = new OutOfMemoryError(this.message);
			}

			throwable.setStackTrace(this.cause.getStackTrace());
		}

		String string = throwable.toString();

		try {
			stringWriter = new StringWriter();
			printWriter = new PrintWriter(stringWriter);
			throwable.printStackTrace(printWriter);
			string = stringWriter.toString();
		} finally {
			IOUtils.closeQuietly(stringWriter);
			IOUtils.closeQuietly(printWriter);
		}

		return string;
	}

	public String asString() {
		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append("---- Minecraft Crash Report ----\n");
		stringBuilder.append("// ");
		stringBuilder.append(generateWittyComment());
		stringBuilder.append("\n\n");
		stringBuilder.append("Time: ");
		stringBuilder.append(new SimpleDateFormat().format(new Date()));
		stringBuilder.append("\n");
		stringBuilder.append("Description: ");
		stringBuilder.append(this.message);
		stringBuilder.append("\n\n");
		stringBuilder.append(this.getCauseAsString());
		stringBuilder.append("\n\nA detailed walkthrough of the error, its code path and all known details is as follows:\n");

		for (int i = 0; i < 87; i++) {
			stringBuilder.append("-");
		}

		stringBuilder.append("\n\n");
		this.addStackTrace(stringBuilder);
		return stringBuilder.toString();
	}

	public File getFile() {
		return this.file;
	}

	public boolean writeToFile(File file) {
		if (this.file != null) {
			return false;
		} else {
			if (file.getParentFile() != null) {
				file.getParentFile().mkdirs();
			}

			FileWriter fileWriter = null;

			boolean var4;
			try {
				fileWriter = new FileWriter(file);
				fileWriter.write(this.asString());
				this.file = file;
				return true;
			} catch (Throwable var8) {
				LOGGER.error("Could not save crash report to {}", new Object[]{file, var8});
				var4 = false;
			} finally {
				IOUtils.closeQuietly(fileWriter);
			}

			return var4;
		}
	}

	public CrashReportSection getSystemDetailsSection() {
		return this.systemDetailsSection;
	}

	public CrashReportSection addElement(String name) {
		return this.addElement(name, 1);
	}

	public CrashReportSection addElement(String name, int ignoredStackTraceCallCount) {
		CrashReportSection crashReportSection = new CrashReportSection(this, name);
		if (this.hasStackTrace) {
			int i = crashReportSection.initStackTrace(ignoredStackTraceCallCount);
			StackTraceElement[] stackTraceElements = this.cause.getStackTrace();
			StackTraceElement stackTraceElement = null;
			StackTraceElement stackTraceElement2 = null;
			int j = stackTraceElements.length - i;
			if (j < 0) {
				System.out.println("Negative index in crash report handler (" + stackTraceElements.length + "/" + i + ")");
			}

			if (stackTraceElements != null && 0 <= j && j < stackTraceElements.length) {
				stackTraceElement = stackTraceElements[j];
				if (stackTraceElements.length + 1 - i < stackTraceElements.length) {
					stackTraceElement2 = stackTraceElements[stackTraceElements.length + 1 - i];
				}
			}

			this.hasStackTrace = crashReportSection.method_4426(stackTraceElement, stackTraceElement2);
			if (i > 0 && !this.otherSections.isEmpty()) {
				CrashReportSection crashReportSection2 = (CrashReportSection)this.otherSections.get(this.otherSections.size() - 1);
				crashReportSection2.trimStackTraceEnd(i);
			} else if (stackTraceElements != null && stackTraceElements.length >= i && 0 <= j && j < stackTraceElements.length) {
				this.stackTrace = new StackTraceElement[j];
				System.arraycopy(stackTraceElements, 0, this.stackTrace, 0, this.stackTrace.length);
			} else {
				this.hasStackTrace = false;
			}
		}

		this.otherSections.add(crashReportSection);
		return crashReportSection;
	}

	private static String generateWittyComment() {
		String[] strings = new String[]{
			"Who set us up the TNT?",
			"Everything's going to plan. No, really, that was supposed to happen.",
			"Uh... Did I do that?",
			"Oops.",
			"Why did you do that?",
			"I feel sad now :(",
			"My bad.",
			"I'm sorry, Dave.",
			"I let you down. Sorry :(",
			"On the bright side, I bought you a teddy bear!",
			"Daisy, daisy...",
			"Oh - I know what I did wrong!",
			"Hey, that tickles! Hehehe!",
			"I blame Dinnerbone.",
			"You should try our sister game, Minceraft!",
			"Don't be sad. I'll do better next time, I promise!",
			"Don't be sad, have a hug! <3",
			"I just don't know what went wrong :(",
			"Shall we play a game?",
			"Quite honestly, I wouldn't worry myself about that.",
			"I bet Cylons wouldn't have this problem.",
			"Sorry :(",
			"Surprise! Haha. Well, this is awkward.",
			"Would you like a cupcake?",
			"Hi. I'm Minecraft, and I'm a crashaholic.",
			"Ooh. Shiny.",
			"This doesn't make any sense!",
			"Why is it breaking :(",
			"Don't do that.",
			"Ouch. That hurt :(",
			"You're mean.",
			"This is a token for 1 free hug. Redeem at your nearest Mojangsta: [~~HUG~~]",
			"There are four lights!",
			"But it works on my machine."
		};

		try {
			return strings[(int)(System.nanoTime() % (long)strings.length)];
		} catch (Throwable var2) {
			return "Witty comment unavailable :(";
		}
	}

	public static CrashReport create(Throwable cause, String title) {
		CrashReport crashReport;
		if (cause instanceof CrashException) {
			crashReport = ((CrashException)cause).getReport();
		} else {
			crashReport = new CrashReport(title, cause);
		}

		return crashReport;
	}
}
