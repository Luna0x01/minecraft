package net.minecraft;

import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.Biomes;

public class class_3633 implements class_3611 {
	private Biome field_17689 = Biomes.PLAINS;

	public class_3633 method_16498(Biome biome) {
		this.field_17689 = biome;
		return this;
	}

	public Biome method_16497() {
		return this.field_17689;
	}
}
