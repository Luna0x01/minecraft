package net.minecraft.network.packet.c2s.play;

import java.io.IOException;
import net.minecraft.network.Packet;
import net.minecraft.network.listener.ServerPlayPacketListener;
import net.minecraft.util.PacketByteBuf;

public class KeepAliveC2SPacket implements Packet<ServerPlayPacketListener> {
	private int time;

	public KeepAliveC2SPacket() {
	}

	public KeepAliveC2SPacket(int i) {
		this.time = i;
	}

	public void apply(ServerPlayPacketListener serverPlayPacketListener) {
		serverPlayPacketListener.onKeepAlive(this);
	}

	@Override
	public void read(PacketByteBuf buf) throws IOException {
		this.time = buf.readVarInt();
	}

	@Override
	public void write(PacketByteBuf buf) throws IOException {
		buf.writeVarInt(this.time);
	}

	public int getTime() {
		return this.time;
	}
}
