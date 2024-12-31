package net.minecraft.resource;

import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class ZipResourcePack extends AbstractFileResourcePack implements Closeable {
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
	public Set<String> getNamespaces() {
		ZipFile zipFile;
		try {
			zipFile = this.getZipFile();
		} catch (IOException var8) {
			return Collections.emptySet();
		}

		Enumeration<? extends ZipEntry> enumeration = zipFile.entries();
		Set<String> set = Sets.newHashSet();

		while (enumeration.hasMoreElements()) {
			ZipEntry zipEntry = (ZipEntry)enumeration.nextElement();
			String string = zipEntry.getName();
			if (string.startsWith("assets/")) {
				List<String> list = Lists.newArrayList(TYPE_NAMESPACE_SPLITTER.split(string));
				if (list.size() > 1) {
					String string2 = (String)list.get(1);
					if (!string2.equals(string2.toLowerCase())) {
						this.warnNonLowercaseNamespace(string2);
					} else {
						set.add(string2);
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

	public void close() throws IOException {
		if (this.file != null) {
			this.file.close();
			this.file = null;
		}
	}
}
