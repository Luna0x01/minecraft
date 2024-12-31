package net.minecraft.world.chunk.palette;

import javax.annotation.Nullable;
import net.minecraft.block.BlockState;
import net.minecraft.util.PacketByteBuf;

public interface Palette {
	int getIdForState(BlockState state);

	@Nullable
	BlockState getStateForId(int id);

	void read(PacketByteBuf buf);

	void write(PacketByteBuf buf);

	int packetSize();
}
