package net.minecraft.world;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.chunk.light.LightingProvider;
import net.minecraft.world.level.ColorResolver;

public interface BlockRenderView extends BlockView {
	LightingProvider getLightingProvider();

	int getColor(BlockPos blockPos, ColorResolver colorResolver);

	default int getLightLevel(LightType lightType, BlockPos blockPos) {
		return this.getLightingProvider().get(lightType).getLightLevel(blockPos);
	}

	default int getBaseLightLevel(BlockPos blockPos, int i) {
		return this.getLightingProvider().getLight(blockPos, i);
	}

	default boolean isSkyVisible(BlockPos blockPos) {
		return this.getLightLevel(LightType.field_9284, blockPos) >= this.getMaxLightLevel();
	}
}
