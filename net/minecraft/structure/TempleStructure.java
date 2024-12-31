package net.minecraft.structure;

import com.google.common.collect.Lists;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Map.Entry;
import net.minecraft.entity.mob.WitchEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.Biomes;
import net.minecraft.world.gen.GeneratorConfig;

public class TempleStructure extends StructureFeature {
	private static final List<Biome> BIOMES = Arrays.asList(
		Biomes.DESERT, Biomes.DESERT_HILLS, Biomes.JUNGLE, Biomes.JUNGLE_HILLS, Biomes.SWAMP, Biomes.ICE_FLATS, Biomes.TAIGA_COLD
	);
	private final List<Biome.SpawnEntry> monsterSpawns = Lists.newArrayList();
	private int distance = 32;
	private final int field_4960 = 8;

	public TempleStructure() {
		this.monsterSpawns.add(new Biome.SpawnEntry(WitchEntity.class, 1, 1, 1));
	}

	public TempleStructure(Map<String, String> map) {
		this();

		for (Entry<String, String> entry : map.entrySet()) {
			if (((String)entry.getKey()).equals("distance")) {
				this.distance = MathHelper.parseInt((String)entry.getValue(), this.distance, 9);
			}
		}
	}

	@Override
	public String getName() {
		return "Temple";
	}

	@Override
	protected boolean shouldStartAt(int chunkX, int chunkZ) {
		int i = chunkX;
		int j = chunkZ;
		if (chunkX < 0) {
			chunkX -= this.distance - 1;
		}

		if (chunkZ < 0) {
			chunkZ -= this.distance - 1;
		}

		int k = chunkX / this.distance;
		int l = chunkZ / this.distance;
		Random random = this.world.getStructureRandom(k, l, 14357617);
		k *= this.distance;
		l *= this.distance;
		k += random.nextInt(this.distance - 8);
		l += random.nextInt(this.distance - 8);
		if (i == k && j == l) {
			Biome biome = this.world.method_3726().method_11535(new BlockPos(i * 16 + 8, 0, j * 16 + 8));
			if (biome == null) {
				return false;
			}

			for (Biome biome2 : BIOMES) {
				if (biome == biome2) {
					return true;
				}
			}
		}

		return false;
	}

	@Override
	public BlockPos method_9269(World world, BlockPos blockPos, boolean bl) {
		this.world = world;
		return method_13774(world, this, blockPos, this.distance, 8, 14357617, false, 100, bl);
	}

	@Override
	protected GeneratorConfig getGeneratorConfig(int chunkX, int chunkZ) {
		return new TempleStructure.TempleGeneratorConfig(this.world, this.random, chunkX, chunkZ);
	}

	public boolean isSwampHut(BlockPos pos) {
		GeneratorConfig generatorConfig = this.getGeneratorConfigAtPos(pos);
		if (generatorConfig != null && generatorConfig instanceof TempleStructure.TempleGeneratorConfig && !generatorConfig.field_13015.isEmpty()) {
			StructurePiece structurePiece = (StructurePiece)generatorConfig.field_13015.get(0);
			return structurePiece instanceof TemplePieces.SwampHut;
		} else {
			return false;
		}
	}

	public List<Biome.SpawnEntry> getMonsterSpawns() {
		return this.monsterSpawns;
	}

	public static class TempleGeneratorConfig extends GeneratorConfig {
		public TempleGeneratorConfig() {
		}

		public TempleGeneratorConfig(World world, Random random, int i, int j) {
			this(world, random, i, j, world.getBiome(new BlockPos(i * 16 + 8, 0, j * 16 + 8)));
		}

		public TempleGeneratorConfig(World world, Random random, int i, int j, Biome biome) {
			super(i, j);
			if (biome == Biomes.JUNGLE || biome == Biomes.JUNGLE_HILLS) {
				TemplePieces.JunglePyramid junglePyramid = new TemplePieces.JunglePyramid(random, i * 16, j * 16);
				this.field_13015.add(junglePyramid);
			} else if (biome == Biomes.SWAMP) {
				TemplePieces.SwampHut swampHut = new TemplePieces.SwampHut(random, i * 16, j * 16);
				this.field_13015.add(swampHut);
			} else if (biome == Biomes.DESERT || biome == Biomes.DESERT_HILLS) {
				TemplePieces.DesertPyramid desertPyramid = new TemplePieces.DesertPyramid(random, i * 16, j * 16);
				this.field_13015.add(desertPyramid);
			} else if (biome == Biomes.ICE_FLATS || biome == Biomes.TAIGA_COLD) {
				TemplePieces.class_2761 lv = new TemplePieces.class_2761(random, i * 16, j * 16);
				this.field_13015.add(lv);
			}

			this.setBoundingBoxFromChildren();
		}
	}
}
