package net.minecraft.resource;

import com.google.common.collect.Sets;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Set;
import org.apache.commons.io.filefilter.DirectoryFileFilter;

public class DirectoryResourcePack extends AbstractFileResourcePack {
	public DirectoryResourcePack(File file) {
		super(file);
	}

	@Override
	protected InputStream openFile(String name) throws IOException {
		return new BufferedInputStream(new FileInputStream(new File(this.base, name)));
	}

	@Override
	protected boolean containsFile(String name) {
		return new File(this.base, name).isFile();
	}

	@Override
	public Set<String> getNamespaces() {
		Set<String> set = Sets.newHashSet();
		File file = new File(this.base, "assets/");
		if (file.isDirectory()) {
			for (File file2 : file.listFiles(DirectoryFileFilter.DIRECTORY)) {
				String string = relativize(file, file2);
				if (!string.equals(string.toLowerCase())) {
					this.warnNonLowercaseNamespace(string);
				} else {
					set.add(string.substring(0, string.length() - 1));
				}
			}
		}

		return set;
	}
}
