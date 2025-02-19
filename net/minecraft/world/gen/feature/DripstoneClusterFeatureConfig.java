package net.minecraft.world.gen.feature;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.math.floatprovider.FloatProvider;
import net.minecraft.util.math.intprovider.IntProvider;

public class DripstoneClusterFeatureConfig implements FeatureConfig {
	public static final Codec<DripstoneClusterFeatureConfig> CODEC = RecordCodecBuilder.create(
		instance -> instance.group(
					Codec.intRange(1, 512)
						.fieldOf("floor_to_ceiling_search_range")
						.forGetter(dripstoneClusterFeatureConfig -> dripstoneClusterFeatureConfig.floorToCeilingSearchRange),
					IntProvider.createValidatingCodec(1, 128).fieldOf("height").forGetter(dripstoneClusterFeatureConfig -> dripstoneClusterFeatureConfig.height),
					IntProvider.createValidatingCodec(1, 128).fieldOf("radius").forGetter(dripstoneClusterFeatureConfig -> dripstoneClusterFeatureConfig.radius),
					Codec.intRange(0, 64)
						.fieldOf("max_stalagmite_stalactite_height_diff")
						.forGetter(dripstoneClusterFeatureConfig -> dripstoneClusterFeatureConfig.maxStalagmiteStalactiteHeightDiff),
					Codec.intRange(1, 64).fieldOf("height_deviation").forGetter(dripstoneClusterFeatureConfig -> dripstoneClusterFeatureConfig.heightDeviation),
					IntProvider.createValidatingCodec(0, 128)
						.fieldOf("dripstone_block_layer_thickness")
						.forGetter(dripstoneClusterFeatureConfig -> dripstoneClusterFeatureConfig.dripstoneBlockLayerThickness),
					FloatProvider.createValidatedCodec(0.0F, 2.0F).fieldOf("density").forGetter(dripstoneClusterFeatureConfig -> dripstoneClusterFeatureConfig.density),
					FloatProvider.createValidatedCodec(0.0F, 2.0F).fieldOf("wetness").forGetter(dripstoneClusterFeatureConfig -> dripstoneClusterFeatureConfig.wetness),
					Codec.floatRange(0.0F, 1.0F)
						.fieldOf("chance_of_dripstone_column_at_max_distance_from_center")
						.forGetter(dripstoneClusterFeatureConfig -> dripstoneClusterFeatureConfig.chanceOfDripstoneColumnAtMaxDistanceFromCenter),
					Codec.intRange(1, 64)
						.fieldOf("max_distance_from_edge_affecting_chance_of_dripstone_column")
						.forGetter(dripstoneClusterFeatureConfig -> dripstoneClusterFeatureConfig.maxDistanceFromCenterAffectingChanceOfDripstoneColumn),
					Codec.intRange(1, 64)
						.fieldOf("max_distance_from_center_affecting_height_bias")
						.forGetter(dripstoneClusterFeatureConfig -> dripstoneClusterFeatureConfig.maxDistanceFromCenterAffectingHeightBias)
				)
				.apply(instance, DripstoneClusterFeatureConfig::new)
	);
	public final int floorToCeilingSearchRange;
	public final IntProvider height;
	public final IntProvider radius;
	public final int maxStalagmiteStalactiteHeightDiff;
	public final int heightDeviation;
	public final IntProvider dripstoneBlockLayerThickness;
	public final FloatProvider density;
	public final FloatProvider wetness;
	public final float chanceOfDripstoneColumnAtMaxDistanceFromCenter;
	public final int maxDistanceFromCenterAffectingChanceOfDripstoneColumn;
	public final int maxDistanceFromCenterAffectingHeightBias;

	public DripstoneClusterFeatureConfig(
		int floorToCeilingSearchRange,
		IntProvider height,
		IntProvider radius,
		int maxStalagmiteStalactiteHeightDiff,
		int heightDeviation,
		IntProvider dripstoneBlockLayerThickness,
		FloatProvider density,
		FloatProvider wetness,
		float wetnessMean,
		int i,
		int j
	) {
		this.floorToCeilingSearchRange = floorToCeilingSearchRange;
		this.height = height;
		this.radius = radius;
		this.maxStalagmiteStalactiteHeightDiff = maxStalagmiteStalactiteHeightDiff;
		this.heightDeviation = heightDeviation;
		this.dripstoneBlockLayerThickness = dripstoneBlockLayerThickness;
		this.density = density;
		this.wetness = wetness;
		this.chanceOfDripstoneColumnAtMaxDistanceFromCenter = wetnessMean;
		this.maxDistanceFromCenterAffectingChanceOfDripstoneColumn = i;
		this.maxDistanceFromCenterAffectingHeightBias = j;
	}
}
