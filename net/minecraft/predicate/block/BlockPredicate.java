package net.minecraft.predicate.block;

import com.google.common.base.Predicate;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;

public class BlockPredicate implements Predicate<BlockState> {
	private final Block block;

	private BlockPredicate(Block block) {
		this.block = block;
	}

	public static BlockPredicate create(Block block) {
		return new BlockPredicate(block);
	}

	public boolean apply(@Nullable BlockState blockState) {
		return blockState != null && blockState.getBlock() == this.block;
	}
}
