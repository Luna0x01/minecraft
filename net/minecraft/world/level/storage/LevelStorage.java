package net.minecraft.world.level.storage;

import com.google.common.collect.Lists;
import com.mojang.datafixers.DataFixTypes;
import com.mojang.datafixers.DataFixer;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.client.ClientException;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.nbt.NbtIo;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ProgressListener;
import net.minecraft.world.SaveHandler;
import net.minecraft.world.WorldSaveHandler;
import net.minecraft.world.level.LevelProperties;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class LevelStorage implements LevelStorageAccess {
	private static final Logger lOGGER = LogManager.getLogger();
	protected final Path field_19759;
	protected final Path field_19760;
	protected final DataFixer field_19761;

	public LevelStorage(Path path, Path path2, DataFixer dataFixer) {
		this.field_19761 = dataFixer;

		try {
			Files.createDirectories(Files.exists(path, new LinkOption[0]) ? path.toRealPath() : path);
		} catch (IOException var5) {
			throw new RuntimeException(var5);
		}

		this.field_19759 = path;
		this.field_19760 = path2;
	}

	@Override
	public String getFormat() {
		return "Old Format";
	}

	@Override
	public List<LevelSummary> getLevelList() throws ClientException {
		List<LevelSummary> list = Lists.newArrayList();

		for (int i = 0; i < 5; i++) {
			String string = "World" + (i + 1);
			LevelProperties levelProperties = this.getLevelProperties(string);
			if (levelProperties != null) {
				list.add(new LevelSummary(levelProperties, string, "", levelProperties.getSizeOnDisk(), false));
			}
		}

		return list;
	}

	@Override
	public void clearAll() {
	}

	@Nullable
	@Override
	public LevelProperties getLevelProperties(String name) {
		File file = new File(this.field_19759.toFile(), name);
		if (!file.exists()) {
			return null;
		} else {
			File file2 = new File(file, "level.dat");
			if (file2.exists()) {
				LevelProperties levelProperties = method_17949(file2, this.field_19761);
				if (levelProperties != null) {
					return levelProperties;
				}
			}

			file2 = new File(file, "level.dat_old");
			return file2.exists() ? method_17949(file2, this.field_19761) : null;
		}
	}

	@Nullable
	public static LevelProperties method_17949(File file, DataFixer dataFixer) {
		try {
			NbtCompound nbtCompound = NbtIo.readCompressed(new FileInputStream(file));
			NbtCompound nbtCompound2 = nbtCompound.getCompound("Data");
			NbtCompound nbtCompound3 = nbtCompound2.contains("Player", 10) ? nbtCompound2.getCompound("Player") : null;
			nbtCompound2.remove("Player");
			int i = nbtCompound2.contains("DataVersion", 99) ? nbtCompound2.getInt("DataVersion") : -1;
			return new LevelProperties(NbtHelper.method_20141(dataFixer, DataFixTypes.LEVEL, nbtCompound2, i), dataFixer, i, nbtCompound3);
		} catch (Exception var6) {
			lOGGER.error("Exception reading {}", file, var6);
			return null;
		}
	}

	@Override
	public void renameLevel(String name, String newName) {
		File file = new File(this.field_19759.toFile(), name);
		if (file.exists()) {
			File file2 = new File(file, "level.dat");
			if (file2.exists()) {
				try {
					NbtCompound nbtCompound = NbtIo.readCompressed(new FileInputStream(file2));
					NbtCompound nbtCompound2 = nbtCompound.getCompound("Data");
					nbtCompound2.putString("LevelName", newName);
					NbtIo.writeCompressed(nbtCompound, new FileOutputStream(file2));
				} catch (Exception var7) {
					var7.printStackTrace();
				}
			}
		}
	}

	@Override
	public boolean isLevelNameValid(String name) {
		File file = new File(this.field_19759.toFile(), name);
		if (file.exists()) {
			return false;
		} else {
			try {
				file.mkdir();
				file.delete();
				return true;
			} catch (Throwable var4) {
				lOGGER.warn("Couldn't make new level", var4);
				return false;
			}
		}
	}

	@Override
	public boolean deleteLevel(String name) {
		File file = new File(this.field_19759.toFile(), name);
		if (!file.exists()) {
			return true;
		} else {
			lOGGER.info("Deleting level {}", name);

			for (int i = 1; i <= 5; i++) {
				lOGGER.info("Attempt {}...", i);
				if (deleteFilesRecursively(file.listFiles())) {
					break;
				}

				lOGGER.warn("Unsuccessful in deleting contents.");
				if (i < 5) {
					try {
						Thread.sleep(500L);
					} catch (InterruptedException var5) {
					}
				}
			}

			return file.delete();
		}
	}

	protected static boolean deleteFilesRecursively(File[] files) {
		for (File file : files) {
			lOGGER.debug("Deleting {}", file);
			if (file.isDirectory() && !deleteFilesRecursively(file.listFiles())) {
				lOGGER.warn("Couldn't delete directory {}", file);
				return false;
			}

			if (!file.delete()) {
				lOGGER.warn("Couldn't delete file {}", file);
				return false;
			}
		}

		return true;
	}

	@Override
	public SaveHandler method_250(String string, @Nullable MinecraftServer minecraftServer) {
		return new WorldSaveHandler(this.field_19759.toFile(), string, minecraftServer, this.field_19761);
	}

	@Override
	public boolean isConvertible(String worldName) {
		return false;
	}

	@Override
	public boolean needsConversion(String worldName) {
		return false;
	}

	@Override
	public boolean convert(String worldName, ProgressListener progressListener) {
		return false;
	}

	@Override
	public boolean levelExists(String name) {
		return Files.isDirectory(this.field_19759.resolve(name), new LinkOption[0]);
	}

	@Override
	public File method_11957(String string, String string2) {
		return this.field_19759.resolve(string).resolve(string2).toFile();
	}

	@Override
	public Path method_17969(String string) {
		return this.field_19759.resolve(string);
	}

	@Override
	public Path method_17968() {
		return this.field_19760;
	}
}
