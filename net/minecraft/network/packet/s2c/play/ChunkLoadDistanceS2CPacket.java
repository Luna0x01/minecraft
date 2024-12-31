package net.minecraft.network.packet.s2c.play;

import java.io.IOException;
import net.minecraft.network.Packet;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.util.PacketByteBuf;

public class ChunkLoadDistanceS2CPacket implements Packet<ClientPlayPacketListener> {
	private int distance;

	@Override
	public void read(PacketByteBuf buf) throws IOException {
		this.distance = buf.readVarInt();
	}

	@Override
	public void write(PacketByteBuf buf) throws IOException {
		buf.writeVarInt(this.distance);
	}

	public void apply(ClientPlayPacketListener clientPlayPacketListener) {
		clientPlayPacketListener.onChunkLoadDistance(this);
	}

	public int getDistance() {
		return this.distance;
	}
}
