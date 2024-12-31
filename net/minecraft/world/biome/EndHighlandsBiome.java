package net.minecraft.world.biome;

import net.minecraft.entity.EntityCategory;
import net.minecraft.entity.EntityType;
import net.minecraft.world.dimension.TheEndDimension;
import net.minecraft.world.gen.GenerationStep;
import net.minecraft.world.gen.decorator.Decorator;
import net.minecraft.world.gen.decorator.DecoratorConfig;
import net.minecraft.world.gen.feature.EndGatewayFeatureConfig;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.FeatureConfig;
import net.minecraft.world.gen.surfacebuilder.SurfaceBuilder;

public class EndHighlandsBiome extends Biome {
	public EndHighlandsBiome() {
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
		this.addStructureFeature(Feature.END_CITY.configure(FeatureConfig.DEFAULT));
		this.addFeature(
			GenerationStep.Feature.field_13173,
			Feature.field_13564
				.configure(EndGatewayFeatureConfig.createConfig(TheEndDimension.SPAWN_POINT, true))
				.createDecoratedFeature(Decorator.field_14230.configure(DecoratorConfig.DEFAULT))
		);
		DefaultBiomeFeatures.addEndCities(this);
		this.addFeature(
			GenerationStep.Feature.field_13178,
			Feature.field_13552.configure(FeatureConfig.DEFAULT).createDecoratedFeature(Decorator.field_14257.configure(DecoratorConfig.DEFAULT))
		);
		this.addSpawn(EntityCategory.field_6302, new Biome.SpawnEntry(EntityType.field_6091, 10, 4, 4));
	}

	@Override
	public int getSkyColor() {
		return 0;
	}
}
