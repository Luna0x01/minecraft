package net.minecraft.client.color.world;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockRenderView;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.level.ColorResolver;

public class BiomeColors {
	public static final ColorResolver GRASS_COLOR = Biome::getGrassColorAt;
	public static final ColorResolver FOLIAGE_COLOR = (biome, d, e) -> biome.getFoliageColor();
	public static final ColorResolver WATER_COLOR = (biome, d, e) -> biome.getWaterColor();

	private static int getColor(BlockRenderView blockRenderView, BlockPos blockPos, ColorResolver colorResolver) {
		return blockRenderView.getColor(blockPos, colorResolver);
	}

	public static int getGrassColor(BlockRenderView blockRenderView, BlockPos blockPos) {
		return getColor(blockRenderView, blockPos, GRASS_COLOR);
	}

	public static int getFoliageColor(BlockRenderView blockRenderView, BlockPos blockPos) {
		return getColor(blockRenderView, blockPos, FOLIAGE_COLOR);
	}

	public static int getWaterColor(BlockRenderView blockRenderView, BlockPos blockPos) {
		return getColor(blockRenderView, blockPos, WATER_COLOR);
	}
}
