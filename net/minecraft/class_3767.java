package net.minecraft;

import javax.annotation.Nullable;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockView;

public class class_3767 implements class_3761<BlockState> {
	private static final class_3767 field_18705 = new class_3767();

	public boolean test(@Nullable BlockState blockState, BlockView blockView, BlockPos blockPos) {
		return blockState != null && !blockState.getMaterial().blocksMovement() && blockState.getFluidState().isEmpty();
	}

	public static class_3767 method_16956() {
		return field_18705;
	}
}
