package net.minecraft.world.chunk.palette;

import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.util.PacketByteBuf;

public class LinearPalette implements Palette {
	private final BlockState[] blockStates;
	private final PaletteResizeListener field_12916;
	private final int bitsPerBlock;
	private int size;

	public LinearPalette(int i, PaletteResizeListener paletteResizeListener) {
		this.blockStates = new BlockState[1 << i];
		this.bitsPerBlock = i;
		this.field_12916 = paletteResizeListener;
	}

	@Override
	public int getIdForState(BlockState state) {
		for (int i = 0; i < this.size; i++) {
			if (this.blockStates[i] == state) {
				return i;
			}
		}

		int j = this.size;
		if (j < this.blockStates.length) {
			this.blockStates[j] = state;
			this.size++;
			return j;
		} else {
			return this.field_12916.resizePalette(this.bitsPerBlock + 1, state);
		}
	}

	@Nullable
	@Override
	public BlockState getStateForId(int id) {
		return id >= 0 && id < this.size ? this.blockStates[id] : null;
	}

	@Override
	public void read(PacketByteBuf buf) {
		this.size = buf.readVarInt();

		for (int i = 0; i < this.size; i++) {
			this.blockStates[i] = Block.BLOCK_STATES.fromId(buf.readVarInt());
		}
	}

	@Override
	public void write(PacketByteBuf buf) {
		buf.writeVarInt(this.size);

		for (int i = 0; i < this.size; i++) {
			buf.writeVarInt(Block.BLOCK_STATES.getId(this.blockStates[i]));
		}
	}

	@Override
	public int packetSize() {
		int i = PacketByteBuf.getVarIntSizeBytes(this.size);

		for (int j = 0; j < this.size; j++) {
			i += PacketByteBuf.getVarIntSizeBytes(Block.BLOCK_STATES.getId(this.blockStates[j]));
		}

		return i;
	}
}
