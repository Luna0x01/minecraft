package net.minecraft.entity.data;

import net.minecraft.util.PacketByteBuf;

public interface TrackedDataHandler<T> {
	void write(PacketByteBuf buf, T value);

	T read(PacketByteBuf buf);

	TrackedData<T> create(int i);
}
