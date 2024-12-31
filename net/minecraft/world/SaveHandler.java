package net.minecraft.world;

import java.io.File;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.structure.class_2763;
import net.minecraft.world.chunk.ChunkStorage;
import net.minecraft.world.dimension.Dimension;
import net.minecraft.world.level.LevelProperties;
import net.minecraft.world.level.storage.WorldSaveException;

public interface SaveHandler {
	LevelProperties getLevelProperties();

	void readSessionLock() throws WorldSaveException;

	ChunkStorage getChunkWriter(Dimension dim);

	void saveWorld(LevelProperties properties, NbtCompound nbt);

	void saveWorld(LevelProperties properties);

	PlayerDataHandler getInstance();

	void clear();

	File getWorldFolder();

	File getDataFile(String fileName);

	class_2763 method_11956();
}
