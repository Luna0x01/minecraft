package net.minecraft.world.level.storage;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.format.SignStyle;
import java.time.temporal.ChronoField;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;
import javax.annotation.Nullable;
import net.minecraft.client.ClientException;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ProgressListener;
import net.minecraft.world.SaveHandler;
import net.minecraft.world.level.LevelProperties;

public interface LevelStorageAccess {
	DateTimeFormatter field_19770 = new DateTimeFormatterBuilder()
		.appendValue(ChronoField.YEAR, 4, 10, SignStyle.EXCEEDS_PAD)
		.appendLiteral('-')
		.appendValue(ChronoField.MONTH_OF_YEAR, 2)
		.appendLiteral('-')
		.appendValue(ChronoField.DAY_OF_MONTH, 2)
		.appendLiteral('_')
		.appendValue(ChronoField.HOUR_OF_DAY, 2)
		.appendLiteral('-')
		.appendValue(ChronoField.MINUTE_OF_HOUR, 2)
		.appendLiteral('-')
		.appendValue(ChronoField.SECOND_OF_MINUTE, 2)
		.toFormatter();

	default long method_17970(String string) throws IOException {
		final Path path = this.method_17969(string);
		String string2 = LocalDateTime.now().format(field_19770) + "_" + string;
		int i = 0;
		Path path2 = this.method_17968();

		try {
			Files.createDirectories(Files.exists(path2, new LinkOption[0]) ? path2.toRealPath() : path2);
		} catch (IOException var19) {
			throw new RuntimeException(var19);
		}

		Path path3;
		do {
			path3 = path2.resolve(string2 + (i++ > 0 ? "_" + i : "") + ".zip");
		} while (Files.exists(path3, new LinkOption[0]));

		final ZipOutputStream zipOutputStream = new ZipOutputStream(new BufferedOutputStream(new FileOutputStream(path3.toFile())));
		Throwable var8 = null;

		try {
			final Path path4 = Paths.get(string);
			Files.walkFileTree(path, new SimpleFileVisitor<Path>() {
				public FileVisitResult visitFile(Path path, BasicFileAttributes basicFileAttributes) throws IOException {
					String string = path4.resolve(path.relativize(path)).toString();
					ZipEntry zipEntry = new ZipEntry(string);
					zipOutputStream.putNextEntry(zipEntry);
					com.google.common.io.Files.asByteSource(path.toFile()).copyTo(zipOutputStream);
					zipOutputStream.closeEntry();
					return FileVisitResult.CONTINUE;
				}
			});
			zipOutputStream.close();
		} catch (Throwable var18) {
			var8 = var18;
			throw var18;
		} finally {
			if (zipOutputStream != null) {
				if (var8 != null) {
					try {
						zipOutputStream.close();
					} catch (Throwable var17) {
						var8.addSuppressed(var17);
					}
				} else {
					zipOutputStream.close();
				}
			}
		}

		return Files.size(path3);
	}

	String getFormat();

	SaveHandler method_250(String string, @Nullable MinecraftServer minecraftServer);

	List<LevelSummary> getLevelList() throws ClientException;

	void clearAll();

	@Nullable
	LevelProperties getLevelProperties(String name);

	boolean isLevelNameValid(String name);

	boolean deleteLevel(String name);

	void renameLevel(String name, String newName);

	boolean isConvertible(String worldName);

	boolean needsConversion(String worldName);

	boolean convert(String worldName, ProgressListener progressListener);

	boolean levelExists(String name);

	File method_11957(String string, String string2);

	Path method_17969(String string);

	Path method_17968();
}
