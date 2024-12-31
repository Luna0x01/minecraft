package net.minecraft.world.chunk.palette;

import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.util.PacketByteBuf;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.chunk.ChunkNibbleArray;

public class PaletteContainer implements PaletteResizeListener {
	private static final Palette REGISTRY_PALETTE = new RegistryPalette();
	protected static final BlockState AIR = Blocks.AIR.getDefaultState();
	protected PaletteData storage;
	protected Palette palette;
	private int bitsPerBlock;

	public PaletteContainer() {
		this.setPaletteSize(4);
	}

	private static int getPositionFor(int x, int y, int z) {
		return y << 8 | z << 4 | x;
	}

	private void setPaletteSize(int bitsPerBlock) {
		if (bitsPerBlock != this.bitsPerBlock) {
			this.bitsPerBlock = bitsPerBlock;
			if (this.bitsPerBlock <= 4) {
				this.bitsPerBlock = 4;
				this.palette = new LinearPalette(this.bitsPerBlock, this);
			} else if (this.bitsPerBlock <= 8) {
				this.palette = new HashMapPalette(this.bitsPerBlock, this);
			} else {
				this.palette = REGISTRY_PALETTE;
				this.bitsPerBlock = MathHelper.log2DeBruijn(Block.BLOCK_STATES.size());
			}

			this.palette.getIdForState(AIR);
			this.storage = new PaletteData(this.bitsPerBlock, 4096);
		}
	}

	@Override
	public int resizePalette(int bitsPerBlock, BlockState state) {
		PaletteData paletteData = this.storage;
		Palette palette = this.palette;
		this.setPaletteSize(bitsPerBlock);

		for (int i = 0; i < paletteData.getMaxBlockAmount(); i++) {
			BlockState blockState = palette.getStateForId(paletteData.get(i));
			if (blockState != null) {
				this.setBlockState(i, blockState);
			}
		}

		return this.palette.getIdForState(state);
	}

	public void setBlockState(int x, int y, int z, BlockState state) {
		this.setBlockState(getPositionFor(x, y, z), state);
	}

	protected void setBlockState(int position, BlockState state) {
		int i = this.palette.getIdForState(state);
		this.storage.set(position, i);
	}

	public BlockState getBlockState(int x, int y, int z) {
		return this.getBlockState(getPositionFor(x, y, z));
	}

	protected BlockState getBlockState(int position) {
		BlockState blockState = this.palette.getStateForId(this.storage.get(position));
		return blockState == null ? AIR : blockState;
	}

	public void read(PacketByteBuf buf) {
		int i = buf.readByte();
		if (this.bitsPerBlock != i) {
			this.setPaletteSize(i);
		}

		this.palette.read(buf);
		buf.readLongArray(this.storage.getBlockStateIds());
	}

	public void write(PacketByteBuf buf) {
		buf.writeByte(this.bitsPerBlock);
		this.palette.write(buf);
		buf.writeLongArray(this.storage.getBlockStateIds());
	}

	@Nullable
	public ChunkNibbleArray store(byte[] blockState, ChunkNibbleArray metadata) {
		ChunkNibbleArray chunkNibbleArray = null;

		for (int i = 0; i < 4096; i++) {
			int j = Block.BLOCK_STATES.getId(this.getBlockState(i));
			int k = i & 15;
			int l = i >> 8 & 15;
			int m = i >> 4 & 15;
			if ((j >> 12 & 15) != 0) {
				if (chunkNibbleArray == null) {
					chunkNibbleArray = new ChunkNibbleArray();
				}

				chunkNibbleArray.set(k, l, m, j >> 12 & 15);
			}

			blockState[i] = (byte)(j >> 4 & 0xFF);
			metadata.set(k, l, m, j & 15);
		}

		return chunkNibbleArray;
	}

	public void load(byte[] blockState, ChunkNibbleArray metadata, @Nullable ChunkNibbleArray upperBits) {
		for (int i = 0; i < 4096; i++) {
			int j = i & 15;
			int k = i >> 8 & 15;
			int l = i >> 4 & 15;
			int m = upperBits == null ? 0 : upperBits.get(j, k, l);
			int n = m << 12 | (blockState[i] & 255) << 4 | metadata.get(j, k, l);
			this.setBlockState(i, Block.BLOCK_STATES.fromId(n));
		}
	}

	public int packetSize() {
		return 1 + this.palette.packetSize() + PacketByteBuf.getVarIntSizeBytes(this.storage.getMaxBlockAmount()) + this.storage.getBlockStateIds().length * 8;
	}
}
