package net.minecraft.resource;

import com.google.common.base.CharMatcher;
import com.google.common.collect.Sets;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Locale;
import java.util.Set;
import javax.annotation.Nullable;
import net.minecraft.util.Util;
import org.apache.commons.io.filefilter.DirectoryFileFilter;

public class DirectoryResourcePack extends AbstractFileResourcePack {
	private static final boolean field_15320 = Util.getOperatingSystem() == Util.OperatingSystem.WINDOWS;
	private static final CharMatcher field_15321 = CharMatcher.is('\\');

	public DirectoryResourcePack(File file) {
		super(file);
	}

	protected static boolean method_13885(File file, String string) throws IOException {
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
			return new BufferedInputStream(new FileInputStream(file));
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
	public Set<String> getNamespaces() {
		Set<String> set = Sets.newHashSet();
		File file = new File(this.base, "assets/");
		if (file.isDirectory()) {
			for (File file2 : file.listFiles(DirectoryFileFilter.DIRECTORY)) {
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
}
