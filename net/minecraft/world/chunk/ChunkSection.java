package net.minecraft.world.chunk;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.world.chunk.palette.PaletteContainer;

public class ChunkSection {
	private final int yOffset;
	private int containedBlockCount;
	private int tickableBlockCount;
	private final PaletteContainer blockData;
	private ChunkNibbleArray blockLight;
	private ChunkNibbleArray skyLight;

	public ChunkSection(int i, boolean bl) {
		this.yOffset = i;
		this.blockData = new PaletteContainer();
		this.blockLight = new ChunkNibbleArray();
		if (bl) {
			this.skyLight = new ChunkNibbleArray();
		}
	}

	public BlockState getBlockState(int x, int y, int z) {
		return this.blockData.getBlockState(x, y, z);
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

		this.blockData.setBlockState(x, y, z, state);
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
					Block block = this.getBlockState(i, j, k).getBlock();
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

	public PaletteContainer getBlockData() {
		return this.blockData;
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
