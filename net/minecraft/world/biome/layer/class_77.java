package net.minecraft.world.biome.layer;

import net.minecraft.class_4040;
import net.minecraft.class_4046;
import net.minecraft.class_4055;

public enum class_77 implements class_4055 {
	INSTANCE;

	@Override
	public int method_17890(class_4040 arg, int i) {
		return class_4046.method_17863(i) ? i : arg.method_17850(299999) + 2;
	}
}
