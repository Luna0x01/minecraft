package net.minecraft.network.packet.s2c.play;

import java.io.IOException;
import net.minecraft.network.Packet;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.util.PacketByteBuf;

public class KeepAliveS2CPacket implements Packet<ClientPlayPacketListener> {
	private int id;

	public KeepAliveS2CPacket() {
	}

	public KeepAliveS2CPacket(int i) {
		this.id = i;
	}

	public void apply(ClientPlayPacketListener clientPlayPacketListener) {
		clientPlayPacketListener.onKeepAlive(this);
	}

	@Override
	public void read(PacketByteBuf buf) throws IOException {
		this.id = buf.readVarInt();
	}

	@Override
	public void write(PacketByteBuf buf) throws IOException {
		buf.writeVarInt(this.id);
	}

	public int getId() {
		return this.id;
	}
}
