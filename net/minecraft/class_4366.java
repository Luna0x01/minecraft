package net.minecraft;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.util.Objects;
import net.minecraft.nbt.NbtIo;
import net.minecraft.nbt.StringNbtReader;
import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class class_4366 implements class_4345 {
	private static final Logger field_21478 = LogManager.getLogger();
	private final class_4344 field_21479;

	public class_4366(class_4344 arg) {
		this.field_21479 = arg;
	}

	@Override
	public void method_19996(class_4346 arg) throws IOException {
		Path path = this.field_21479.method_19993();

		for (Path path2 : this.field_21479.method_19991()) {
			Files.walk(path2).filter(pathx -> pathx.toString().endsWith(".snbt")).forEach(path3 -> this.method_20072(arg, path3, this.method_20075(path2, path3), path));
		}
	}

	@Override
	public String method_19995() {
		return "SNBT -> NBT";
	}

	private String method_20075(Path path, Path path2) {
		String string = path.relativize(path2).toString().replaceAll("\\\\", "/");
		return string.substring(0, string.length() - ".snbt".length());
	}

	private void method_20072(class_4346 arg, Path path, String string, Path path2) {
		try {
			Path path3 = path2.resolve(string + ".nbt");
			BufferedReader bufferedReader = Files.newBufferedReader(path);
			Throwable var7 = null;

			try {
				String string2 = IOUtils.toString(bufferedReader);
				String string3 = field_21406.hashUnencodedChars(string2).toString();
				if (!Objects.equals(arg.method_19998(path3), string3) || !Files.exists(path3, new LinkOption[0])) {
					Files.createDirectories(path3.getParent());
					OutputStream outputStream = Files.newOutputStream(path3);
					Throwable var11 = null;

					try {
						NbtIo.writeCompressed(StringNbtReader.parse(string2), outputStream);
					} catch (Throwable var38) {
						var11 = var38;
						throw var38;
					} finally {
						if (outputStream != null) {
							if (var11 != null) {
								try {
									outputStream.close();
								} catch (Throwable var37) {
									var11.addSuppressed(var37);
								}
							} else {
								outputStream.close();
							}
						}
					}
				}

				arg.method_19999(path3, string3);
			} catch (Throwable var40) {
				var7 = var40;
				throw var40;
			} finally {
				if (bufferedReader != null) {
					if (var7 != null) {
						try {
							bufferedReader.close();
						} catch (Throwable var36) {
							var7.addSuppressed(var36);
						}
					} else {
						bufferedReader.close();
					}
				}
			}
		} catch (CommandSyntaxException var42) {
			field_21478.error("Couldn't convert {} from SNBT to NBT at {} as it's invalid SNBT", string, path, var42);
		} catch (IOException var43) {
			field_21478.error("Couldn't convert {} from SNBT to NBT at {}", string, path, var43);
		}
	}
}
