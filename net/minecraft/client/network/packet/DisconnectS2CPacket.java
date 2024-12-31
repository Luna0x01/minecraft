package net.minecraft.client.network.packet;

import java.io.IOException;
import net.minecraft.network.Packet;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.text.Text;
import net.minecraft.util.PacketByteBuf;

public class DisconnectS2CPacket implements Packet<ClientPlayPacketListener> {
	private Text reason;

	public DisconnectS2CPacket() {
	}

	public DisconnectS2CPacket(Text text) {
		this.reason = text;
	}

	@Override
	public void read(PacketByteBuf packetByteBuf) throws IOException {
		this.reason = packetByteBuf.readText();
	}

	@Override
	public void write(PacketByteBuf packetByteBuf) throws IOException {
		packetByteBuf.writeText(this.reason);
	}

	public void apply(ClientPlayPacketListener clientPlayPacketListener) {
		clientPlayPacketListener.onDisconnect(this);
	}

	public Text getReason() {
		return this.reason;
	}
}
