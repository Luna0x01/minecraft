package net.minecraft.world.chunk;

import com.google.common.collect.Lists;
import java.util.List;
import java.util.Map;
import java.util.Random;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.EntityCategory;
import net.minecraft.structure.MineshaftStructure;
import net.minecraft.structure.OceanMonumentStructure;
import net.minecraft.structure.StrongholdStructure;
import net.minecraft.structure.StructureFeature;
import net.minecraft.structure.TempleStructure;
import net.minecraft.structure.VillageStructure;
import net.minecraft.util.ProgressListener;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.FlatWorldHelper;
import net.minecraft.world.gen.carver.Carver;
import net.minecraft.world.gen.feature.DungeonFeature;
import net.minecraft.world.gen.feature.LakesFeature;
import net.minecraft.world.gen.layer.FlatWorldLayer;

public class FlatChunkGenerator implements ChunkProvider {
	private World world;
	private Random random;
	private final BlockState[] field_10123 = new BlockState[256];
	private final FlatWorldHelper field_4940;
	private final List<StructureFeature> field_4941 = Lists.newArrayList();
	private final boolean field_4942;
	private final boolean field_4943;
	private LakesFeature field_4944;
	private LakesFeature field_4945;

	public FlatChunkGenerator(World world, long l, boolean bl, String string) {
		this.world = world;
		this.random = new Random(l);
		this.field_4940 = FlatWorldHelper.getHelper(string);
		if (bl) {
			Map<String, Map<String, String>> map = this.field_4940.getStructures();
			if (map.containsKey("village")) {
				Map<String, String> map2 = (Map<String, String>)map.get("village");
				if (!map2.containsKey("size")) {
					map2.put("size", "1");
				}

				this.field_4941.add(new VillageStructure(map2));
			}

			if (map.containsKey("biome_1")) {
				this.field_4941.add(new TempleStructure((Map<String, String>)map.get("biome_1")));
			}

			if (map.containsKey("mineshaft")) {
				this.field_4941.add(new MineshaftStructure((Map<String, String>)map.get("mineshaft")));
			}

			if (map.containsKey("stronghold")) {
				this.field_4941.add(new StrongholdStructure((Map<String, String>)map.get("stronghold")));
			}

			if (map.containsKey("oceanmonument")) {
				this.field_4941.add(new OceanMonumentStructure((Map<String, String>)map.get("oceanmonument")));
			}
		}

		if (this.field_4940.getStructures().containsKey("lake")) {
			this.field_4944 = new LakesFeature(Blocks.WATER);
		}

		if (this.field_4940.getStructures().containsKey("lava_lake")) {
			this.field_4945 = new LakesFeature(Blocks.LAVA);
		}

		this.field_4943 = this.field_4940.getStructures().containsKey("dungeon");
		int i = 0;
		int j = 0;
		boolean bl2 = true;

		for (FlatWorldLayer flatWorldLayer : this.field_4940.getLayers()) {
			for (int k = flatWorldLayer.method_4111(); k < flatWorldLayer.method_4111() + flatWorldLayer.getThickness(); k++) {
				BlockState blockState = flatWorldLayer.getBlockState();
				if (blockState.getBlock() != Blocks.AIR) {
					bl2 = false;
					this.field_10123[k] = blockState;
				}
			}

			if (flatWorldLayer.getBlockState().getBlock() == Blocks.AIR) {
				j += flatWorldLayer.getThickness();
			} else {
				i += flatWorldLayer.getThickness() + j;
				j = 0;
			}
		}

		world.setSeaLevel(i);
		this.field_4942 = bl2 ? false : this.field_4940.getStructures().containsKey("decoration");
	}

	@Override
	public Chunk getChunk(int x, int z) {
		ChunkBlockStateStorage chunkBlockStateStorage = new ChunkBlockStateStorage();

		for (int i = 0; i < this.field_10123.length; i++) {
			BlockState blockState = this.field_10123[i];
			if (blockState != null) {
				for (int j = 0; j < 16; j++) {
					for (int k = 0; k < 16; k++) {
						chunkBlockStateStorage.set(j, i, k, blockState);
					}
				}
			}
		}

		for (Carver carver : this.field_4941) {
			carver.carveRegion(this, this.world, x, z, chunkBlockStateStorage);
		}

		Chunk chunk = new Chunk(this.world, chunkBlockStateStorage, x, z);
		Biome[] biomes = this.world.getBiomeSource().method_3861(null, x * 16, z * 16, 16, 16);
		byte[] bs = chunk.getBiomeArray();

		for (int l = 0; l < bs.length; l++) {
			bs[l] = (byte)biomes[l].id;
		}

		chunk.calculateSkyLight();
		return chunk;
	}

	@Override
	public boolean chunkExists(int x, int z) {
		return true;
	}

	@Override
	public void decorateChunk(ChunkProvider provider, int x, int z) {
		int i = x * 16;
		int j = z * 16;
		BlockPos blockPos = new BlockPos(i, 0, j);
		Biome biome = this.world.getBiome(new BlockPos(i + 16, 0, j + 16));
		boolean bl = false;
		this.random.setSeed(this.world.getSeed());
		long l = this.random.nextLong() / 2L * 2L + 1L;
		long m = this.random.nextLong() / 2L * 2L + 1L;
		this.random.setSeed((long)x * l + (long)z * m ^ this.world.getSeed());
		ChunkPos chunkPos = new ChunkPos(x, z);

		for (StructureFeature structureFeature : this.field_4941) {
			boolean bl2 = structureFeature.populate(this.world, this.random, chunkPos);
			if (structureFeature instanceof VillageStructure) {
				bl |= bl2;
			}
		}

		if (this.field_4944 != null && !bl && this.random.nextInt(4) == 0) {
			this.field_4944.generate(this.world, this.random, blockPos.add(this.random.nextInt(16) + 8, this.random.nextInt(256), this.random.nextInt(16) + 8));
		}

		if (this.field_4945 != null && !bl && this.random.nextInt(8) == 0) {
			BlockPos blockPos2 = blockPos.add(this.random.nextInt(16) + 8, this.random.nextInt(this.random.nextInt(248) + 8), this.random.nextInt(16) + 8);
			if (blockPos2.getY() < this.world.getSeaLevel() || this.random.nextInt(10) == 0) {
				this.field_4945.generate(this.world, this.random, blockPos2);
			}
		}

		if (this.field_4943) {
			for (int k = 0; k < 8; k++) {
				new DungeonFeature().generate(this.world, this.random, blockPos.add(this.random.nextInt(16) + 8, this.random.nextInt(256), this.random.nextInt(16) + 8));
			}
		}

		if (this.field_4942) {
			biome.decorate(this.world, this.random, blockPos);
		}
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
		return "FlatLevelSource";
	}

	@Override
	public List<Biome.SpawnEntry> getSpawnEntries(EntityCategory category, BlockPos pos) {
		Biome biome = this.world.getBiome(pos);
		return biome.getSpawnEntries(category);
	}

	@Override
	public BlockPos getNearestStructurePos(World world, String structureName, BlockPos pos) {
		if ("Stronghold".equals(structureName)) {
			for (StructureFeature structureFeature : this.field_4941) {
				if (structureFeature instanceof StrongholdStructure) {
					return structureFeature.method_9269(world, pos);
				}
			}
		}

		return null;
	}

	@Override
	public int getLoadedChunksCount() {
		return 0;
	}

	@Override
	public void handleInitialLoad(Chunk chunk, int x, int z) {
		for (StructureFeature structureFeature : this.field_4941) {
			structureFeature.carveRegion(this, this.world, x, z, null);
		}
	}

	@Override
	public Chunk getChunk(BlockPos pos) {
		return this.getChunk(pos.getX() >> 4, pos.getZ() >> 4);
	}
}
