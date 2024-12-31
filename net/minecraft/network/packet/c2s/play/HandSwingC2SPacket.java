package net.minecraft.network.packet.c2s.play;

import java.io.IOException;
import net.minecraft.network.Packet;
import net.minecraft.network.listener.ServerPlayPacketListener;
import net.minecraft.util.PacketByteBuf;

public class HandSwingC2SPacket implements Packet<ServerPlayPacketListener> {
	@Override
	public void read(PacketByteBuf buf) throws IOException {
	}

	@Override
	public void write(PacketByteBuf buf) throws IOException {
	}

	public void apply(ServerPlayPacketListener serverPlayPacketListener) {
		serverPlayPacketListener.onHandSwing(this);
	}
}
