package net.minecraft;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtIo;
import net.minecraft.text.Text;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class class_4365 implements class_4345 {
	private static final Logger field_21476 = LogManager.getLogger();
	private final class_4344 field_21477;

	public class_4365(class_4344 arg) {
		this.field_21477 = arg;
	}

	@Override
	public void method_19996(class_4346 arg) throws IOException {
		Path path = this.field_21477.method_19993();

		for (Path path2 : this.field_21477.method_19991()) {
			Files.walk(path2).filter(pathx -> pathx.toString().endsWith(".nbt")).forEach(path3 -> this.method_20069(path3, this.method_20070(path2, path3), path));
		}
	}

	@Override
	public String method_19995() {
		return "NBT to SNBT";
	}

	private String method_20070(Path path, Path path2) {
		String string = path.relativize(path2).toString().replaceAll("\\\\", "/");
		return string.substring(0, string.length() - ".nbt".length());
	}

	private void method_20069(Path path, String string, Path path2) {
		try {
			NbtCompound nbtCompound = NbtIo.readCompressed(Files.newInputStream(path));
			Text text = nbtCompound.asText("    ", 0);
			String string2 = text.getString();
			Path path3 = path2.resolve(string + ".snbt");
			Files.createDirectories(path3.getParent());
			BufferedWriter bufferedWriter = Files.newBufferedWriter(path3);
			Throwable var9 = null;

			try {
				bufferedWriter.write(string2);
			} catch (Throwable var19) {
				var9 = var19;
				throw var19;
			} finally {
				if (bufferedWriter != null) {
					if (var9 != null) {
						try {
							bufferedWriter.close();
						} catch (Throwable var18) {
							var9.addSuppressed(var18);
						}
					} else {
						bufferedWriter.close();
					}
				}
			}

			field_21476.info("Converted {} from NBT to SNBT", string);
		} catch (IOException var21) {
			field_21476.error("Couldn't convert {} from NBT to SNBT at {}", string, path, var21);
		}
	}
}
