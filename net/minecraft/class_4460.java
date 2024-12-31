package net.minecraft;

import java.io.File;
import java.io.FileFilter;
import java.util.Map;
import java.util.function.Supplier;
import net.minecraft.resource.DirectoryResourcePack;
import net.minecraft.resource.ZipResourcePack;

public class class_4460 implements class_4463 {
	private static final FileFilter field_21897 = file -> {
		boolean bl = file.isFile() && file.getName().endsWith(".zip");
		boolean bl2 = file.isDirectory() && new File(file, "pack.mcmeta").isFile();
		return bl || bl2;
	};
	private final File field_21898;

	public class_4460(File file) {
		this.field_21898 = file;
	}

	@Override
	public <T extends class_4465> void method_21356(Map<String, T> map, class_4465.class_4467<T> arg) {
		if (!this.field_21898.isDirectory()) {
			this.field_21898.mkdirs();
		}

		File[] files = this.field_21898.listFiles(field_21897);
		if (files != null) {
			for (File file : files) {
				String string = "file/" + file.getName();
				T lv = class_4465.method_21359(string, false, this.method_21339(file), arg, class_4465.class_4466.TOP);
				if (lv != null) {
					map.put(string, lv);
				}
			}
		}
	}

	private Supplier<class_4454> method_21339(File file) {
		return file.isDirectory() ? () -> new DirectoryResourcePack(file) : () -> new ZipResourcePack(file);
	}
}
