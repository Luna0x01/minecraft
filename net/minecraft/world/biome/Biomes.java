package net.minecraft.world.biome;

import net.minecraft.Bootstrap;
import net.minecraft.util.Identifier;

public abstract class Biomes {
	public static final Biome OCEAN;
	public static final Biome DEFAULT;
	public static final Biome PLAINS;
	public static final Biome DESERT;
	public static final Biome EXTREME_HILLS;
	public static final Biome FOREST;
	public static final Biome TAIGA;
	public static final Biome SWAMP;
	public static final Biome RIVER;
	public static final Biome NETHER;
	public static final Biome SKY;
	public static final Biome FROZEN_OCEAN;
	public static final Biome FROZEN_RIVER;
	public static final Biome ICE_FLATS;
	public static final Biome ICE_MOUNTAINS;
	public static final Biome MUSHROOM_ISLAND;
	public static final Biome MUSHROOM_ISLAND_SHORE;
	public static final Biome BEACH;
	public static final Biome DESERT_HILLS;
	public static final Biome FOREST_HILLS;
	public static final Biome TAIGA_HILLS;
	public static final Biome ExTREME_HILLS_SMALLER;
	public static final Biome JUNGLE;
	public static final Biome JUNGLE_HILLS;
	public static final Biome JUNGLE_EDGE;
	public static final Biome DEEP_OCEAN;
	public static final Biome STONE_BEACH;
	public static final Biome COLD_BEACH;
	public static final Biome BIRCH_FOREST;
	public static final Biome BIRCH_FOREST_HILLS;
	public static final Biome ROOFED_FOREST;
	public static final Biome TAIGA_COLD;
	public static final Biome TAIGA_COLD_HILLS;
	public static final Biome REDWOOD_TAIGA;
	public static final Biome REDWOOD_TAIGA_HILLS;
	public static final Biome EXTREME_HILLS_WITH_TREES;
	public static final Biome SAVANNA;
	public static final Biome SAVANNA_ROCK;
	public static final Biome MESA;
	public static final Biome MESA_ROCK;
	public static final Biome MESA_CLEAR_ROCK;
	public static final Biome VOID;
	public static final Biome PLAINS_M;
	public static final Biome DESERT_M;
	public static final Biome EXTREME_HILLS_M;
	public static final Biome FOREST_M;
	public static final Biome TAIGA_M;
	public static final Biome SWAMP_M;
	public static final Biome ICE_FLATS_M;
	public static final Biome JUNGLE_M;
	public static final Biome JUNGLE_EDGE_M;
	public static final Biome BIRCH_FOREST_M;
	public static final Biome BIRCH_FOREST_HILLS_M;
	public static final Biome ROOFED_FOREST_M;
	public static final Biome TAIGA_COLD_M;
	public static final Biome MUTATED_REDWOOD_TAIGA;
	public static final Biome MUTATED_REDWOOD_TAIGA_HILLS;
	public static final Biome EXTREME_HILLS_WITH_TREES_M;
	public static final Biome SAVANNA_M;
	public static final Biome MUTATED_SAVANNA_ROCK;
	public static final Biome MESA_M;
	public static final Biome MUTATED_MESA_ROCK;
	public static final Biome MUTATED_MESA_CLEAR_ROCK;

	private static Biome getBiome(String biomeName) {
		Biome biome = Biome.REGISTRY.get(new Identifier(biomeName));
		if (biome == null) {
			throw new IllegalStateException("Invalid Biome requested: " + biomeName);
		} else {
			return biome;
		}
	}

	static {
		if (!Bootstrap.isInitialized()) {
			throw new RuntimeException("Accessed Biomes before Bootstrap!");
		} else {
			OCEAN = getBiome("ocean");
			DEFAULT = OCEAN;
			PLAINS = getBiome("plains");
			DESERT = getBiome("desert");
			EXTREME_HILLS = getBiome("extreme_hills");
			FOREST = getBiome("forest");
			TAIGA = getBiome("taiga");
			SWAMP = getBiome("swampland");
			RIVER = getBiome("river");
			NETHER = getBiome("hell");
			SKY = getBiome("sky");
			FROZEN_OCEAN = getBiome("frozen_ocean");
			FROZEN_RIVER = getBiome("frozen_river");
			ICE_FLATS = getBiome("ice_flats");
			ICE_MOUNTAINS = getBiome("ice_mountains");
			MUSHROOM_ISLAND = getBiome("mushroom_island");
			MUSHROOM_ISLAND_SHORE = getBiome("mushroom_island_shore");
			BEACH = getBiome("beaches");
			DESERT_HILLS = getBiome("desert_hills");
			FOREST_HILLS = getBiome("forest_hills");
			TAIGA_HILLS = getBiome("taiga_hills");
			ExTREME_HILLS_SMALLER = getBiome("smaller_extreme_hills");
			JUNGLE = getBiome("jungle");
			JUNGLE_HILLS = getBiome("jungle_hills");
			JUNGLE_EDGE = getBiome("jungle_edge");
			DEEP_OCEAN = getBiome("deep_ocean");
			STONE_BEACH = getBiome("stone_beach");
			COLD_BEACH = getBiome("cold_beach");
			BIRCH_FOREST = getBiome("birch_forest");
			BIRCH_FOREST_HILLS = getBiome("birch_forest_hills");
			ROOFED_FOREST = getBiome("roofed_forest");
			TAIGA_COLD = getBiome("taiga_cold");
			TAIGA_COLD_HILLS = getBiome("taiga_cold_hills");
			REDWOOD_TAIGA = getBiome("redwood_taiga");
			REDWOOD_TAIGA_HILLS = getBiome("redwood_taiga_hills");
			EXTREME_HILLS_WITH_TREES = getBiome("extreme_hills_with_trees");
			SAVANNA = getBiome("savanna");
			SAVANNA_ROCK = getBiome("savanna_rock");
			MESA = getBiome("mesa");
			MESA_ROCK = getBiome("mesa_rock");
			MESA_CLEAR_ROCK = getBiome("mesa_clear_rock");
			VOID = getBiome("void");
			PLAINS_M = getBiome("mutated_plains");
			DESERT_M = getBiome("mutated_desert");
			EXTREME_HILLS_M = getBiome("mutated_extreme_hills");
			FOREST_M = getBiome("mutated_forest");
			TAIGA_M = getBiome("mutated_taiga");
			SWAMP_M = getBiome("mutated_swampland");
			ICE_FLATS_M = getBiome("mutated_ice_flats");
			JUNGLE_M = getBiome("mutated_jungle");
			JUNGLE_EDGE_M = getBiome("mutated_jungle_edge");
			BIRCH_FOREST_M = getBiome("mutated_birch_forest");
			BIRCH_FOREST_HILLS_M = getBiome("mutated_birch_forest_hills");
			ROOFED_FOREST_M = getBiome("mutated_roofed_forest");
			TAIGA_COLD_M = getBiome("mutated_taiga_cold");
			MUTATED_REDWOOD_TAIGA = getBiome("mutated_redwood_taiga");
			MUTATED_REDWOOD_TAIGA_HILLS = getBiome("mutated_redwood_taiga_hills");
			EXTREME_HILLS_WITH_TREES_M = getBiome("mutated_extreme_hills_with_trees");
			SAVANNA_M = getBiome("mutated_savanna");
			MUTATED_SAVANNA_ROCK = getBiome("mutated_savanna_rock");
			MESA_M = getBiome("mutated_mesa");
			MUTATED_MESA_ROCK = getBiome("mutated_mesa_rock");
			MUTATED_MESA_CLEAR_ROCK = getBiome("mutated_mesa_clear_rock");
		}
	}
}
