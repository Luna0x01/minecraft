package net.minecraft.block;

import java.util.Random;

public class PackedIceBlock extends Block {
	public PackedIceBlock(Block.Builder builder) {
		super(builder);
	}

	@Override
	public int getDropCount(BlockState state, Random random) {
		return 0;
	}
}
