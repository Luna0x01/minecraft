package net.minecraft;

import java.util.function.Predicate;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;

public class class_3875 implements class_3845 {
	public static final Predicate<BlockState> field_19226 = blockState -> {
		if (blockState == null) {
			return false;
		} else {
			Block block = blockState.getBlock();
			return block == Blocks.STONE || block == Blocks.GRANITE || block == Blocks.DIORITE || block == Blocks.ANDESITE;
		}
	};
	public final Predicate<BlockState> field_19227;
	public final int field_19228;
	public final BlockState field_19229;

	public class_3875(Predicate<BlockState> predicate, BlockState blockState, int i) {
		this.field_19228 = i;
		this.field_19229 = blockState;
		this.field_19227 = predicate;
	}
}
