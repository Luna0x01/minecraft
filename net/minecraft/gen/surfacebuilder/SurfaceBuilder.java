package net.minecraft.gen.surfacebuilder;

import java.util.Random;
import net.minecraft.class_3781;
import net.minecraft.class_4012;
import net.minecraft.class_4013;
import net.minecraft.class_4014;
import net.minecraft.block.BlockState;
import net.minecraft.world.biome.Biome;

public class SurfaceBuilder<C extends class_4014> implements class_4012<class_4013> {
	private final class_4012<C> field_19437;
	private final C field_19438;

	public SurfaceBuilder(class_4012<C> arg, C arg2) {
		this.field_19437 = arg;
		this.field_19438 = arg2;
	}

	public void method_17718(
		Random random, class_3781 arg, Biome biome, int i, int j, int k, double d, BlockState blockState, BlockState blockState2, int l, long m, class_4013 arg2
	) {
		this.field_19437.method_17718(random, arg, biome, i, j, k, d, blockState, blockState2, l, m, this.field_19438);
	}

	@Override
	public void method_17717(long l) {
		this.field_19437.method_17717(l);
	}

	public C method_17700() {
		return this.field_19438;
	}
}
