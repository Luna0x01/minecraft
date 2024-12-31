package net.minecraft.world;

import java.io.File;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.world.chunk.ChunkStorage;
import net.minecraft.world.dimension.Dimension;
import net.minecraft.world.level.LevelProperties;
import net.minecraft.world.level.storage.WorldSaveException;

public class EmptySaveHandler implements SaveHandler {
	@Override
	public LevelProperties getLevelProperties() {
		return null;
	}

	@Override
	public void readSessionLock() throws WorldSaveException {
	}

	@Override
	public ChunkStorage getChunkWriter(Dimension dim) {
		return null;
	}

	@Override
	public void saveWorld(LevelProperties properties, NbtCompound nbt) {
	}

	@Override
	public void saveWorld(LevelProperties properties) {
	}

	@Override
	public PlayerDataHandler getInstance() {
		return null;
	}

	@Override
	public void clear() {
	}

	@Override
	public File getDataFile(String fileName) {
		return null;
	}

	@Override
	public String getWorldName() {
		return "none";
	}

	@Override
	public File getWorldFolder() {
		return null;
	}
}
