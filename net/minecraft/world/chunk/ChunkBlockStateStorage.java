package net.minecraft.world.chunk;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;

public class ChunkBlockStateStorage {
	private static final BlockState AIR = Blocks.AIR.getDefaultState();
	private final char[] field_12955 = new char[65536];

	public BlockState get(int x, int y, int z) {
		BlockState blockState = Block.BLOCK_STATES.fromId(this.field_12955[positionAsIndex(x, y, z)]);
		return blockState == null ? AIR : blockState;
	}

	public void set(int x, int y, int z, BlockState state) {
		this.field_12955[positionAsIndex(x, y, z)] = (char)Block.BLOCK_STATES.getId(state);
	}

	private static int positionAsIndex(int x, int y, int z) {
		return x << 12 | z << 8 | y;
	}

	public int method_11819(int x, int z) {
		int i = (x << 12 | z << 8) + 256 - 1;

		for (int j = 255; j >= 0; j--) {
			BlockState blockState = Block.BLOCK_STATES.fromId(this.field_12955[i + j]);
			if (blockState != null && blockState != AIR) {
				return j;
			}
		}

		return 0;
	}
}
