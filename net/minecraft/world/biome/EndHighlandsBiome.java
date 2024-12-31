package net.minecraft.world.biome;

import net.minecraft.class_3801;
import net.minecraft.class_3830;
import net.minecraft.class_3841;
import net.minecraft.class_3843;
import net.minecraft.class_3844;
import net.minecraft.class_3845;
import net.minecraft.entity.EntityCategory;
import net.minecraft.entity.EntityType;
import net.minecraft.gen.surfacebuilder.SurfaceBuilder;

public class EndHighlandsBiome extends Biome {
	public EndHighlandsBiome() {
		super(
			new Biome.Builder()
				.setSurfaceBuilder(new SurfaceBuilder<>(field_17594, field_17593))
				.setPrecipitation(Biome.Precipitation.NONE)
				.setCategory(Biome.Category.THEEND)
				.setDepth(0.1F)
				.setScale(0.2F)
				.setTemperature(0.5F)
				.setDownfall(0.5F)
				.setWaterColor(4159204)
				.setWaterFogColor(329011)
				.setParent(null)
		);
		this.method_16436(class_3844.field_19193, new class_3841());
		this.method_16432(class_3801.class_3803.SURFACE_STRUCTURES, method_16433(class_3844.field_19178, new class_3843(true), field_17549, class_3830.field_19084));
		this.method_16432(class_3801.class_3803.SURFACE_STRUCTURES, method_16433(class_3844.field_19193, new class_3841(), field_17612, class_3830.field_19084));
		this.method_16432(class_3801.class_3803.VEGETAL_DECORATION, method_16433(class_3844.field_19177, class_3845.field_19203, field_17548, class_3830.field_19084));
		this.method_16425(EntityCategory.MONSTER, new Biome.SpawnEntry(EntityType.ENDERMAN, 10, 4, 4));
	}

	@Override
	public int getSkyColor(float temperature) {
		return 0;
	}
}
