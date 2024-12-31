package net.minecraft.world.biome;

import net.minecraft.class_3801;
import net.minecraft.class_3819;
import net.minecraft.class_3829;
import net.minecraft.class_3830;
import net.minecraft.class_3831;
import net.minecraft.class_3844;
import net.minecraft.class_3845;
import net.minecraft.class_3850;
import net.minecraft.class_3868;
import net.minecraft.class_3875;
import net.minecraft.class_3877;
import net.minecraft.class_3899;
import net.minecraft.class_3934;
import net.minecraft.class_3935;
import net.minecraft.block.Blocks;
import net.minecraft.entity.EntityCategory;
import net.minecraft.entity.EntityType;
import net.minecraft.fluid.Fluids;
import net.minecraft.gen.surfacebuilder.SurfaceBuilder;
import net.minecraft.predicate.block.BlockPredicate;

public final class NetherBiome extends Biome {
	protected NetherBiome() {
		super(
			new Biome.Builder()
				.setSurfaceBuilder(new SurfaceBuilder<>(field_17561, field_17592))
				.setPrecipitation(Biome.Precipitation.NONE)
				.setCategory(Biome.Category.NETHER)
				.setDepth(0.1F)
				.setScale(0.2F)
				.setTemperature(2.0F)
				.setDownfall(0.0F)
				.setWaterColor(4159204)
				.setWaterFogColor(329011)
				.setParent(null)
		);
		this.method_16436(class_3844.field_19192, new class_3868());
		this.method_16429(class_3801.class_3802.AIR, method_16437(field_17601, new class_3877(0.2F)));
		this.method_16432(
			class_3801.class_3803.VEGETAL_DECORATION, method_16433(class_3844.field_19174, new class_3899(Fluids.LAVA), field_17620, new class_3831(20, 8, 16, 256))
		);
		this.method_16432(
			class_3801.class_3803.VEGETAL_DECORATION, method_16433(class_3844.field_19162, new class_3819(Blocks.BROWN_MUSHROOM), field_17614, new class_3934(4))
		);
		this.method_16432(
			class_3801.class_3803.VEGETAL_DECORATION, method_16433(class_3844.field_19162, new class_3819(Blocks.RED_MUSHROOM), field_17614, new class_3934(8))
		);
		this.method_16432(class_3801.class_3803.UNDERGROUND_DECORATION, method_16433(class_3844.field_19192, new class_3868(), field_17612, class_3830.field_19084));
		this.method_16432(
			class_3801.class_3803.UNDERGROUND_DECORATION, method_16433(class_3844.field_19165, new class_3850(false), field_17618, new class_3831(8, 4, 8, 128))
		);
		this.method_16432(class_3801.class_3803.UNDERGROUND_DECORATION, method_16433(class_3844.field_19139, class_3845.field_19203, field_17537, new class_3935(10)));
		this.method_16432(class_3801.class_3803.UNDERGROUND_DECORATION, method_16433(class_3844.field_19143, class_3845.field_19203, field_17545, new class_3935(10)));
		this.method_16432(
			class_3801.class_3803.UNDERGROUND_DECORATION, method_16433(class_3844.field_19143, class_3845.field_19203, field_17618, new class_3831(10, 0, 0, 128))
		);
		this.method_16432(
			class_3801.class_3803.UNDERGROUND_DECORATION,
			method_16433(class_3844.field_19162, new class_3819(Blocks.BROWN_MUSHROOM), field_17622, new class_3829(0.5F, 0, 0, 128))
		);
		this.method_16432(
			class_3801.class_3803.UNDERGROUND_DECORATION,
			method_16433(class_3844.field_19162, new class_3819(Blocks.RED_MUSHROOM), field_17622, new class_3829(0.5F, 0, 0, 128))
		);
		this.method_16432(
			class_3801.class_3803.UNDERGROUND_DECORATION,
			method_16433(
				class_3844.field_19168,
				new class_3875(BlockPredicate.create(Blocks.NETHERRACK), Blocks.NETHER_QUARTZ_ORE.getDefaultState(), 14),
				field_17618,
				new class_3831(16, 10, 20, 128)
			)
		);
		this.method_16432(
			class_3801.class_3803.UNDERGROUND_DECORATION,
			method_16433(
				class_3844.field_19168, new class_3875(BlockPredicate.create(Blocks.NETHERRACK), Blocks.MAGMA_BLOCK.getDefaultState(), 33), field_17538, new class_3935(4)
			)
		);
		this.method_16432(
			class_3801.class_3803.UNDERGROUND_DECORATION, method_16433(class_3844.field_19165, new class_3850(true), field_17618, new class_3831(16, 10, 20, 128))
		);
		this.method_16425(EntityCategory.MONSTER, new Biome.SpawnEntry(EntityType.GHAST, 50, 4, 4));
		this.method_16425(EntityCategory.MONSTER, new Biome.SpawnEntry(EntityType.ZOMBIE_PIGMAN, 100, 4, 4));
		this.method_16425(EntityCategory.MONSTER, new Biome.SpawnEntry(EntityType.MAGMA_CUBE, 2, 4, 4));
		this.method_16425(EntityCategory.MONSTER, new Biome.SpawnEntry(EntityType.ENDERMAN, 1, 4, 4));
	}
}
