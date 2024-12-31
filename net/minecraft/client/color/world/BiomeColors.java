package net.minecraft.client.color.world;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockView;
import net.minecraft.world.biome.Biome;

public class BiomeColors {
	private static final BiomeColors.ColorProvider GRASS_COLOR = new BiomeColors.ColorProvider() {
		@Override
		public int getColorAtPos(Biome biome, BlockPos pos) {
			return biome.getGrassColor(pos);
		}
	};
	private static final BiomeColors.ColorProvider FOLIAGE_COLOR = new BiomeColors.ColorProvider() {
		@Override
		public int getColorAtPos(Biome biome, BlockPos pos) {
			return biome.getFoliageColor(pos);
		}
	};
	private static final BiomeColors.ColorProvider WATER_COLOR = new BiomeColors.ColorProvider() {
		@Override
		public int getColorAtPos(Biome biome, BlockPos pos) {
			return biome.getWaterColor();
		}
	};

	private static int getColor(BlockView view, BlockPos pos, BiomeColors.ColorProvider provider) {
		int i = 0;
		int j = 0;
		int k = 0;

		for (BlockPos.Mutable mutable : BlockPos.mutableIterate(pos.add(-1, 0, -1), pos.add(1, 0, 1))) {
			int l = provider.getColorAtPos(view.getBiome(mutable), mutable);
			i += (l & 0xFF0000) >> 16;
			j += (l & 0xFF00) >> 8;
			k += l & 0xFF;
		}

		return (i / 9 & 0xFF) << 16 | (j / 9 & 0xFF) << 8 | k / 9 & 0xFF;
	}

	public static int getGrassColor(BlockView view, BlockPos pos) {
		return getColor(view, pos, GRASS_COLOR);
	}

	public static int getFoliageColor(BlockView view, BlockPos pos) {
		return getColor(view, pos, FOLIAGE_COLOR);
	}

	public static int getWaterColor(BlockView view, BlockPos pos) {
		return getColor(view, pos, WATER_COLOR);
	}

	interface ColorProvider {
		int getColorAtPos(Biome biome, BlockPos pos);
	}
}
