package net.minecraft;

import javax.annotation.Nullable;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockView;

public class class_3765 implements class_3761<BlockState> {
	private static final class_3765 field_18703 = new class_3765();

	public boolean test(@Nullable BlockState blockState, BlockView blockView, BlockPos blockPos) {
		return blockState != null && blockState.method_16885(blockView, blockPos) == 0;
	}

	public static class_3765 method_16952() {
		return field_18703;
	}
}
