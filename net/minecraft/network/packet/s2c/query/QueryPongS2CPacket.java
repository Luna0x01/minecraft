package net.minecraft.network.packet.s2c.query;

import java.io.IOException;
import net.minecraft.network.Packet;
import net.minecraft.network.listener.ClientQueryPacketListener;
import net.minecraft.util.PacketByteBuf;

public class QueryPongS2CPacket implements Packet<ClientQueryPacketListener> {
	private long startTime;

	public QueryPongS2CPacket() {
	}

	public QueryPongS2CPacket(long l) {
		this.startTime = l;
	}

	@Override
	public void read(PacketByteBuf buf) throws IOException {
		this.startTime = buf.readLong();
	}

	@Override
	public void write(PacketByteBuf buf) throws IOException {
		buf.writeLong(this.startTime);
	}

	public void apply(ClientQueryPacketListener clientQueryPacketListener) {
		clientQueryPacketListener.onPong(this);
	}
}
