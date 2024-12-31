package net.minecraft;

import java.util.Random;
import net.minecraft.block.BlockState;

public class class_3862 extends class_3910 {
	public class_3862(boolean bl, int i, BlockState blockState, BlockState blockState2, boolean bl2) {
		super(bl, i, blockState, blockState2, bl2);
	}

	@Override
	protected int method_17443(Random random) {
		return this.field_19267 + random.nextInt(7);
	}
}
