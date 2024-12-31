package net.minecraft.resource;

import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.function.Predicate;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import net.minecraft.class_4455;
import net.minecraft.util.Identifier;
import org.apache.commons.io.IOUtils;

public class ZipResourcePack extends AbstractFileResourcePack {
	public static final Splitter TYPE_NAMESPACE_SPLITTER = Splitter.on('/').omitEmptyStrings().limit(3);
	private ZipFile file;

	public ZipResourcePack(File file) {
		super(file);
	}

	private ZipFile getZipFile() throws IOException {
		if (this.file == null) {
			this.file = new ZipFile(this.base);
		}

		return this.file;
	}

	@Override
	protected InputStream openFile(String name) throws IOException {
		ZipFile zipFile = this.getZipFile();
		ZipEntry zipEntry = zipFile.getEntry(name);
		if (zipEntry == null) {
			throw new ResourceNotFoundException(this.base, name);
		} else {
			return zipFile.getInputStream(zipEntry);
		}
	}

	@Override
	public boolean containsFile(String name) {
		try {
			return this.getZipFile().getEntry(name) != null;
		} catch (IOException var3) {
			return false;
		}
	}

	@Override
	public Set<String> method_21327(class_4455 arg) {
		ZipFile zipFile;
		try {
			zipFile = this.getZipFile();
		} catch (IOException var9) {
			return Collections.emptySet();
		}

		Enumeration<? extends ZipEntry> enumeration = zipFile.entries();
		Set<String> set = Sets.newHashSet();

		while (enumeration.hasMoreElements()) {
			ZipEntry zipEntry = (ZipEntry)enumeration.nextElement();
			String string = zipEntry.getName();
			if (string.startsWith(arg.method_21331() + "/")) {
				List<String> list = Lists.newArrayList(TYPE_NAMESPACE_SPLITTER.split(string));
				if (list.size() > 1) {
					String string2 = (String)list.get(1);
					if (string2.equals(string2.toLowerCase(Locale.ROOT))) {
						set.add(string2);
					} else {
						this.warnNonLowercaseNamespace(string2);
					}
				}
			}
		}

		return set;
	}

	protected void finalize() throws Throwable {
		this.close();
		super.finalize();
	}

	public void close() {
		if (this.file != null) {
			IOUtils.closeQuietly(this.file);
			this.file = null;
		}
	}

	@Override
	public Collection<Identifier> method_21328(class_4455 arg, String string, int i, Predicate<String> predicate) {
		ZipFile zipFile;
		try {
			zipFile = this.getZipFile();
		} catch (IOException var15) {
			return Collections.emptySet();
		}

		Enumeration<? extends ZipEntry> enumeration = zipFile.entries();
		List<Identifier> list = Lists.newArrayList();
		String string2 = arg.method_21331() + "/";

		while (enumeration.hasMoreElements()) {
			ZipEntry zipEntry = (ZipEntry)enumeration.nextElement();
			if (!zipEntry.isDirectory() && zipEntry.getName().startsWith(string2)) {
				String string3 = zipEntry.getName().substring(string2.length());
				if (!string3.endsWith(".mcmeta")) {
					int j = string3.indexOf(47);
					if (j >= 0) {
						String string4 = string3.substring(j + 1);
						if (string4.startsWith(string + "/")) {
							String[] strings = string4.substring(string.length() + 2).split("/");
							if (strings.length >= i + 1 && predicate.test(string4)) {
								String string5 = string3.substring(0, j);
								list.add(new Identifier(string5, string4));
							}
						}
					}
				}
			}
		}

		return list;
	}
}
