package net.minecraft.world;

import com.mojang.datafixers.DataFixer;
import java.io.File;
import javax.annotation.Nullable;
import net.minecraft.class_3998;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.world.chunk.ChunkStorage;
import net.minecraft.world.dimension.Dimension;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.level.LevelProperties;
import net.minecraft.world.level.storage.WorldSaveException;

public interface SaveHandler {
	@Nullable
	LevelProperties getLevelProperties();

	void readSessionLock() throws WorldSaveException;

	ChunkStorage getChunkWriter(Dimension dim);

	void saveWorld(LevelProperties properties, NbtCompound nbt);

	void saveWorld(LevelProperties properties);

	PlayerDataHandler getInstance();

	void clear();

	File getWorldFolder();

	@Nullable
	File method_243(DimensionType dimensionType, String string);

	class_3998 method_11956();

	DataFixer method_17967();
}
