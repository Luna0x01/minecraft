package net.minecraft.world.chunk;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;

public class ChunkSection {
	private int yOffset;
	private int containedBlockCount;
	private int tickableBlockCount;
	private char[] blockStates;
	private ChunkNibbleArray blockLight;
	private ChunkNibbleArray skyLight;

	public ChunkSection(int i, boolean bl) {
		this.yOffset = i;
		this.blockStates = new char[4096];
		this.blockLight = new ChunkNibbleArray();
		if (bl) {
			this.skyLight = new ChunkNibbleArray();
		}
	}

	public BlockState getBlockState(int x, int y, int z) {
		BlockState blockState = Block.BLOCK_STATES.fromId(this.blockStates[y << 8 | z << 4 | x]);
		return blockState != null ? blockState : Blocks.AIR.getDefaultState();
	}

	public void setBlockState(int x, int y, int z, BlockState state) {
		BlockState blockState = this.getBlockState(x, y, z);
		Block block = blockState.getBlock();
		Block block2 = state.getBlock();
		if (block != Blocks.AIR) {
			this.containedBlockCount--;
			if (block.ticksRandomly()) {
				this.tickableBlockCount--;
			}
		}

		if (block2 != Blocks.AIR) {
			this.containedBlockCount++;
			if (block2.ticksRandomly()) {
				this.tickableBlockCount++;
			}
		}

		this.blockStates[y << 8 | z << 4 | x] = (char)Block.BLOCK_STATES.getId(state);
	}

	public Block getBlock(int x, int y, int z) {
		return this.getBlockState(x, y, z).getBlock();
	}

	public int getBlockData(int x, int y, int z) {
		BlockState blockState = this.getBlockState(x, y, z);
		return blockState.getBlock().getData(blockState);
	}

	public boolean isEmpty() {
		return this.containedBlockCount == 0;
	}

	public boolean hasTickableBlocks() {
		return this.tickableBlockCount > 0;
	}

	public int getYOffset() {
		return this.yOffset;
	}

	public void setSkyLight(int x, int y, int z, int lightLevel) {
		this.skyLight.set(x, y, z, lightLevel);
	}

	public int getSkyLight(int x, int y, int z) {
		return this.skyLight.get(x, y, z);
	}

	public void setBlockLight(int x, int y, int z, int lightLevel) {
		this.blockLight.set(x, y, z, lightLevel);
	}

	public int getBlockLight(int x, int y, int z) {
		return this.blockLight.get(x, y, z);
	}

	public void calculateCounts() {
		this.containedBlockCount = 0;
		this.tickableBlockCount = 0;

		for (int i = 0; i < 16; i++) {
			for (int j = 0; j < 16; j++) {
				for (int k = 0; k < 16; k++) {
					Block block = this.getBlock(i, j, k);
					if (block != Blocks.AIR) {
						this.containedBlockCount++;
						if (block.ticksRandomly()) {
							this.tickableBlockCount++;
						}
					}
				}
			}
		}
	}

	public char[] getBlockStates() {
		return this.blockStates;
	}

	public void setBlockStates(char[] blockStates) {
		this.blockStates = blockStates;
	}

	public ChunkNibbleArray getBlockLight() {
		return this.blockLight;
	}

	public ChunkNibbleArray getSkyLight() {
		return this.skyLight;
	}

	public void setBlockLight(ChunkNibbleArray blockLight) {
		this.blockLight = blockLight;
	}

	public void setSkyLight(ChunkNibbleArray skyLight) {
		this.skyLight = skyLight;
	}
}
