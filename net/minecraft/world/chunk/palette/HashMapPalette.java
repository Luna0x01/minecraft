package net.minecraft.world.chunk.palette;

import javax.annotation.Nullable;
import net.minecraft.class_2929;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.util.PacketByteBuf;

public class HashMapPalette implements Palette {
	private final class_2929<BlockState> field_12908;
	private final PaletteResizeListener field_12909;
	private final int bitsPerBlock;

	public HashMapPalette(int i, PaletteResizeListener paletteResizeListener) {
		this.bitsPerBlock = i;
		this.field_12909 = paletteResizeListener;
		this.field_12908 = new class_2929<>(1 << i);
	}

	@Override
	public int getIdForState(BlockState state) {
		int i = this.field_12908.getId(state);
		if (i == -1) {
			i = this.field_12908.method_12864(state);
			if (i >= 1 << this.bitsPerBlock) {
				i = this.field_12909.resizePalette(this.bitsPerBlock + 1, state);
			}
		}

		return i;
	}

	@Nullable
	@Override
	public BlockState getStateForId(int id) {
		return this.field_12908.getById(id);
	}

	@Override
	public void read(PacketByteBuf buf) {
		this.field_12908.clear();
		int i = buf.readVarInt();

		for (int j = 0; j < i; j++) {
			this.field_12908.method_12864(Block.BLOCK_STATES.fromId(buf.readVarInt()));
		}
	}

	@Override
	public void write(PacketByteBuf buf) {
		int i = this.field_12908.size();
		buf.writeVarInt(i);

		for (int j = 0; j < i; j++) {
			buf.writeVarInt(Block.BLOCK_STATES.getId(this.field_12908.getById(j)));
		}
	}

	@Override
	public int packetSize() {
		int i = PacketByteBuf.getVarIntSizeBytes(this.field_12908.size());

		for (int j = 0; j < this.field_12908.size(); j++) {
			i += PacketByteBuf.getVarIntSizeBytes(Block.BLOCK_STATES.getId(this.field_12908.getById(j)));
		}

		return i;
	}
}
