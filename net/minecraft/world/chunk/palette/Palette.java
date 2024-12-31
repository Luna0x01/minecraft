package net.minecraft.world.chunk.palette;

import javax.annotation.Nullable;
import net.minecraft.nbt.NbtList;
import net.minecraft.util.PacketByteBuf;

public interface Palette<T> {
	int method_17098(T object);

	@Nullable
	T method_17096(int i);

	void read(PacketByteBuf buf);

	void write(PacketByteBuf buf);

	int packetSize();

	void method_17097(NbtList nbtList);
}
