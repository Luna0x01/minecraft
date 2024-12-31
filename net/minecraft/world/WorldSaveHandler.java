package net.minecraft.world;

import com.mojang.datafixers.DataFixTypes;
import com.mojang.datafixers.DataFixer;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import javax.annotation.Nullable;
import net.minecraft.class_3998;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.nbt.NbtIo;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.Util;
import net.minecraft.world.chunk.ChunkStorage;
import net.minecraft.world.dimension.Dimension;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.level.LevelProperties;
import net.minecraft.world.level.storage.LevelStorage;
import net.minecraft.world.level.storage.WorldSaveException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class WorldSaveHandler implements SaveHandler, PlayerDataHandler {
	private static final Logger LOGGER = LogManager.getLogger();
	private final File worldDir;
	private final File playerDataDir;
	private final long startTime = Util.method_20227();
	private final String worldName;
	private final class_3998 field_19756;
	protected final DataFixer field_19755;

	public WorldSaveHandler(File file, String string, @Nullable MinecraftServer minecraftServer, DataFixer dataFixer) {
		this.field_19755 = dataFixer;
		this.worldDir = new File(file, string);
		this.worldDir.mkdirs();
		this.playerDataDir = new File(this.worldDir, "playerdata");
		this.worldName = string;
		if (minecraftServer != null) {
			this.playerDataDir.mkdirs();
			this.field_19756 = new class_3998(minecraftServer, this.worldDir, dataFixer);
		} else {
			this.field_19756 = null;
		}

		this.writeSessionLock();
	}

	private void writeSessionLock() {
		try {
			File file = new File(this.worldDir, "session.lock");
			DataOutputStream dataOutputStream = new DataOutputStream(new FileOutputStream(file));

			try {
				dataOutputStream.writeLong(this.startTime);
			} finally {
				dataOutputStream.close();
			}
		} catch (IOException var7) {
			var7.printStackTrace();
			throw new RuntimeException("Failed to check session lock, aborting");
		}
	}

	@Override
	public File getWorldFolder() {
		return this.worldDir;
	}

	@Override
	public void readSessionLock() throws WorldSaveException {
		try {
			File file = new File(this.worldDir, "session.lock");
			DataInputStream dataInputStream = new DataInputStream(new FileInputStream(file));

			try {
				if (dataInputStream.readLong() != this.startTime) {
					throw new WorldSaveException("The save is being accessed from another location, aborting");
				}
			} finally {
				dataInputStream.close();
			}
		} catch (IOException var7) {
			throw new WorldSaveException("Failed to check session lock, aborting");
		}
	}

	@Override
	public ChunkStorage getChunkWriter(Dimension dim) {
		throw new RuntimeException("Old Chunk Storage is no longer supported.");
	}

	@Nullable
	@Override
	public LevelProperties getLevelProperties() {
		File file = new File(this.worldDir, "level.dat");
		if (file.exists()) {
			LevelProperties levelProperties = LevelStorage.method_17949(file, this.field_19755);
			if (levelProperties != null) {
				return levelProperties;
			}
		}

		file = new File(this.worldDir, "level.dat_old");
		return file.exists() ? LevelStorage.method_17949(file, this.field_19755) : null;
	}

	@Override
	public void saveWorld(LevelProperties properties, @Nullable NbtCompound nbt) {
		NbtCompound nbtCompound = properties.toNbt(nbt);
		NbtCompound nbtCompound2 = new NbtCompound();
		nbtCompound2.put("Data", nbtCompound);

		try {
			File file = new File(this.worldDir, "level.dat_new");
			File file2 = new File(this.worldDir, "level.dat_old");
			File file3 = new File(this.worldDir, "level.dat");
			NbtIo.writeCompressed(nbtCompound2, new FileOutputStream(file));
			if (file2.exists()) {
				file2.delete();
			}

			file3.renameTo(file2);
			if (file3.exists()) {
				file3.delete();
			}

			file.renameTo(file3);
			if (file.exists()) {
				file.delete();
			}
		} catch (Exception var8) {
			var8.printStackTrace();
		}
	}

	@Override
	public void saveWorld(LevelProperties properties) {
		this.saveWorld(properties, null);
	}

	@Override
	public void savePlayerData(PlayerEntity player) {
		try {
			NbtCompound nbtCompound = player.toNbt(new NbtCompound());
			File file = new File(this.playerDataDir, player.getEntityName() + ".dat.tmp");
			File file2 = new File(this.playerDataDir, player.getEntityName() + ".dat");
			NbtIo.writeCompressed(nbtCompound, new FileOutputStream(file));
			if (file2.exists()) {
				file2.delete();
			}

			file.renameTo(file2);
		} catch (Exception var5) {
			LOGGER.warn("Failed to save player data for {}", player.method_15540().getString());
		}
	}

	@Nullable
	@Override
	public NbtCompound getPlayerData(PlayerEntity player) {
		NbtCompound nbtCompound = null;

		try {
			File file = new File(this.playerDataDir, player.getEntityName() + ".dat");
			if (file.exists() && file.isFile()) {
				nbtCompound = NbtIo.readCompressed(new FileInputStream(file));
			}
		} catch (Exception var4) {
			LOGGER.warn("Failed to load player data for {}", player.method_15540().getString());
		}

		if (nbtCompound != null) {
			int i = nbtCompound.contains("DataVersion", 3) ? nbtCompound.getInt("DataVersion") : -1;
			player.fromNbt(NbtHelper.method_20141(this.field_19755, DataFixTypes.PLAYER, nbtCompound, i));
		}

		return nbtCompound;
	}

	@Override
	public PlayerDataHandler getInstance() {
		return this;
	}

	@Override
	public String[] getSavedPlayerIds() {
		String[] strings = this.playerDataDir.list();
		if (strings == null) {
			strings = new String[0];
		}

		for (int i = 0; i < strings.length; i++) {
			if (strings[i].endsWith(".dat")) {
				strings[i] = strings[i].substring(0, strings[i].length() - 4);
			}
		}

		return strings;
	}

	@Override
	public void clear() {
	}

	@Override
	public File method_243(DimensionType dimensionType, String string) {
		File file = new File(dimensionType.method_17197(this.worldDir), "data");
		file.mkdirs();
		return new File(file, string + ".dat");
	}

	@Override
	public class_3998 method_11956() {
		return this.field_19756;
	}

	@Override
	public DataFixer method_17967() {
		return this.field_19755;
	}
}
