package net.minecraft.network.packet.c2s.play;

import java.io.IOException;
import net.minecraft.network.Packet;
import net.minecraft.network.listener.ServerPlayPacketListener;
import net.minecraft.util.PacketByteBuf;

public class KeepAliveC2SPacket implements Packet<ServerPlayPacketListener> {
	private long field_16310;

	public KeepAliveC2SPacket() {
	}

	public KeepAliveC2SPacket(long l) {
		this.field_16310 = l;
	}

	public void apply(ServerPlayPacketListener serverPlayPacketListener) {
		serverPlayPacketListener.onKeepAlive(this);
	}

	@Override
	public void read(PacketByteBuf buf) throws IOException {
		this.field_16310 = buf.readLong();
	}

	@Override
	public void write(PacketByteBuf buf) throws IOException {
		buf.writeLong(this.field_16310);
	}

	public long method_7988() {
		return this.field_16310;
	}
}
