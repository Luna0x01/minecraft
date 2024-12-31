package net.minecraft.world.biome;

import net.minecraft.class_3801;
import net.minecraft.class_3830;
import net.minecraft.class_3844;
import net.minecraft.class_3845;
import net.minecraft.gen.surfacebuilder.SurfaceBuilder;

public final class VoidBiome extends Biome {
	public VoidBiome() {
		super(
			new Biome.Builder()
				.setSurfaceBuilder(new SurfaceBuilder<>(field_17562, field_17583))
				.setPrecipitation(Biome.Precipitation.NONE)
				.setCategory(Biome.Category.NONE)
				.setDepth(0.1F)
				.setScale(0.2F)
				.setTemperature(0.5F)
				.setDownfall(0.5F)
				.setWaterColor(4159204)
				.setWaterFogColor(329011)
				.setParent(null)
		);
		this.method_16432(
			class_3801.class_3803.TOP_LAYER_MODIFICATION, method_16433(class_3844.field_19134, class_3845.field_19203, field_17612, class_3830.field_19084)
		);
	}
}
