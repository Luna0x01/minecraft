package net.minecraft;

import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockView;

public class class_3760 implements class_3761<BlockState> {
	private final Block field_18698;

	public class_3760(Block block) {
		this.field_18698 = block;
	}

	public static class_3760 method_16942(Block block) {
		return new class_3760(block);
	}

	public boolean test(@Nullable BlockState blockState, BlockView blockView, BlockPos blockPos) {
		return blockState != null && blockState.getBlock() == this.field_18698;
	}
}
