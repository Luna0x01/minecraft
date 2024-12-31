package net.minecraft;

import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockView;

public class class_3766 implements class_3761<BlockState> {
	private static final class_3766 field_18704 = new class_3766();

	public boolean test(BlockState blockState, BlockView blockView, BlockPos blockPos) {
		return !blockState.getFluidState().isEmpty();
	}

	public static class_3766 method_16954() {
		return field_18704;
	}
}
