package net.minecraft.network;

import net.minecraft.network.listener.PacketListener;

public interface Packet<T extends PacketListener> {
	void write(PacketByteBuf buf);

	void apply(T listener);

	default boolean isWritingErrorSkippable() {
		return false;
	}
}
