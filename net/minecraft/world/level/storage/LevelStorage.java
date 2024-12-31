package net.minecraft.world.level.storage;

import com.google.common.collect.Lists;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.client.ClientException;
import net.minecraft.datafixer.DataFixerUpper;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtIo;
import net.minecraft.util.ProgressListener;
import net.minecraft.world.SaveHandler;
import net.minecraft.world.WorldSaveHandler;
import net.minecraft.world.level.LevelProperties;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class LevelStorage implements LevelStorageAccess {
	private static final Logger lOGGER = LogManager.getLogger();
	protected final File file;
	protected final DataFixerUpper field_13098;

	public LevelStorage(File file, DataFixerUpper dataFixerUpper) {
		this.field_13098 = dataFixerUpper;
		if (!file.exists()) {
			file.mkdirs();
		}

		this.file = file;
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
		File file = new File(this.file, name);
		if (!file.exists()) {
			return null;
		} else {
			File file2 = new File(file, "level.dat");
			if (file2.exists()) {
				LevelProperties levelProperties = method_11950(file2, this.field_13098);
				if (levelProperties != null) {
					return levelProperties;
				}
			}

			file2 = new File(file, "level.dat_old");
			return file2.exists() ? method_11950(file2, this.field_13098) : null;
		}
	}

	@Nullable
	public static LevelProperties method_11950(File file, DataFixerUpper dataFixerUpper) {
		try {
			NbtCompound nbtCompound = NbtIo.readCompressed(new FileInputStream(file));
			NbtCompound nbtCompound2 = nbtCompound.getCompound("Data");
			return new LevelProperties(dataFixerUpper.update(LevelDataType.LEVEL, nbtCompound2));
		} catch (Exception var4) {
			lOGGER.error("Exception reading {}", new Object[]{file, var4});
			return null;
		}
	}

	@Override
	public void renameLevel(String name, String newName) {
		File file = new File(this.file, name);
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
		File file = new File(this.file, name);
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
		File file = new File(this.file, name);
		if (!file.exists()) {
			return true;
		} else {
			lOGGER.info("Deleting level {}", new Object[]{name});

			for (int i = 1; i <= 5; i++) {
				lOGGER.info("Attempt {}...", new Object[]{i});
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
			lOGGER.debug("Deleting {}", new Object[]{file});
			if (file.isDirectory() && !deleteFilesRecursively(file.listFiles())) {
				lOGGER.warn("Couldn't delete directory {}", new Object[]{file});
				return false;
			}

			if (!file.delete()) {
				lOGGER.warn("Couldn't delete file {}", new Object[]{file});
				return false;
			}
		}

		return true;
	}

	@Override
	public SaveHandler createSaveHandler(String worldName, boolean createPlayerDataDir) {
		return new WorldSaveHandler(this.file, worldName, createPlayerDataDir, this.field_13098);
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
		File file = new File(this.file, name);
		return file.isDirectory();
	}

	@Override
	public File method_11957(String string, String string2) {
		return new File(new File(this.file, string), string2);
	}
}
