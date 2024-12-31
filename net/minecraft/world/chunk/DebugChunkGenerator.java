package net.minecraft.world.chunk;

import com.google.common.collect.Lists;
import java.util.List;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.EntityCategory;
import net.minecraft.util.ProgressListener;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;

public class DebugChunkGenerator implements ChunkProvider {
	private static final List<BlockState> field_10119 = Lists.newArrayList();
	private static final int field_10120;
	private static final int field_10121;
	private final World world;

	public DebugChunkGenerator(World world) {
		this.world = world;
	}

	@Override
	public Chunk getChunk(int x, int z) {
		ChunkBlockStateStorage chunkBlockStateStorage = new ChunkBlockStateStorage();

		for (int i = 0; i < 16; i++) {
			for (int j = 0; j < 16; j++) {
				int k = x * 16 + i;
				int l = z * 16 + j;
				chunkBlockStateStorage.set(i, 60, j, Blocks.BARRIER.getDefaultState());
				BlockState blockState = method_9190(k, l);
				if (blockState != null) {
					chunkBlockStateStorage.set(i, 70, j, blockState);
				}
			}
		}

		Chunk chunk = new Chunk(this.world, chunkBlockStateStorage, x, z);
		chunk.calculateSkyLight();
		Biome[] biomes = this.world.getBiomeSource().method_3861(null, x * 16, z * 16, 16, 16);
		byte[] bs = chunk.getBiomeArray();

		for (int m = 0; m < bs.length; m++) {
			bs[m] = (byte)biomes[m].id;
		}

		chunk.calculateSkyLight();
		return chunk;
	}

	public static BlockState method_9190(int i, int j) {
		BlockState blockState = null;
		if (i > 0 && j > 0 && i % 2 != 0 && j % 2 != 0) {
			i /= 2;
			j /= 2;
			if (i <= field_10120 && j <= field_10121) {
				int k = MathHelper.abs(i * field_10120 + j);
				if (k < field_10119.size()) {
					blockState = (BlockState)field_10119.get(k);
				}
			}
		}

		return blockState;
	}

	@Override
	public boolean chunkExists(int x, int z) {
		return true;
	}

	@Override
	public void decorateChunk(ChunkProvider provider, int x, int z) {
	}

	@Override
	public boolean isChunkModified(ChunkProvider chunkProvider, Chunk chunk, int x, int z) {
		return false;
	}

	@Override
	public boolean saveChunks(boolean saveEntities, ProgressListener progressListener) {
		return true;
	}

	@Override
	public void flushChunks() {
	}

	@Override
	public boolean tickChunks() {
		return false;
	}

	@Override
	public boolean isSavingEnabled() {
		return true;
	}

	@Override
	public String getChunkProviderName() {
		return "DebugLevelSource";
	}

	@Override
	public List<Biome.SpawnEntry> getSpawnEntries(EntityCategory category, BlockPos pos) {
		Biome biome = this.world.getBiome(pos);
		return biome.getSpawnEntries(category);
	}

	@Override
	public BlockPos getNearestStructurePos(World world, String structureName, BlockPos pos) {
		return null;
	}

	@Override
	public int getLoadedChunksCount() {
		return 0;
	}

	@Override
	public void handleInitialLoad(Chunk chunk, int x, int z) {
	}

	@Override
	public Chunk getChunk(BlockPos pos) {
		return this.getChunk(pos.getX() >> 4, pos.getZ() >> 4);
	}

	static {
		for (Block block : Block.REGISTRY) {
			field_10119.addAll(block.getStateManager().getBlockStates());
		}

		field_10120 = MathHelper.ceil(MathHelper.sqrt((float)field_10119.size()));
		field_10121 = MathHelper.ceil((float)field_10119.size() / (float)field_10120);
	}
}
