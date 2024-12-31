package net.minecraft.block.sapling;

import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.world.biome.DefaultBiomeFeatures;
import net.minecraft.world.gen.feature.BranchedTreeFeatureConfig;
import net.minecraft.world.gen.feature.ConfiguredFeature;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.MegaTreeFeatureConfig;

public class SpruceSaplingGenerator extends LargeTreeSaplingGenerator {
	@Nullable
	@Override
	protected ConfiguredFeature<BranchedTreeFeatureConfig, ?> createTreeFeature(Random random, boolean bl) {
		return Feature.field_13510.configure(DefaultBiomeFeatures.SPRUCE_TREE_CONFIG);
	}

	@Nullable
	@Override
	protected ConfiguredFeature<MegaTreeFeatureConfig, ?> createLargeTreeFeature(Random random) {
		return Feature.field_13580.configure(random.nextBoolean() ? DefaultBiomeFeatures.MEGA_SPRUCE_TREE_CONFIG : DefaultBiomeFeatures.MEGA_PINE_TREE_CONFIG);
	}
}
