package net.minecraft.world;

import java.io.File;
import javax.annotation.Nullable;
import net.minecraft.datafixer.DataFixerUpper;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.FileIoThread;
import net.minecraft.world.chunk.ChunkStorage;
import net.minecraft.world.chunk.RegionIo;
import net.minecraft.world.chunk.ThreadedAnvilChunkStorage;
import net.minecraft.world.dimension.Dimension;
import net.minecraft.world.dimension.TheEndDimension;
import net.minecraft.world.dimension.TheNetherDimension;
import net.minecraft.world.level.LevelProperties;

public class AnvilWorldSaveHandler extends WorldSaveHandler {
	public AnvilWorldSaveHandler(File file, String string, boolean bl, DataFixerUpper dataFixerUpper) {
		super(file, string, bl, dataFixerUpper);
	}

	@Override
	public ChunkStorage getChunkWriter(Dimension dim) {
		File file = this.getWorldFolder();
		if (dim instanceof TheNetherDimension) {
			File file2 = new File(file, "DIM-1");
			file2.mkdirs();
			return new ThreadedAnvilChunkStorage(file2, this.field_13096);
		} else if (dim instanceof TheEndDimension) {
			File file3 = new File(file, "DIM1");
			file3.mkdirs();
			return new ThreadedAnvilChunkStorage(file3, this.field_13096);
		} else {
			return new ThreadedAnvilChunkStorage(file, this.field_13096);
		}
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
