package net.minecraft.network.packet.s2c.play;

import java.io.IOException;
import net.minecraft.network.Packet;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.text.Text;
import net.minecraft.util.PacketByteBuf;

public class PlayerListHeaderS2CPacket implements Packet<ClientPlayPacketListener> {
	private Text header;
	private Text footer;

	public PlayerListHeaderS2CPacket() {
	}

	public PlayerListHeaderS2CPacket(Text text) {
		this.header = text;
	}

	@Override
	public void read(PacketByteBuf buf) throws IOException {
		this.header = buf.readText();
		this.footer = buf.readText();
	}

	@Override
	public void write(PacketByteBuf buf) throws IOException {
		buf.writeText(this.header);
		buf.writeText(this.footer);
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
