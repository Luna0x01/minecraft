package net.minecraft.client.network.packet;

import java.io.IOException;
import net.minecraft.network.Packet;
import net.minecraft.network.listener.ClientLoginPacketListener;
import net.minecraft.util.Identifier;
import net.minecraft.util.PacketByteBuf;

public class LoginQueryRequestS2CPacket implements Packet<ClientLoginPacketListener> {
	private int queryId;
	private Identifier channel;
	private PacketByteBuf payload;

	@Override
	public void read(PacketByteBuf packetByteBuf) throws IOException {
		this.queryId = packetByteBuf.readVarInt();
		this.channel = packetByteBuf.readIdentifier();
		int i = packetByteBuf.readableBytes();
		if (i >= 0 && i <= 1048576) {
			this.payload = new PacketByteBuf(packetByteBuf.readBytes(i));
		} else {
			throw new IOException("Payload may not be larger than 1048576 bytes");
		}
	}

	@Override
	public void write(PacketByteBuf packetByteBuf) throws IOException {
		packetByteBuf.writeVarInt(this.queryId);
		packetByteBuf.writeIdentifier(this.channel);
		packetByteBuf.writeBytes(this.payload.copy());
	}

	public void apply(ClientLoginPacketListener clientLoginPacketListener) {
		clientLoginPacketListener.onQueryRequest(this);
	}

	public int getQueryId() {
		return this.queryId;
	}
}
