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

	@Nullable
	@Override
	public File method_243(DimensionType dimensionType, String string) {
		return null;
	}

	@Override
	public File getWorldFolder() {
		return null;
	}

	@Override
	public class_3998 method_11956() {
		return null;
	}

	@Override
	public DataFixer method_17967() {
		return null;
	}
}
