package net.minecraft.client.network.packet;

import java.io.IOException;
import net.minecraft.network.Packet;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.util.PacketByteBuf;

public class KeepAliveS2CPacket implements Packet<ClientPlayPacketListener> {
	private long id;

	public KeepAliveS2CPacket() {
	}

	public KeepAliveS2CPacket(long l) {
		this.id = l;
	}

	public void method_11518(ClientPlayPacketListener clientPlayPacketListener) {
		clientPlayPacketListener.onKeepAlive(this);
	}

	@Override
	public void read(PacketByteBuf packetByteBuf) throws IOException {
		this.id = packetByteBuf.readLong();
	}

	@Override
	public void write(PacketByteBuf packetByteBuf) throws IOException {
		packetByteBuf.writeLong(this.id);
	}

	public long getId() {
		return this.id;
	}
}
