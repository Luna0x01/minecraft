package net.minecraft;

import com.google.common.collect.Sets;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.Set;
import javax.annotation.Nullable;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeCache;
import net.minecraft.world.biome.Biomes;
import net.minecraft.world.biome.SingletonBiomeSource;
import net.minecraft.world.biome.layer.Layer;
import net.minecraft.world.level.LevelProperties;

public class class_3659 extends SingletonBiomeSource {
	private final BiomeCache field_17701 = new BiomeCache(this);
	private final Layer field_17702;
	private final Layer field_17703;
	private final Biome[] field_17704 = new Biome[]{
		Biomes.OCEAN,
		Biomes.PLAINS,
		Biomes.DESERT,
		Biomes.EXTREME_HILLS,
		Biomes.FOREST,
		Biomes.TAIGA,
		Biomes.SWAMP,
		Biomes.RIVER,
		Biomes.FROZEN_OCEAN,
		Biomes.FROZEN_RIVER,
		Biomes.ICE_FLATS,
		Biomes.ICE_MOUNTAINS,
		Biomes.MUSHROOM_ISLAND,
		Biomes.MUSHROOM_ISLAND_SHORE,
		Biomes.BEACH,
		Biomes.DESERT_HILLS,
		Biomes.FOREST_HILLS,
		Biomes.TAIGA_HILLS,
		Biomes.ExTREME_HILLS_SMALLER,
		Biomes.JUNGLE,
		Biomes.JUNGLE_HILLS,
		Biomes.JUNGLE_EDGE,
		Biomes.DEEP_OCEAN,
		Biomes.STONE_BEACH,
		Biomes.COLD_BEACH,
		Biomes.BIRCH_FOREST,
		Biomes.BIRCH_FOREST_HILLS,
		Biomes.ROOFED_FOREST,
		Biomes.TAIGA_COLD,
		Biomes.TAIGA_COLD_HILLS,
		Biomes.GIANT_TREE_TAIGA,
		Biomes.GIANT_TREE_TAIGA_HILLS,
		Biomes.EXTREME_HILLS_WITH_TREES,
		Biomes.SAVANNA,
		Biomes.SAVANNA_PLATEAU,
		Biomes.MESA,
		Biomes.WOODED_BADLANDS_PLATEAU,
		Biomes.BADLANDS_PLATEAU,
		Biomes.WARM_OCEAN,
		Biomes.LUKEWARM_OCEAN,
		Biomes.COLD_OCEAN,
		Biomes.DEEP_WARM_OCEAN,
		Biomes.DEEP_LUKEWARM_OCEAN,
		Biomes.DEEP_COLD_OCEAN,
		Biomes.DEEP_FROZEN_OCEAN,
		Biomes.PLAINS_M,
		Biomes.DESERT_M,
		Biomes.EXTREME_HILLS_M,
		Biomes.FOREST_M,
		Biomes.TAIGA_M,
		Biomes.SWAMP_M,
		Biomes.ICE_FLATS_M,
		Biomes.JUNGLE_M,
		Biomes.JUNGLE_EDGE_M,
		Biomes.BIRCH_FOREST_M,
		Biomes.BIRCH_FOREST_HILLS_M,
		Biomes.ROOFED_FOREST_M,
		Biomes.TAIGA_COLD_M,
		Biomes.GIANT_SPRUCE_TAIGA,
		Biomes.GIANT_SPRUCE_TAIGA_HILLS,
		Biomes.EXTREME_HILLS_WITH_TREES_M,
		Biomes.SAVANNA_M,
		Biomes.SHATTERED_SAVANNA_PLATEAU,
		Biomes.MESA_M,
		Biomes.MODIFIED_WOODED_BADLANDS_PLATEAU,
		Biomes.MODIFIED_BADLANDS_PLATEAU
	};

	public class_3659(class_3660 arg) {
		LevelProperties levelProperties = arg.method_16533();
		class_3809 lv = arg.method_16536();
		Layer[] layers = class_4046.method_17859(levelProperties.getSeed(), levelProperties.getGeneratorType(), lv);
		this.field_17702 = layers[0];
		this.field_17703 = layers[1];
	}

	@Nullable
	@Override
	public Biome method_16480(BlockPos blockPos, @Nullable Biome biome) {
		return this.field_17701.method_3843(blockPos.getX(), blockPos.getZ(), biome);
	}

	@Override
	public Biome[] method_16476(int i, int j, int k, int l) {
		return this.field_17702.method_17856(i, j, k, l, Biomes.DEFAULT);
	}

	@Override
	public Biome[] method_16477(int i, int j, int k, int l, boolean bl) {
		return bl && k == 16 && l == 16 && (i & 15) == 0 && (j & 15) == 0
			? this.field_17701.method_3844(i, j)
			: this.field_17703.method_17856(i, j, k, l, Biomes.DEFAULT);
	}

	@Override
	public Set<Biome> method_16475(int i, int j, int k) {
		int l = i - k >> 2;
		int m = j - k >> 2;
		int n = i + k >> 2;
		int o = j + k >> 2;
		int p = n - l + 1;
		int q = o - m + 1;
		Set<Biome> set = Sets.newHashSet();
		Collections.addAll(set, this.field_17702.method_17856(l, m, p, q, null));
		return set;
	}

	@Nullable
	@Override
	public BlockPos method_16478(int i, int j, int k, List<Biome> list, Random random) {
		int l = i - k >> 2;
		int m = j - k >> 2;
		int n = i + k >> 2;
		int o = j + k >> 2;
		int p = n - l + 1;
		int q = o - m + 1;
		Biome[] biomes = this.field_17702.method_17856(l, m, p, q, null);
		BlockPos blockPos = null;
		int r = 0;

		for (int s = 0; s < p * q; s++) {
			int t = l + s % p << 2;
			int u = m + s / p << 2;
			if (list.contains(biomes[s])) {
				if (blockPos == null || random.nextInt(r + 1) == 0) {
					blockPos = new BlockPos(t, 0, u);
				}

				r++;
			}
		}

		return blockPos;
	}

	@Override
	public boolean method_16479(class_3902<?> arg) {
		return (Boolean)this.field_17661.computeIfAbsent(arg, argx -> {
			for (Biome biome : this.field_17704) {
				if (biome.method_16435(argx)) {
					return true;
				}
			}

			return false;
		});
	}

	@Override
	public Set<BlockState> method_16481() {
		if (this.field_17662.isEmpty()) {
			for (Biome biome : this.field_17704) {
				this.field_17662.add(biome.method_16450().method_17720());
			}
		}

		return this.field_17662;
	}

	@Override
	public void tick() {
		this.field_17701.method_3840();
	}
}
