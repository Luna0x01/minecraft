package net.minecraft;

import javax.annotation.Nullable;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockView;

public class class_3768 implements class_3761<BlockState> {
	private static final class_3768 field_18706 = new class_3768();

	public boolean test(@Nullable BlockState blockState, BlockView blockView, BlockPos blockPos) {
		return blockState != null && !blockState.getMaterial().blocksMovement();
	}

	public static class_3768 method_16958() {
		return field_18706;
	}
}
