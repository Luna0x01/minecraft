package net.minecraft;

import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.Biomes;

public class class_3617 implements class_3611 {
	private Biome[] field_17685 = new Biome[]{Biomes.PLAINS};
	private int field_17686 = 1;

	public class_3617 method_16495(Biome[] biomes) {
		this.field_17685 = biomes;
		return this;
	}

	public class_3617 method_16494(int i) {
		this.field_17686 = i;
		return this;
	}

	public Biome[] method_16493() {
		return this.field_17685;
	}

	public int method_16496() {
		return this.field_17686;
	}
}
