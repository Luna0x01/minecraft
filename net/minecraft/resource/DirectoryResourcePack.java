package net.minecraft.resource;

import com.google.common.base.CharMatcher;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.class_4374;
import net.minecraft.class_4455;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import org.apache.commons.io.filefilter.DirectoryFileFilter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class DirectoryResourcePack extends AbstractFileResourcePack {
	private static final Logger field_21885 = LogManager.getLogger();
	private static final boolean field_15320 = Util.getOperatingSystem() == Util.OperatingSystem.WINDOWS;
	private static final CharMatcher field_15321 = CharMatcher.is('\\');

	public DirectoryResourcePack(File file) {
		super(file);
	}

	public static boolean method_13885(File file, String string) throws IOException {
		String string2 = file.getCanonicalPath();
		if (field_15320) {
			string2 = field_15321.replaceFrom(string2, '/');
		}

		return string2.endsWith(string);
	}

	@Override
	protected InputStream openFile(String name) throws IOException {
		File file = this.method_13886(name);
		if (file == null) {
			throw new ResourceNotFoundException(this.base, name);
		} else {
			return new FileInputStream(file);
		}
	}

	@Override
	protected boolean containsFile(String name) {
		return this.method_13886(name) != null;
	}

	@Nullable
	private File method_13886(String string) {
		try {
			File file = new File(this.base, string);
			if (file.isFile() && method_13885(file, string)) {
				return file;
			}
		} catch (IOException var3) {
		}

		return null;
	}

	@Override
	public Set<String> method_21327(class_4455 arg) {
		Set<String> set = Sets.newHashSet();
		File file = new File(this.base, arg.method_21331());
		File[] files = file.listFiles(DirectoryFileFilter.DIRECTORY);
		if (files != null) {
			for (File file2 : files) {
				String string = relativize(file, file2);
				if (string.equals(string.toLowerCase(Locale.ROOT))) {
					set.add(string.substring(0, string.length() - 1));
				} else {
					this.warnNonLowercaseNamespace(string);
				}
			}
		}

		return set;
	}

	public void close() throws IOException {
	}

	@Override
	public Collection<Identifier> method_21328(class_4455 arg, String string, int i, Predicate<String> predicate) {
		File file = new File(this.base, arg.method_21331());
		List<Identifier> list = Lists.newArrayList();

		for (String string2 : this.method_21327(arg)) {
			this.method_21326(new File(new File(file, string2), string), i, string2, list, string + "/", predicate);
		}

		return list;
	}

	private void method_21326(File file, int i, String string, List<Identifier> list, String string2, Predicate<String> predicate) {
		File[] files = file.listFiles();
		if (files != null) {
			for (File file2 : files) {
				if (file2.isDirectory()) {
					if (i > 0) {
						this.method_21326(file2, i - 1, string, list, string2 + file2.getName() + "/", predicate);
					}
				} else if (!file2.getName().endsWith(".mcmeta") && predicate.test(file2.getName())) {
					try {
						list.add(new Identifier(string, string2 + file2.getName()));
					} catch (class_4374 var13) {
						field_21885.error(var13.getMessage());
					}
				}
			}
		}
	}
}
