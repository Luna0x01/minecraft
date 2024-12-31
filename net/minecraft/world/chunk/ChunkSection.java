package net.minecraft.world.chunk;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.fluid.FluidState;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.world.chunk.palette.Palette;
import net.minecraft.world.chunk.palette.PaletteContainer;
import net.minecraft.world.chunk.palette.RegistryPalette;

public class ChunkSection {
	private static final Palette<BlockState> field_18896 = new RegistryPalette<>(Block.BLOCK_STATES, Blocks.AIR.getDefaultState());
	private final int yOffset;
	private int containedBlockCount;
	private int tickableBlockCount;
	private int field_18897;
	private final PaletteContainer<BlockState> field_18898;
	private ChunkNibbleArray blockLight;
	private ChunkNibbleArray skyLight;

	public ChunkSection(int i, boolean bl) {
		this.yOffset = i;
		this.field_18898 = new PaletteContainer<>(field_18896, Block.BLOCK_STATES, NbtHelper::toBlockState, NbtHelper::method_20139, Blocks.AIR.getDefaultState());
		this.blockLight = new ChunkNibbleArray();
		if (bl) {
			this.skyLight = new ChunkNibbleArray();
		}
	}

	public BlockState getBlockState(int x, int y, int z) {
		return this.field_18898.method_17100(x, y, z);
	}

	public FluidState method_17093(int i, int j, int k) {
		return this.field_18898.method_17100(i, j, k).getFluidState();
	}

	public void setBlockState(int x, int y, int z, BlockState state) {
		BlockState blockState = this.getBlockState(x, y, z);
		FluidState fluidState = this.method_17093(x, y, z);
		FluidState fluidState2 = state.getFluidState();
		if (!blockState.isAir()) {
			this.containedBlockCount--;
			if (blockState.hasRandomTicks()) {
				this.tickableBlockCount--;
			}
		}

		if (!fluidState.isEmpty()) {
			this.field_18897--;
		}

		if (!state.isAir()) {
			this.containedBlockCount++;
			if (state.hasRandomTicks()) {
				this.tickableBlockCount++;
			}
		}

		if (!fluidState2.isEmpty()) {
			this.field_18897--;
		}

		this.field_18898.method_17101(x, y, z, state);
	}

	public boolean isEmpty() {
		return this.containedBlockCount == 0;
	}

	public boolean method_17092() {
		return this.hasTickableBlocks() || this.method_17094();
	}

	public boolean hasTickableBlocks() {
		return this.tickableBlockCount > 0;
	}

	public boolean method_17094() {
		return this.field_18897 > 0;
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
		this.field_18897 = 0;

		for (int i = 0; i < 16; i++) {
			for (int j = 0; j < 16; j++) {
				for (int k = 0; k < 16; k++) {
					BlockState blockState = this.getBlockState(i, j, k);
					FluidState fluidState = this.method_17093(i, j, k);
					if (!blockState.isAir()) {
						this.containedBlockCount++;
						if (blockState.hasRandomTicks()) {
							this.tickableBlockCount++;
						}
					}

					if (!fluidState.isEmpty()) {
						this.containedBlockCount++;
						if (fluidState.method_17812()) {
							this.field_18897++;
						}
					}
				}
			}
		}
	}

	public PaletteContainer<BlockState> getBlockData() {
		return this.field_18898;
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
