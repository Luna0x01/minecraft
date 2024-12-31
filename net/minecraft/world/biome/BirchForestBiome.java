package net.minecraft.world.biome;

import com.google.common.collect.Lists;
import net.minecraft.class_3801;
import net.minecraft.class_3819;
import net.minecraft.class_3830;
import net.minecraft.class_3831;
import net.minecraft.class_3838;
import net.minecraft.class_3840;
import net.minecraft.class_3844;
import net.minecraft.class_3845;
import net.minecraft.class_3864;
import net.minecraft.class_3866;
import net.minecraft.class_3867;
import net.minecraft.class_3875;
import net.minecraft.class_3877;
import net.minecraft.class_3882;
import net.minecraft.class_3899;
import net.minecraft.class_3900;
import net.minecraft.class_3909;
import net.minecraft.class_3934;
import net.minecraft.class_3935;
import net.minecraft.class_3937;
import net.minecraft.class_3941;
import net.minecraft.class_3948;
import net.minecraft.class_3951;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.entity.EntityCategory;
import net.minecraft.entity.EntityType;
import net.minecraft.fluid.Fluids;
import net.minecraft.gen.surfacebuilder.SurfaceBuilder;

public final class BirchForestBiome extends Biome {
	public BirchForestBiome() {
		super(
			new Biome.Builder()
				.setSurfaceBuilder(new SurfaceBuilder<>(field_17594, field_17582))
				.setPrecipitation(Biome.Precipitation.RAIN)
				.setCategory(Biome.Category.FOREST)
				.setDepth(0.1F)
				.setScale(0.2F)
				.setTemperature(0.6F)
				.setDownfall(0.6F)
				.setWaterColor(4159204)
				.setWaterFogColor(329011)
				.setParent(null)
		);
		this.method_16436(class_3844.field_19182, new class_3866(0.004, class_3867.class_3014.NORMAL));
		this.method_16436(class_3844.field_19189, new class_3900());
		this.method_16429(class_3801.class_3802.AIR, method_16437(field_17600, new class_3877(0.14285715F)));
		this.method_16429(class_3801.class_3802.AIR, method_16437(field_17602, new class_3877(0.02F)));
		this.method_16424();
		this.method_16432(
			class_3801.class_3803.LOCAL_MODIFICATIONS, method_16433(class_3844.field_19167, new class_3864(Blocks.WATER), field_17541, new class_3948(4))
		);
		this.method_16432(
			class_3801.class_3803.LOCAL_MODIFICATIONS, method_16433(class_3844.field_19167, new class_3864(Blocks.LAVA), field_17540, new class_3948(80))
		);
		this.method_16432(class_3801.class_3803.UNDERGROUND_STRUCTURES, method_16433(class_3844.field_19158, class_3845.field_19203, field_17542, new class_3951(8)));
		this.method_16432(
			class_3801.class_3803.VEGETAL_DECORATION,
			method_16433(
				class_3844.field_19169,
				new class_3882(
					new class_3844[]{class_3844.field_19164, class_3844.field_19164, class_3844.field_19164},
					new class_3845[]{
						new class_3840(Blocks.LILAC.getDefaultState()), new class_3840(Blocks.ROSE_BUSH.getDefaultState()), new class_3840(Blocks.PEONY.getDefaultState())
					},
					0
				),
				field_17607,
				new class_3935(5)
			)
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
			class_3801.class_3803.VEGETAL_DECORATION, method_16433(class_3844.field_19196, class_3845.field_19203, field_17617, new class_3937(10, 0.1F, 1))
		);
		this.method_16432(class_3801.class_3803.VEGETAL_DECORATION, method_16434(class_3844.field_19127, field_17607, new class_3935(2)));
		this.method_16432(
			class_3801.class_3803.VEGETAL_DECORATION,
			method_16433(class_3844.field_19133, new class_3909(Blocks.GRASS.getDefaultState()), field_17608, new class_3935(2))
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
		this.method_16425(EntityCategory.PASSIVE, new Biome.SpawnEntry(EntityType.SHEEP, 12, 4, 4));
		this.method_16425(EntityCategory.PASSIVE, new Biome.SpawnEntry(EntityType.PIG, 10, 4, 4));
		this.method_16425(EntityCategory.PASSIVE, new Biome.SpawnEntry(EntityType.CHICKEN, 10, 4, 4));
		this.method_16425(EntityCategory.PASSIVE, new Biome.SpawnEntry(EntityType.COW, 8, 4, 4));
		this.method_16425(EntityCategory.AMBIENT, new Biome.SpawnEntry(EntityType.BAT, 10, 8, 8));
		this.method_16425(EntityCategory.MONSTER, new Biome.SpawnEntry(EntityType.SPIDER, 100, 4, 4));
		this.method_16425(EntityCategory.MONSTER, new Biome.SpawnEntry(EntityType.ZOMBIE, 95, 4, 4));
		this.method_16425(EntityCategory.MONSTER, new Biome.SpawnEntry(EntityType.ZOMBIE_VILLAGER, 5, 1, 1));
		this.method_16425(EntityCategory.MONSTER, new Biome.SpawnEntry(EntityType.SKELETON, 100, 4, 4));
		this.method_16425(EntityCategory.MONSTER, new Biome.SpawnEntry(EntityType.CREEPER, 100, 4, 4));
		this.method_16425(EntityCategory.MONSTER, new Biome.SpawnEntry(EntityType.SLIME, 100, 4, 4));
		this.method_16425(EntityCategory.MONSTER, new Biome.SpawnEntry(EntityType.ENDERMAN, 10, 1, 4));
		this.method_16425(EntityCategory.MONSTER, new Biome.SpawnEntry(EntityType.WITCH, 5, 1, 1));
	}
}
