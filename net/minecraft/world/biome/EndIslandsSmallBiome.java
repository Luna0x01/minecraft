package net.minecraft.world.biome;

import net.minecraft.entity.EntityCategory;
import net.minecraft.entity.EntityType;
import net.minecraft.world.gen.GenerationStep;
import net.minecraft.world.gen.decorator.Decorator;
import net.minecraft.world.gen.decorator.DecoratorConfig;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.FeatureConfig;
import net.minecraft.world.gen.surfacebuilder.SurfaceBuilder;

public class EndIslandsSmallBiome extends Biome {
	public EndIslandsSmallBiome() {
		super(
			new Biome.Settings()
				.configureSurfaceBuilder(SurfaceBuilder.field_15701, SurfaceBuilder.END_CONFIG)
				.precipitation(Biome.Precipitation.NONE)
				.category(Biome.Category.THEEND)
				.depth(0.1F)
				.scale(0.2F)
				.temperature(0.5F)
				.downfall(0.5F)
				.waterColor(4159204)
				.waterFogColor(329011)
				.parent(null)
		);
		this.addFeature(
			GenerationStep.Feature.field_13174, configureFeature(Feature.field_13574, FeatureConfig.DEFAULT, Decorator.field_14251, DecoratorConfig.DEFAULT)
		);
		DefaultBiomeFeatures.method_20826(this);
		this.addSpawn(EntityCategory.field_6302, new Biome.SpawnEntry(EntityType.field_6091, 10, 4, 4));
	}

	@Override
	public int getSkyColor(float f) {
		return 0;
	}
}
