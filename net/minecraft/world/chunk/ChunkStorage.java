package net.minecraft.world.chunk;

import java.io.IOException;
import javax.annotation.Nullable;
import net.minecraft.world.World;
import net.minecraft.world.level.storage.WorldSaveException;

public interface ChunkStorage {
	@Nullable
	Chunk loadChunk(World world, int x, int z) throws IOException;

	void writeChunk(World world, Chunk chunk) throws IOException, WorldSaveException;

	void writeEntities(World world, Chunk chunk) throws IOException;

	void method_3950();

	void save();

	boolean chunkExists(int x, int z);
}
