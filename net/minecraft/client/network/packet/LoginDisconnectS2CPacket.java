package net.minecraft.client.network.packet;

import java.io.IOException;
import net.minecraft.network.Packet;
import net.minecraft.network.listener.ClientLoginPacketListener;
import net.minecraft.text.Text;
import net.minecraft.util.PacketByteBuf;

public class LoginDisconnectS2CPacket implements Packet<ClientLoginPacketListener> {
	private Text reason;

	public LoginDisconnectS2CPacket() {
	}

	public LoginDisconnectS2CPacket(Text text) {
		this.reason = text;
	}

	@Override
	public void read(PacketByteBuf packetByteBuf) throws IOException {
		this.reason = Text.Serializer.fromLenientJson(packetByteBuf.readString(262144));
	}

	@Override
	public void write(PacketByteBuf packetByteBuf) throws IOException {
		packetByteBuf.writeText(this.reason);
	}

	public void apply(ClientLoginPacketListener clientLoginPacketListener) {
		clientLoginPacketListener.onDisconnect(this);
	}

	public Text getReason() {
		return this.reason;
	}
}
