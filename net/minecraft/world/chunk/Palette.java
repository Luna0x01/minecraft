package net.minecraft.world.chunk;

import javax.annotation.Nullable;
import net.minecraft.nbt.ListTag;
import net.minecraft.util.PacketByteBuf;

public interface Palette<T> {
	int getIndex(T object);

	boolean accepts(T object);

	@Nullable
	T getByIndex(int i);

	void fromPacket(PacketByteBuf packetByteBuf);

	void toPacket(PacketByteBuf packetByteBuf);

	int getPacketSize();

	void fromTag(ListTag listTag);
}
