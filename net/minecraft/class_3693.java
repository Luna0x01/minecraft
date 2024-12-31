package net.minecraft;

import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.TransparentBlock;

public class class_3693 extends TransparentBlock {
	public class_3693(Block.Builder builder) {
		super(builder);
	}

	@Override
	public int getDropCount(BlockState state, Random random) {
		return 0;
	}
}
