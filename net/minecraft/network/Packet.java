package net.minecraft.network;

import java.io.IOException;
import net.minecraft.network.listener.PacketListener;

public interface Packet<T extends PacketListener> {
	void read(PacketByteBuf buf) throws IOException;

	void write(PacketByteBuf buf) throws IOException;

	void apply(T listener);

	default boolean isWritingErrorSkippable() {
		return false;
	}
}
