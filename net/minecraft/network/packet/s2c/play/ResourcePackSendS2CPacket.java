package net.minecraft.network.packet.s2c.play;

import java.io.IOException;
import net.minecraft.network.Packet;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.util.PacketByteBuf;

public class ResourcePackSendS2CPacket implements Packet<ClientPlayPacketListener> {
	private String url;
	private String hash;

	public ResourcePackSendS2CPacket() {
	}

	public ResourcePackSendS2CPacket(String string, String string2) {
		this.url = string;
		this.hash = string2;
		if (string2.length() > 40) {
			throw new IllegalArgumentException("Hash is too long (max 40, was " + string2.length() + ")");
		}
	}

	@Override
	public void read(PacketByteBuf buf) throws IOException {
		this.url = buf.readString(32767);
		this.hash = buf.readString(40);
	}

	@Override
	public void write(PacketByteBuf buf) throws IOException {
		buf.writeString(this.url);
		buf.writeString(this.hash);
	}

	public void apply(ClientPlayPacketListener clientPlayPacketListener) {
		clientPlayPacketListener.onResourcePackSend(this);
	}

	public String getURL() {
		return this.url;
	}

	public String getHash() {
		return this.hash;
	}
}
