package net.minecraft.network.packet.c2s.play;

import java.io.IOException;
import net.minecraft.network.Packet;
import net.minecraft.network.listener.ServerPlayPacketListener;
import net.minecraft.util.PacketByteBuf;

public class CustomPayloadC2SPacket implements Packet<ServerPlayPacketListener> {
	private String channel;
	private PacketByteBuf payload;

	public CustomPayloadC2SPacket() {
	}

	public CustomPayloadC2SPacket(String string, PacketByteBuf packetByteBuf) {
		this.channel = string;
		this.payload = packetByteBuf;
		if (packetByteBuf.writerIndex() > 32767) {
			throw new IllegalArgumentException("Payload may not be larger than 32767 bytes");
		}
	}

	@Override
	public void read(PacketByteBuf buf) throws IOException {
		this.channel = buf.readString(20);
		int i = buf.readableBytes();
		if (i >= 0 && i <= 32767) {
			this.payload = new PacketByteBuf(buf.readBytes(i));
		} else {
			throw new IOException("Payload may not be larger than 32767 bytes");
		}
	}

	@Override
	public void write(PacketByteBuf buf) throws IOException {
		buf.writeString(this.channel);
		buf.writeBytes(this.payload);
	}

	public void apply(ServerPlayPacketListener serverPlayPacketListener) {
		serverPlayPacketListener.onCustomPayload(this);
		if (this.payload != null) {
			this.payload.release();
		}
	}

	public String getChannel() {
		return this.channel;
	}

	public PacketByteBuf getPayload() {
		return this.payload;
	}
}
