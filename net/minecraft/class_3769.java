package net.minecraft;

import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.tag.Tag;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockView;

public class class_3769 implements class_3761<BlockState> {
	private final Tag<Block> field_18707;

	public class_3769(Tag<Block> tag) {
		this.field_18707 = tag;
	}

	public static class_3769 method_16961(Tag<Block> tag) {
		return new class_3769(tag);
	}

	public boolean test(@Nullable BlockState blockState, BlockView blockView, BlockPos blockPos) {
		return blockState != null && blockState.isIn(this.field_18707);
	}
}
