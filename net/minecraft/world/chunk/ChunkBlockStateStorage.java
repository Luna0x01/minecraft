package net.minecraft.world.chunk;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;

public class ChunkBlockStateStorage {
	private final short[] blockStates = new short[65536];
	private final BlockState AIR = Blocks.AIR.getDefaultState();

	public BlockState get(int x, int y, int z) {
		int i = x << 12 | z << 8 | y;
		return this.get(i);
	}

	public BlockState get(int index) {
		if (index >= 0 && index < this.blockStates.length) {
			BlockState blockState = Block.BLOCK_STATES.fromId(this.blockStates[index]);
			return blockState != null ? blockState : this.AIR;
		} else {
			throw new IndexOutOfBoundsException("The coordinate is out of range");
		}
	}

	public void set(int x, int y, int z, BlockState state) {
		int i = x << 12 | z << 8 | y;
		this.set(i, state);
	}

	public void set(int index, BlockState state) {
		if (index >= 0 && index < this.blockStates.length) {
			this.blockStates[index] = (short)Block.BLOCK_STATES.getId(state);
		} else {
			throw new IndexOutOfBoundsException("The coordinate is out of range");
		}
	}
}
