package net.minecraft.network.packet.s2c.play;

import java.io.IOException;
import net.minecraft.network.Packet;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.util.PacketByteBuf;

public class CustomPayloadS2CPacket implements Packet<ClientPlayPacketListener> {
	private String channel;
	private PacketByteBuf payload;

	public CustomPayloadS2CPacket() {
	}

	public CustomPayloadS2CPacket(String string, PacketByteBuf packetByteBuf) {
		this.channel = string;
		this.payload = packetByteBuf;
		if (packetByteBuf.writerIndex() > 1048576) {
			throw new IllegalArgumentException("Payload may not be larger than 1048576 bytes");
		}
	}

	@Override
	public void read(PacketByteBuf buf) throws IOException {
		this.channel = buf.readString(20);
		int i = buf.readableBytes();
		if (i >= 0 && i <= 1048576) {
			this.payload = new PacketByteBuf(buf.readBytes(i));
		} else {
			throw new IOException("Payload may not be larger than 1048576 bytes");
		}
	}

	@Override
	public void write(PacketByteBuf buf) throws IOException {
		buf.writeString(this.channel);
		buf.writeBytes(this.payload);
	}

	public void apply(ClientPlayPacketListener clientPlayPacketListener) {
		clientPlayPacketListener.onCustomPayload(this);
	}

	public String getChannel() {
		return this.channel;
	}

	public PacketByteBuf getPayload() {
		return this.payload;
	}
}
