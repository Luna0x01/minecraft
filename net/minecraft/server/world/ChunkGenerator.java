package net.minecraft.server.world;

import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.entity.EntityCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.Chunk;

public interface ChunkGenerator {
	Chunk generate(int x, int z);

	void populate(int x, int z);

	boolean method_11762(Chunk chunk, int x, int z);

	List<Biome.SpawnEntry> getSpawnEntries(EntityCategory category, BlockPos pos);

	@Nullable
	BlockPos method_3866(World world, String string, BlockPos blockPos);

	void method_4702(Chunk chunk, int x, int z);
}
