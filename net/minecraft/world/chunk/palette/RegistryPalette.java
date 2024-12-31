package net.minecraft.world.chunk.palette;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.util.PacketByteBuf;

public class RegistryPalette implements Palette {
	@Override
	public int getIdForState(BlockState state) {
		int i = Block.BLOCK_STATES.getId(state);
		return i == -1 ? 0 : i;
	}

	@Override
	public BlockState getStateForId(int id) {
		BlockState blockState = Block.BLOCK_STATES.fromId(id);
		return blockState == null ? Blocks.AIR.getDefaultState() : blockState;
	}

	@Override
	public void read(PacketByteBuf buf) {
		buf.readVarInt();
	}

	@Override
	public void write(PacketByteBuf buf) {
		buf.writeVarInt(0);
	}

	@Override
	public int packetSize() {
		return PacketByteBuf.getVarIntSizeBytes(0);
	}
}
