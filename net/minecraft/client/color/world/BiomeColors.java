package net.minecraft.client.color.world;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockRenderView;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.level.ColorResolver;

public class BiomeColors {
	public static final ColorResolver GRASS_COLOR = Biome::getGrassColorAt;
	public static final ColorResolver FOLIAGE_COLOR = (biome, x, z) -> biome.getFoliageColor();
	public static final ColorResolver WATER_COLOR = (biome, x, z) -> biome.getWaterColor();

	private static int getColor(BlockRenderView world, BlockPos pos, ColorResolver resolver) {
		return world.getColor(pos, resolver);
	}

	public static int getGrassColor(BlockRenderView world, BlockPos pos) {
		return getColor(world, pos, GRASS_COLOR);
	}

	public static int getFoliageColor(BlockRenderView world, BlockPos pos) {
		return getColor(world, pos, FOLIAGE_COLOR);
	}

	public static int getWaterColor(BlockRenderView world, BlockPos pos) {
		return getColor(world, pos, WATER_COLOR);
	}
}
