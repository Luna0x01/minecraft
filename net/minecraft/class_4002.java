package net.minecraft;

import java.util.Random;
import net.minecraft.block.BlockState;
import net.minecraft.world.biome.Biome;

public class class_4002 implements class_4012<class_4013> {
	public void method_17718(
		Random random, class_3781 arg, Biome biome, int i, int j, int k, double d, BlockState blockState, BlockState blockState2, int l, long m, class_4013 arg2
	) {
		if (d < -1.0 || d > 2.0) {
			Biome.field_17594.method_17718(random, arg, biome, i, j, k, d, blockState, blockState2, l, m, Biome.field_17584);
		} else if (d > 1.0) {
			Biome.field_17594.method_17718(random, arg, biome, i, j, k, d, blockState, blockState2, l, m, Biome.field_17583);
		} else {
			Biome.field_17594.method_17718(random, arg, biome, i, j, k, d, blockState, blockState2, l, m, Biome.field_17582);
		}
	}
}
