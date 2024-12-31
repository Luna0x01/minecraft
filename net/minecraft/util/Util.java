package net.minecraft.util;

import com.google.common.collect.Iterators;
import it.unimi.dsi.fastutil.Hash.Strategy;
import java.io.File;
import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.AccessController;
import java.security.PrivilegedActionException;
import java.time.Instant;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;
import java.util.function.Consumer;
import java.util.function.LongSupplier;
import java.util.function.Supplier;
import java.util.regex.Pattern;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.state.property.Property;
import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Util {
	public static LongSupplier field_21541 = System::nanoTime;
	private static final Logger field_21542 = LogManager.getLogger();
	private static final Pattern field_21543 = Pattern.compile(
		".*\\.|(?:CON|PRN|AUX|NUL|COM1|COM2|COM3|COM4|COM5|COM6|COM7|COM8|COM9|LPT1|LPT2|LPT3|LPT4|LPT5|LPT6|LPT7|LPT8|LPT9)(?:\\..*)?", 2
	);

	public static <K, V> Collector<Entry<? extends K, ? extends V>, ?, Map<K, V>> method_20218() {
		return Collectors.toMap(Entry::getKey, Entry::getValue);
	}

	public static <T extends Comparable<T>> String method_20219(Property<T> property, Object object) {
		return property.name((T)object);
	}

	public static String createTranslationKey(String string, @Nullable Identifier identifier) {
		return identifier == null ? string + ".unregistered_sadface" : string + '.' + identifier.getNamespace() + '.' + identifier.getPath().replace('/', '.');
	}

	public static long method_20227() {
		return method_20230() / 1000000L;
	}

	public static long method_20230() {
		return field_21541.getAsLong();
	}

	public static long method_20231() {
		return Instant.now().toEpochMilli();
	}

	public static Util.OperatingSystem getOperatingSystem() {
		String string = System.getProperty("os.name").toLowerCase(Locale.ROOT);
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

	public static Stream<String> method_20232() {
		RuntimeMXBean runtimeMXBean = ManagementFactory.getRuntimeMXBean();
		return runtimeMXBean.getInputArguments().stream().filter(string -> string.startsWith("-X"));
	}

	public static boolean method_20224(Path path) {
		Path path2 = path.normalize();
		return path2.equals(path);
	}

	public static boolean method_20229(Path path) {
		for (Path path2 : path) {
			if (field_21543.matcher(path2.toString()).matches()) {
				return false;
			}
		}

		return true;
	}

	public static Path method_20225(Path path, String string, String string2) {
		String string3 = string + string2;
		Path path2 = Paths.get(string3);
		if (path2.endsWith(string2)) {
			throw new InvalidPathException(string3, "empty resource name");
		} else {
			return path.resolve(path2);
		}
	}

	@Nullable
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

	public static <T> T getLast(List<T> list) {
		return (T)list.get(list.size() - 1);
	}

	public static <T> T method_20220(Iterable<T> iterable, @Nullable T object) {
		Iterator<T> iterator = iterable.iterator();
		T object2 = (T)iterator.next();
		if (object != null) {
			T object3 = object2;

			while (object3 != object) {
				if (iterator.hasNext()) {
					object3 = (T)iterator.next();
				}
			}

			if (iterator.hasNext()) {
				return (T)iterator.next();
			}
		}

		return object2;
	}

	public static <T> T method_20228(Iterable<T> iterable, @Nullable T object) {
		Iterator<T> iterator = iterable.iterator();
		T object2 = null;

		while (iterator.hasNext()) {
			T object3 = (T)iterator.next();
			if (object3 == object) {
				if (object2 == null) {
					object2 = (T)(iterator.hasNext() ? Iterators.getLast(iterator) : object);
				}
				break;
			}

			object2 = object3;
		}

		return object2;
	}

	public static <T> T make(Supplier<T> supplier) {
		return (T)supplier.get();
	}

	public static <T> T make(T object, Consumer<T> initializer) {
		initializer.accept(object);
		return object;
	}

	public static <K> Strategy<K> method_20233() {
		return Util.class_4378.INSTANCE;
	}

	public static enum OperatingSystem {
		LINUX,
		SOLARIS,
		WINDOWS {
			@Override
			protected String[] method_20239(URL uRL) {
				return new String[]{"rundll32", "url.dll,FileProtocolHandler", uRL.toString()};
			}
		},
		MACOS {
			@Override
			protected String[] method_20239(URL uRL) {
				return new String[]{"open", uRL.toString()};
			}
		},
		OTHER;

		private OperatingSystem() {
		}

		public void method_20238(URL uRL) {
			try {
				Process process = (Process)AccessController.doPrivileged(() -> Runtime.getRuntime().exec(this.method_20239(uRL)));

				for (String string : IOUtils.readLines(process.getErrorStream())) {
					Util.field_21542.error(string);
				}

				process.getInputStream().close();
				process.getErrorStream().close();
				process.getOutputStream().close();
			} catch (IOException | PrivilegedActionException var5) {
				Util.field_21542.error("Couldn't open url '{}'", uRL, var5);
			}
		}

		public void method_20237(URI uRI) {
			try {
				this.method_20238(uRI.toURL());
			} catch (MalformedURLException var3) {
				Util.field_21542.error("Couldn't open uri '{}'", uRI, var3);
			}
		}

		public void method_20235(File file) {
			try {
				this.method_20238(file.toURI().toURL());
			} catch (MalformedURLException var3) {
				Util.field_21542.error("Couldn't open file '{}'", file, var3);
			}
		}

		protected String[] method_20239(URL uRL) {
			String string = uRL.toString();
			if ("file".equals(uRL.getProtocol())) {
				string = string.replace("file:", "file://");
			}

			return new String[]{"xdg-open", string};
		}

		public void method_20236(String string) {
			try {
				this.method_20238(new URI(string).toURL());
			} catch (MalformedURLException | IllegalArgumentException | URISyntaxException var3) {
				Util.field_21542.error("Couldn't open uri '{}'", string, var3);
			}
		}
	}

	static enum class_4378 implements Strategy<Object> {
		INSTANCE;

		public int hashCode(Object object) {
			return System.identityHashCode(object);
		}

		public boolean equals(Object object, Object object2) {
			return object == object2;
		}
	}
}
