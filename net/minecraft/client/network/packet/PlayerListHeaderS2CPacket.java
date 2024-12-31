package net.minecraft.client.network.packet;

import java.io.IOException;
import net.minecraft.network.Packet;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.text.Text;
import net.minecraft.util.PacketByteBuf;

public class PlayerListHeaderS2CPacket implements Packet<ClientPlayPacketListener> {
	private Text header;
	private Text footer;

	@Override
	public void read(PacketByteBuf packetByteBuf) throws IOException {
		this.header = packetByteBuf.readText();
		this.footer = packetByteBuf.readText();
	}

	@Override
	public void write(PacketByteBuf packetByteBuf) throws IOException {
		packetByteBuf.writeText(this.header);
		packetByteBuf.writeText(this.footer);
	}

	public void apply(ClientPlayPacketListener clientPlayPacketListener) {
		clientPlayPacketListener.onPlayerListHeader(this);
	}

	public Text getHeader() {
		return this.header;
	}

	public Text getFooter() {
		return this.footer;
	}
}
