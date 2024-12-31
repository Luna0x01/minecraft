package net.minecraft.world;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtIo;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.chunk.ChunkStorage;
import net.minecraft.world.dimension.Dimension;
import net.minecraft.world.level.LevelProperties;
import net.minecraft.world.level.storage.WorldSaveException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class WorldSaveHandler implements SaveHandler, PlayerDataHandler {
	private static final Logger LOGGER = LogManager.getLogger();
	private final File worldDir;
	private final File playerDataDir;
	private final File dataDir;
	private final long startTime = MinecraftServer.getTimeMillis();
	private final String worldName;

	public WorldSaveHandler(File file, String string, boolean bl) {
		this.worldDir = new File(file, string);
		this.worldDir.mkdirs();
		this.playerDataDir = new File(this.worldDir, "playerdata");
		this.dataDir = new File(this.worldDir, "data");
		this.dataDir.mkdirs();
		this.worldName = string;
		if (bl) {
			this.playerDataDir.mkdirs();
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

	@Override
	public LevelProperties getLevelProperties() {
		File file = new File(this.worldDir, "level.dat");
		if (file.exists()) {
			try {
				NbtCompound nbtCompound = NbtIo.readCompressed(new FileInputStream(file));
				NbtCompound nbtCompound2 = nbtCompound.getCompound("Data");
				return new LevelProperties(nbtCompound2);
			} catch (Exception var5) {
				var5.printStackTrace();
			}
		}

		file = new File(this.worldDir, "level.dat_old");
		if (file.exists()) {
			try {
				NbtCompound nbtCompound3 = NbtIo.readCompressed(new FileInputStream(file));
				NbtCompound nbtCompound4 = nbtCompound3.getCompound("Data");
				return new LevelProperties(nbtCompound4);
			} catch (Exception var4) {
				var4.printStackTrace();
			}
		}

		return null;
	}

	@Override
	public void saveWorld(LevelProperties properties, NbtCompound nbt) {
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
		NbtCompound nbtCompound = properties.toNbt();
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
		} catch (Exception var7) {
			var7.printStackTrace();
		}
	}

	@Override
	public void savePlayerData(PlayerEntity player) {
		try {
			NbtCompound nbtCompound = new NbtCompound();
			player.writePlayerData(nbtCompound);
			File file = new File(this.playerDataDir, player.getUuid().toString() + ".dat.tmp");
			File file2 = new File(this.playerDataDir, player.getUuid().toString() + ".dat");
			NbtIo.writeCompressed(nbtCompound, new FileOutputStream(file));
			if (file2.exists()) {
				file2.delete();
			}

			file.renameTo(file2);
		} catch (Exception var5) {
			LOGGER.warn("Failed to save player data for " + player.getTranslationKey());
		}
	}

	@Override
	public NbtCompound getPlayerData(PlayerEntity player) {
		NbtCompound nbtCompound = null;

		try {
			File file = new File(this.playerDataDir, player.getUuid().toString() + ".dat");
			if (file.exists() && file.isFile()) {
				nbtCompound = NbtIo.readCompressed(new FileInputStream(file));
			}
		} catch (Exception var4) {
			LOGGER.warn("Failed to load player data for " + player.getTranslationKey());
		}

		if (nbtCompound != null) {
			player.fromNbt(nbtCompound);
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
	public File getDataFile(String fileName) {
		return new File(this.dataDir, fileName + ".dat");
	}

	@Override
	public String getWorldName() {
		return this.worldName;
	}
}
