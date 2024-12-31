package net.minecraft;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.FileVisitOption;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.resource.AbstractFileResourcePack;
import net.minecraft.resource.DirectoryResourcePack;
import net.minecraft.util.Identifier;
import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class class_4456 implements class_4454 {
	public static Path field_21890;
	private static final Logger field_21893 = LogManager.getLogger();
	public static Class<?> field_21891;
	public final Set<String> field_21892;

	public class_4456(String... strings) {
		this.field_21892 = ImmutableSet.copyOf(strings);
	}

	@Override
	public InputStream method_21330(String string) throws IOException {
		if (!string.contains("/") && !string.contains("\\")) {
			if (field_21890 != null) {
				Path path = field_21890.resolve(string);
				if (Files.exists(path, new LinkOption[0])) {
					return Files.newInputStream(path);
				}
			}

			return this.method_21333(string);
		} else {
			throw new IllegalArgumentException("Root resources can only be filenames, not paths (no / allowed!)");
		}
	}

	@Override
	public InputStream method_5897(class_4455 arg, Identifier identifier) throws IOException {
		InputStream inputStream = this.method_21334(arg, identifier);
		if (inputStream != null) {
			return inputStream;
		} else {
			throw new FileNotFoundException(identifier.getPath());
		}
	}

	@Override
	public Collection<Identifier> method_21328(class_4455 arg, String string, int i, Predicate<String> predicate) {
		Set<Identifier> set = Sets.newHashSet();
		if (field_21890 != null) {
			try {
				set.addAll(this.method_21332(i, "minecraft", field_21890.resolve(arg.method_21331()).resolve("minecraft"), string, predicate));
			} catch (IOException var26) {
			}

			if (arg == class_4455.CLIENT_RESOURCES) {
				Enumeration<URL> enumeration = null;

				try {
					enumeration = field_21891.getClassLoader().getResources(arg.method_21331() + "/minecraft");
				} catch (IOException var25) {
				}

				while (enumeration != null && enumeration.hasMoreElements()) {
					try {
						URI uRI = ((URL)enumeration.nextElement()).toURI();
						if ("file".equals(uRI.getScheme())) {
							set.addAll(this.method_21332(i, "minecraft", Paths.get(uRI), string, predicate));
						}
					} catch (IOException | URISyntaxException var24) {
					}
				}
			}
		}

		try {
			URL uRL = class_4456.class.getResource("/" + arg.method_21331() + "/.mcassetsroot");
			if (uRL == null) {
				field_21893.error("Couldn't find .mcassetsroot, cannot load vanilla resources");
				return set;
			}

			URI uRI2 = uRL.toURI();
			if ("file".equals(uRI2.getScheme())) {
				URL uRL2 = new URL(uRL.toString().substring(0, uRL.toString().length() - ".mcassetsroot".length()) + "minecraft");
				if (uRL2 == null) {
					return set;
				}

				Path path = Paths.get(uRL2.toURI());
				set.addAll(this.method_21332(i, "minecraft", path, string, predicate));
			} else if ("jar".equals(uRI2.getScheme())) {
				FileSystem fileSystem = FileSystems.newFileSystem(uRI2, Collections.emptyMap());
				Throwable var33 = null;

				try {
					Path path2 = fileSystem.getPath("/" + arg.method_21331() + "/minecraft");
					set.addAll(this.method_21332(i, "minecraft", path2, string, predicate));
				} catch (Throwable var23) {
					var33 = var23;
					throw var23;
				} finally {
					if (fileSystem != null) {
						if (var33 != null) {
							try {
								fileSystem.close();
							} catch (Throwable var22) {
								var33.addSuppressed(var22);
							}
						} else {
							fileSystem.close();
						}
					}
				}
			} else {
				field_21893.error("Unsupported scheme {} trying to list vanilla resources (NYI?)", uRI2);
			}
		} catch (NoSuchFileException | FileNotFoundException var28) {
		} catch (IOException | URISyntaxException var29) {
			field_21893.error("Couldn't get a list of all vanilla resources", var29);
		}

		return set;
	}

	private Collection<Identifier> method_21332(int i, String string, Path path, String string2, Predicate<String> predicate) throws IOException {
		List<Identifier> list = Lists.newArrayList();
		Iterator<Path> iterator = Files.walk(path.resolve(string2), i, new FileVisitOption[0]).iterator();

		while (iterator.hasNext()) {
			Path path2 = (Path)iterator.next();
			if (!path2.endsWith(".mcmeta") && Files.isRegularFile(path2, new LinkOption[0]) && predicate.test(path2.getFileName().toString())) {
				list.add(new Identifier(string, path.relativize(path2).toString().replaceAll("\\\\", "/")));
			}
		}

		return list;
	}

	@Nullable
	protected InputStream method_21334(class_4455 arg, Identifier identifier) {
		String string = "/" + arg.method_21331() + "/" + identifier.getNamespace() + "/" + identifier.getPath();
		if (field_21890 != null) {
			Path path = field_21890.resolve(arg.method_21331() + "/" + identifier.getNamespace() + "/" + identifier.getPath());
			if (Files.exists(path, new LinkOption[0])) {
				try {
					return Files.newInputStream(path);
				} catch (IOException var7) {
				}
			}
		}

		try {
			URL uRL = class_4456.class.getResource(string);
			return uRL != null && DirectoryResourcePack.method_13885(new File(uRL.getFile()), string) ? class_4456.class.getResourceAsStream(string) : null;
		} catch (IOException var6) {
			return class_4456.class.getResourceAsStream(string);
		}
	}

	@Nullable
	protected InputStream method_21333(String string) {
		return class_4456.class.getResourceAsStream("/" + string);
	}

	@Override
	public boolean method_5900(class_4455 arg, Identifier identifier) {
		InputStream inputStream = this.method_21334(arg, identifier);
		boolean bl = inputStream != null;
		IOUtils.closeQuietly(inputStream);
		return bl;
	}

	@Override
	public Set<String> method_21327(class_4455 arg) {
		return this.field_21892;
	}

	@Nullable
	@Override
	public <T> T method_21329(class_4457<T> arg) throws IOException {
		try {
			InputStream inputStream = this.method_21330("pack.mcmeta");
			Throwable var3 = null;

			Object var4;
			try {
				var4 = AbstractFileResourcePack.method_21324(arg, inputStream);
			} catch (Throwable var14) {
				var3 = var14;
				throw var14;
			} finally {
				if (inputStream != null) {
					if (var3 != null) {
						try {
							inputStream.close();
						} catch (Throwable var13) {
							var3.addSuppressed(var13);
						}
					} else {
						inputStream.close();
					}
				}
			}

			return (T)var4;
		} catch (FileNotFoundException | RuntimeException var16) {
			return null;
		}
	}

	@Override
	public String method_5899() {
		return "Default";
	}

	public void close() {
	}
}
