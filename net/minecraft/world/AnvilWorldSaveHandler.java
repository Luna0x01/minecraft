package net.minecraft.world;

import com.mojang.datafixers.DataFixer;
import java.io.File;
import javax.annotation.Nullable;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.FileIoThread;
import net.minecraft.world.chunk.ChunkStorage;
import net.minecraft.world.chunk.RegionIo;
import net.minecraft.world.chunk.ThreadedAnvilChunkStorage;
import net.minecraft.world.dimension.Dimension;
import net.minecraft.world.level.LevelProperties;

public class AnvilWorldSaveHandler extends WorldSaveHandler {
	public AnvilWorldSaveHandler(File file, String string, @Nullable MinecraftServer minecraftServer, DataFixer dataFixer) {
		super(file, string, minecraftServer, dataFixer);
	}

	@Override
	public ChunkStorage getChunkWriter(Dimension dim) {
		File file = dim.method_11789().method_17197(this.getWorldFolder());
		file.mkdirs();
		return new ThreadedAnvilChunkStorage(file, this.field_19755);
	}

	@Override
	public void saveWorld(LevelProperties properties, @Nullable NbtCompound nbt) {
		properties.setVersion(19133);
		super.saveWorld(properties, nbt);
	}

	@Override
	public void clear() {
		try {
			FileIoThread.getInstance().waitUntilComplete();
		} catch (InterruptedException var2) {
			var2.printStackTrace();
		}

		RegionIo.clearRegionFormats();
	}
}
