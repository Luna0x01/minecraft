package net.minecraft;

import java.util.BitSet;
import java.util.Random;
import net.minecraft.world.BlockView;
import net.minecraft.world.IWorld;

public class class_3973<C extends class_3845> implements class_3997<class_3871> {
	private final class_3997<C> field_19346;
	private final C field_19347;

	public class_3973(class_3997<C> arg, C arg2) {
		this.field_19346 = arg;
		this.field_19347 = arg2;
	}

	public boolean method_17679(BlockView blockView, Random random, int i, int j, class_3871 arg) {
		return this.field_19346.method_17679(blockView, random, i, j, this.field_19347);
	}

	public boolean method_17680(IWorld iWorld, Random random, int i, int j, int k, int l, BitSet bitSet, class_3871 arg) {
		return this.field_19346.method_17680(iWorld, random, i, j, k, l, bitSet, this.field_19347);
	}
}
