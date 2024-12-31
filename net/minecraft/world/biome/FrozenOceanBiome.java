package net.minecraft.world.biome;

import com.google.common.collect.Lists;
import java.util.Random;
import net.minecraft.class_3801;
import net.minecraft.class_3819;
import net.minecraft.class_3830;
import net.minecraft.class_3831;
import net.minecraft.class_3838;
import net.minecraft.class_3844;
import net.minecraft.class_3845;
import net.minecraft.class_3853;
import net.minecraft.class_3864;
import net.minecraft.class_3866;
import net.minecraft.class_3867;
import net.minecraft.class_3874;
import net.minecraft.class_3875;
import net.minecraft.class_3877;
import net.minecraft.class_3880;
import net.minecraft.class_3890;
import net.minecraft.class_3899;
import net.minecraft.class_3909;
import net.minecraft.class_3934;
import net.minecraft.class_3935;
import net.minecraft.class_3937;
import net.minecraft.class_3941;
import net.minecraft.class_3948;
import net.minecraft.class_3951;
import net.minecraft.class_3983;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.entity.EntityCategory;
import net.minecraft.entity.EntityType;
import net.minecraft.fluid.Fluids;
import net.minecraft.gen.surfacebuilder.SurfaceBuilder;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.noise.PerlinNoiseGenerator;

public final class FrozenOceanBiome extends Biome {
	protected static final PerlinNoiseGenerator field_17690 = new PerlinNoiseGenerator(new Random(3456L), 3);

	public FrozenOceanBiome() {
		super(
			new Biome.Builder()
				.setSurfaceBuilder(new SurfaceBuilder<>(field_17560, field_17582))
				.setPrecipitation(Biome.Precipitation.SNOW)
				.setCategory(Biome.Category.OCEAN)
				.setDepth(-1.0F)
				.setScale(0.1F)
				.setTemperature(0.0F)
				.setDownfall(0.5F)
				.setWaterColor(3750089)
				.setWaterFogColor(329011)
				.setParent(null)
		);
		this.method_16436(class_3844.field_19191, new class_3874(class_3983.class_3985.COLD, 0.3F, 0.9F));
		this.method_16436(class_3844.field_19182, new class_3866(0.004, class_3867.class_3014.NORMAL));
		this.method_16436(class_3844.field_19187, new class_3890(false));
		this.method_16429(class_3801.class_3802.AIR, method_16437(field_17600, new class_3877(0.06666667F)));
		this.method_16429(class_3801.class_3802.AIR, method_16437(field_17602, new class_3877(0.02F)));
		this.method_16429(class_3801.class_3802.LIQUID, method_16437(field_17603, new class_3877(0.02F)));
		this.method_16429(class_3801.class_3802.LIQUID, method_16437(field_17604, new class_3877(0.06666667F)));
		this.method_16424();
		this.method_16432(
			class_3801.class_3803.LOCAL_MODIFICATIONS, method_16433(class_3844.field_19167, new class_3864(Blocks.WATER), field_17541, new class_3948(4))
		);
		this.method_16432(
			class_3801.class_3803.LOCAL_MODIFICATIONS, method_16433(class_3844.field_19167, new class_3864(Blocks.LAVA), field_17540, new class_3948(80))
		);
		this.method_16432(
			class_3801.class_3803.LOCAL_MODIFICATIONS,
			method_16433(class_3844.field_19160, new class_3853(Blocks.PACKED_ICE.getDefaultState()), field_17544, new class_3934(16))
		);
		this.method_16432(
			class_3801.class_3803.LOCAL_MODIFICATIONS,
			method_16433(class_3844.field_19160, new class_3853(Blocks.BLUE_ICE.getDefaultState()), field_17544, new class_3934(200))
		);
		this.method_16432(class_3801.class_3803.UNDERGROUND_STRUCTURES, method_16433(class_3844.field_19158, class_3845.field_19203, field_17542, new class_3951(8)));
		this.method_16432(
			class_3801.class_3803.SURFACE_STRUCTURES, method_16433(class_3844.field_19159, class_3845.field_19203, field_17621, new class_3831(20, 30, 32, 64))
		);
		this.method_16432(
			class_3801.class_3803.UNDERGROUND_ORES,
			method_16433(class_3844.field_19168, new class_3875(class_3875.field_19226, Blocks.DIRT.getDefaultState(), 33), field_17618, new class_3831(10, 0, 0, 256))
		);
		this.method_16432(
			class_3801.class_3803.UNDERGROUND_ORES,
			method_16433(class_3844.field_19168, new class_3875(class_3875.field_19226, Blocks.GRAVEL.getDefaultState(), 33), field_17618, new class_3831(8, 0, 0, 256))
		);
		this.method_16432(
			class_3801.class_3803.UNDERGROUND_ORES,
			method_16433(class_3844.field_19168, new class_3875(class_3875.field_19226, Blocks.GRANITE.getDefaultState(), 33), field_17618, new class_3831(10, 0, 0, 80))
		);
		this.method_16432(
			class_3801.class_3803.UNDERGROUND_ORES,
			method_16433(class_3844.field_19168, new class_3875(class_3875.field_19226, Blocks.DIORITE.getDefaultState(), 33), field_17618, new class_3831(10, 0, 0, 80))
		);
		this.method_16432(
			class_3801.class_3803.UNDERGROUND_ORES,
			method_16433(
				class_3844.field_19168, new class_3875(class_3875.field_19226, Blocks.ANDESITE.getDefaultState(), 33), field_17618, new class_3831(10, 0, 0, 80)
			)
		);
		this.method_16432(
			class_3801.class_3803.UNDERGROUND_ORES,
			method_16433(
				class_3844.field_19168, new class_3875(class_3875.field_19226, Blocks.COAL_ORE.getDefaultState(), 17), field_17618, new class_3831(20, 0, 0, 128)
			)
		);
		this.method_16432(
			class_3801.class_3803.UNDERGROUND_ORES,
			method_16433(class_3844.field_19168, new class_3875(class_3875.field_19226, Blocks.IRON_ORE.getDefaultState(), 9), field_17618, new class_3831(20, 0, 0, 64))
		);
		this.method_16432(
			class_3801.class_3803.UNDERGROUND_ORES,
			method_16433(class_3844.field_19168, new class_3875(class_3875.field_19226, Blocks.GOLD_ORE.getDefaultState(), 9), field_17618, new class_3831(2, 0, 0, 32))
		);
		this.method_16432(
			class_3801.class_3803.UNDERGROUND_ORES,
			method_16433(
				class_3844.field_19168, new class_3875(class_3875.field_19226, Blocks.REDSTONE_ORE.getDefaultState(), 8), field_17618, new class_3831(8, 0, 0, 16)
			)
		);
		this.method_16432(
			class_3801.class_3803.UNDERGROUND_ORES,
			method_16433(
				class_3844.field_19168, new class_3875(class_3875.field_19226, Blocks.DIAMOND_ORE.getDefaultState(), 8), field_17618, new class_3831(1, 0, 0, 16)
			)
		);
		this.method_16432(
			class_3801.class_3803.UNDERGROUND_ORES,
			method_16433(
				class_3844.field_19168, new class_3875(class_3875.field_19226, Blocks.LAPIS_LAZULI_ORE.getDefaultState(), 7), field_17531, new class_3941(1, 16, 16)
			)
		);
		this.method_16432(
			class_3801.class_3803.UNDERGROUND_ORES,
			method_16433(
				class_3844.field_19163, new class_3838(Blocks.SAND, 7, 2, Lists.newArrayList(new Block[]{Blocks.DIRT, Blocks.GRASS_BLOCK})), field_17606, new class_3935(3)
			)
		);
		this.method_16432(
			class_3801.class_3803.UNDERGROUND_ORES,
			method_16433(
				class_3844.field_19163, new class_3838(Blocks.CLAY, 4, 1, Lists.newArrayList(new Block[]{Blocks.DIRT, Blocks.CLAY})), field_17606, new class_3935(1)
			)
		);
		this.method_16432(
			class_3801.class_3803.UNDERGROUND_ORES,
			method_16433(
				class_3844.field_19163,
				new class_3838(Blocks.GRAVEL, 6, 2, Lists.newArrayList(new Block[]{Blocks.DIRT, Blocks.GRASS_BLOCK})),
				field_17606,
				new class_3935(1)
			)
		);
		this.method_16432(
			class_3801.class_3803.VEGETAL_DECORATION,
			method_16433(
				class_3844.field_19170,
				new class_3880(
					new class_3844[]{class_3844.field_19195}, new class_3845[]{class_3845.field_19203}, new float[]{0.1F}, class_3844.field_19123, class_3845.field_19203
				),
				field_17617,
				new class_3937(0, 0.1F, 1)
			)
		);
		this.method_16432(class_3801.class_3803.VEGETAL_DECORATION, method_16434(class_3844.field_19127, field_17607, new class_3935(2)));
		this.method_16432(
			class_3801.class_3803.VEGETAL_DECORATION,
			method_16433(class_3844.field_19133, new class_3909(Blocks.GRASS.getDefaultState()), field_17608, new class_3935(1))
		);
		this.method_16432(
			class_3801.class_3803.VEGETAL_DECORATION, method_16433(class_3844.field_19162, new class_3819(Blocks.BROWN_MUSHROOM), field_17614, new class_3934(4))
		);
		this.method_16432(
			class_3801.class_3803.VEGETAL_DECORATION, method_16433(class_3844.field_19162, new class_3819(Blocks.RED_MUSHROOM), field_17614, new class_3934(8))
		);
		this.method_16432(class_3801.class_3803.VEGETAL_DECORATION, method_16433(class_3844.field_19146, class_3845.field_19203, field_17608, new class_3935(10)));
		this.method_16432(class_3801.class_3803.VEGETAL_DECORATION, method_16433(class_3844.field_19145, class_3845.field_19203, field_17614, new class_3934(32)));
		this.method_16432(
			class_3801.class_3803.VEGETAL_DECORATION, method_16433(class_3844.field_19174, new class_3899(Fluids.WATER), field_17619, new class_3831(50, 8, 8, 256))
		);
		this.method_16432(
			class_3801.class_3803.VEGETAL_DECORATION, method_16433(class_3844.field_19174, new class_3899(Fluids.LAVA), field_17620, new class_3831(20, 8, 16, 256))
		);
		this.method_16432(
			class_3801.class_3803.TOP_LAYER_MODIFICATION, method_16433(class_3844.field_19155, class_3845.field_19203, field_17612, class_3830.field_19084)
		);
		this.method_16425(EntityCategory.AQUATIC, new Biome.SpawnEntry(EntityType.SQUID, 1, 1, 4));
		this.method_16425(EntityCategory.AQUATIC, new Biome.SpawnEntry(EntityType.SALMON, 15, 1, 5));
		this.method_16425(EntityCategory.PASSIVE, new Biome.SpawnEntry(EntityType.POLAR_BEAR, 1, 1, 2));
		this.method_16425(EntityCategory.AMBIENT, new Biome.SpawnEntry(EntityType.BAT, 10, 8, 8));
		this.method_16425(EntityCategory.MONSTER, new Biome.SpawnEntry(EntityType.SPIDER, 100, 4, 4));
		this.method_16425(EntityCategory.MONSTER, new Biome.SpawnEntry(EntityType.ZOMBIE, 95, 4, 4));
		this.method_16425(EntityCategory.MONSTER, new Biome.SpawnEntry(EntityType.DROWNED, 5, 1, 1));
		this.method_16425(EntityCategory.MONSTER, new Biome.SpawnEntry(EntityType.ZOMBIE_VILLAGER, 5, 1, 1));
		this.method_16425(EntityCategory.MONSTER, new Biome.SpawnEntry(EntityType.SKELETON, 100, 4, 4));
		this.method_16425(EntityCategory.MONSTER, new Biome.SpawnEntry(EntityType.CREEPER, 100, 4, 4));
		this.method_16425(EntityCategory.MONSTER, new Biome.SpawnEntry(EntityType.SLIME, 100, 4, 4));
		this.method_16425(EntityCategory.MONSTER, new Biome.SpawnEntry(EntityType.ENDERMAN, 10, 1, 4));
		this.method_16425(EntityCategory.MONSTER, new Biome.SpawnEntry(EntityType.WITCH, 5, 1, 1));
	}

	@Override
	public float getTemperature(BlockPos pos) {
		float f = this.getTemperature();
		double d = field_17690.noise((double)pos.getX() * 0.05, (double)pos.getZ() * 0.05);
		double e = FOLIAGE_NOISE.noise((double)pos.getX() * 0.2, (double)pos.getZ() * 0.2);
		double g = d + e;
		if (g < 0.3) {
			double h = FOLIAGE_NOISE.noise((double)pos.getX() * 0.09, (double)pos.getZ() * 0.09);
			if (h < 0.8) {
				f = 0.2F;
			}
		}

		if (pos.getY() > 64) {
			float i = (float)(TEMPERATURE_NOISE.noise((double)((float)pos.getX() / 8.0F), (double)((float)pos.getZ() / 8.0F)) * 4.0);
			return f - (i + (float)pos.getY() - 64.0F) * 0.05F / 30.0F;
		} else {
			return f;
		}
	}
}
