package net.minecraft.world.chunk;

import com.google.common.collect.Lists;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.EntityCategory;
import net.minecraft.server.world.ChunkGenerator;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;

public class DebugChunkGenerator implements ChunkGenerator {
	private static final List<BlockState> field_10119 = Lists.newArrayList();
	private static final int field_10120;
	private static final int field_10121;
	protected static final BlockState field_12956 = Blocks.AIR.getDefaultState();
	protected static final BlockState field_12957 = Blocks.BARRIER.getDefaultState();
	private final World world;

	public DebugChunkGenerator(World world) {
		this.world = world;
	}

	@Override
	public Chunk generate(int x, int z) {
		ChunkBlockStateStorage chunkBlockStateStorage = new ChunkBlockStateStorage();

		for (int i = 0; i < 16; i++) {
			for (int j = 0; j < 16; j++) {
				int k = x * 16 + i;
				int l = z * 16 + j;
				chunkBlockStateStorage.set(i, 60, j, field_12957);
				BlockState blockState = method_9190(k, l);
				if (blockState != null) {
					chunkBlockStateStorage.set(i, 70, j, blockState);
				}
			}
		}

		Chunk chunk = new Chunk(this.world, chunkBlockStateStorage, x, z);
		chunk.calculateSkyLight();
		Biome[] biomes = this.world.method_3726().method_11540(null, x * 16, z * 16, 16, 16);
		byte[] bs = chunk.getBiomeArray();

		for (int m = 0; m < bs.length; m++) {
			bs[m] = (byte)Biome.getBiomeIndex(biomes[m]);
		}

		chunk.calculateSkyLight();
		return chunk;
	}

	public static BlockState method_9190(int i, int j) {
		BlockState blockState = field_12956;
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
	public void populate(int x, int z) {
	}

	@Override
	public boolean method_11762(Chunk chunk, int x, int z) {
		return false;
	}

	@Override
	public List<Biome.SpawnEntry> getSpawnEntries(EntityCategory category, BlockPos pos) {
		Biome biome = this.world.getBiome(pos);
		return biome.getSpawnEntries(category);
	}

	@Nullable
	@Override
	public BlockPos method_3866(World world, String string, BlockPos blockPos) {
		return null;
	}

	@Override
	public void method_4702(Chunk chunk, int x, int z) {
	}

	static {
		for (Block block : Block.REGISTRY) {
			field_10119.addAll(block.getStateManager().getBlockStates());
		}

		field_10120 = MathHelper.ceil(MathHelper.sqrt((float)field_10119.size()));
		field_10121 = MathHelper.ceil((float)field_10119.size() / (float)field_10120);
	}
}
