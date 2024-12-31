package net.minecraft.network.packet.s2c.play;

import java.io.IOException;
import net.minecraft.network.Packet;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.util.PacketByteBuf;

public class KeepAliveS2CPacket implements Packet<ClientPlayPacketListener> {
	private long field_16284;

	public KeepAliveS2CPacket() {
	}

	public KeepAliveS2CPacket(long l) {
		this.field_16284 = l;
	}

	public void apply(ClientPlayPacketListener clientPlayPacketListener) {
		clientPlayPacketListener.onKeepAlive(this);
	}

	@Override
	public void read(PacketByteBuf buf) throws IOException {
		this.field_16284 = buf.readLong();
	}

	@Override
	public void write(PacketByteBuf buf) throws IOException {
		buf.writeLong(this.field_16284);
	}

	public long method_7753() {
		return this.field_16284;
	}
}
