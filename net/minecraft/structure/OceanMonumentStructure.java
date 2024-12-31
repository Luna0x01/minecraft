package net.minecraft.structure;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.Map.Entry;
import net.minecraft.entity.mob.GuardianEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.Biomes;
import net.minecraft.world.gen.GeneratorConfig;

public class OceanMonumentStructure extends StructureFeature {
	private int spacing = 32;
	private int separation = 5;
	public static final List<Biome> BIOMES = Arrays.asList(Biomes.OCEAN, Biomes.DEEP_OCEAN, Biomes.RIVER, Biomes.FROZEN_OCEAN, Biomes.FROZEN_RIVER);
	public static final List<Biome> field_13008 = Arrays.asList(Biomes.DEEP_OCEAN);
	private static final List<Biome.SpawnEntry> MONSTER_SPAWNS = Lists.newArrayList();

	public OceanMonumentStructure() {
	}

	public OceanMonumentStructure(Map<String, String> map) {
		this();

		for (Entry<String, String> entry : map.entrySet()) {
			if (((String)entry.getKey()).equals("spacing")) {
				this.spacing = MathHelper.parseInt((String)entry.getValue(), this.spacing, 1);
			} else if (((String)entry.getKey()).equals("separation")) {
				this.separation = MathHelper.parseInt((String)entry.getValue(), this.separation, 1);
			}
		}
	}

	@Override
	public String getName() {
		return "Monument";
	}

	@Override
	protected boolean shouldStartAt(int chunkX, int chunkZ) {
		int i = chunkX;
		int j = chunkZ;
		if (chunkX < 0) {
			chunkX -= this.spacing - 1;
		}

		if (chunkZ < 0) {
			chunkZ -= this.spacing - 1;
		}

		int k = chunkX / this.spacing;
		int l = chunkZ / this.spacing;
		Random random = this.world.getStructureRandom(k, l, 10387313);
		k *= this.spacing;
		l *= this.spacing;
		k += (random.nextInt(this.spacing - this.separation) + random.nextInt(this.spacing - this.separation)) / 2;
		l += (random.nextInt(this.spacing - this.separation) + random.nextInt(this.spacing - this.separation)) / 2;
		if (i == k && j == l) {
			if (!this.world.method_3726().method_3854(i * 16 + 8, j * 16 + 8, 16, field_13008)) {
				return false;
			}

			boolean bl = this.world.method_3726().method_3854(i * 16 + 8, j * 16 + 8, 29, BIOMES);
			if (bl) {
				return true;
			}
		}

		return false;
	}

	@Override
	protected GeneratorConfig getGeneratorConfig(int chunkX, int chunkZ) {
		return new OceanMonumentStructure.OceanMonumentGeneratorConfig(this.world, this.random, chunkX, chunkZ);
	}

	public List<Biome.SpawnEntry> getSpawnableMobs() {
		return MONSTER_SPAWNS;
	}

	static {
		MONSTER_SPAWNS.add(new Biome.SpawnEntry(GuardianEntity.class, 1, 2, 4));
	}

	public static class OceanMonumentGeneratorConfig extends GeneratorConfig {
		private final Set<ChunkPos> processedChunks = Sets.newHashSet();
		private boolean initialized;

		public OceanMonumentGeneratorConfig() {
		}

		public OceanMonumentGeneratorConfig(World world, Random random, int i, int j) {
			super(i, j);
			this.init(world, random, i, j);
		}

		private void init(World world, Random random, int chunkX, int chunkZ) {
			random.setSeed(world.getSeed());
			long l = random.nextLong();
			long m = random.nextLong();
			long n = (long)chunkX * l;
			long o = (long)chunkZ * m;
			random.setSeed(n ^ o ^ world.getSeed());
			int i = chunkX * 16 + 8 - 29;
			int j = chunkZ * 16 + 8 - 29;
			Direction direction = Direction.DirectionType.HORIZONTAL.getRandomDirection(random);
			this.field_13015.add(new OceanMonumentPieces.MainBuilding(random, i, j, direction));
			this.setBoundingBoxFromChildren();
			this.initialized = true;
		}

		@Override
		public void generateStructure(World world, Random random, BlockBox boundingBox) {
			if (!this.initialized) {
				this.field_13015.clear();
				this.init(world, random, this.getChunkX(), this.getChunkZ());
			}

			super.generateStructure(world, random, boundingBox);
		}

		@Override
		public boolean method_9277(ChunkPos chunkPos) {
			return this.processedChunks.contains(chunkPos) ? false : super.method_9277(chunkPos);
		}

		@Override
		public void method_9278(ChunkPos chunkPos) {
			super.method_9278(chunkPos);
			this.processedChunks.add(chunkPos);
		}

		@Override
		public void serialize(NbtCompound nbt) {
			super.serialize(nbt);
			NbtList nbtList = new NbtList();

			for (ChunkPos chunkPos : this.processedChunks) {
				NbtCompound nbtCompound = new NbtCompound();
				nbtCompound.putInt("X", chunkPos.x);
				nbtCompound.putInt("Z", chunkPos.z);
				nbtList.add(nbtCompound);
			}

			nbt.put("Processed", nbtList);
		}

		@Override
		public void deserialize(NbtCompound nbt) {
			super.deserialize(nbt);
			if (nbt.contains("Processed", 9)) {
				NbtList nbtList = nbt.getList("Processed", 10);

				for (int i = 0; i < nbtList.size(); i++) {
					NbtCompound nbtCompound = nbtList.getCompound(i);
					this.processedChunks.add(new ChunkPos(nbtCompound.getInt("X"), nbtCompound.getInt("Z")));
				}
			}
		}
	}
}
