package net.minecraft.network.packet.c2s.query;

import java.io.IOException;
import net.minecraft.network.Packet;
import net.minecraft.network.listener.ServerQueryPacketListener;
import net.minecraft.util.PacketByteBuf;

public class QueryPingC2SPacket implements Packet<ServerQueryPacketListener> {
	private long startTime;

	public QueryPingC2SPacket() {
	}

	public QueryPingC2SPacket(long l) {
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

	public void apply(ServerQueryPacketListener serverQueryPacketListener) {
		serverQueryPacketListener.onPing(this);
	}

	public long getStartTime() {
		return this.startTime;
	}
}
