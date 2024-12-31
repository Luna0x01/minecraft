package net.minecraft.network.packet.s2c.play;

import java.io.IOException;
import net.minecraft.network.Packet;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.text.Text;
import net.minecraft.util.PacketByteBuf;

public class ChatMessageS2CPacket implements Packet<ClientPlayPacketListener> {
	private Text text;
	private byte type;

	public ChatMessageS2CPacket() {
	}

	public ChatMessageS2CPacket(Text text) {
		this(text, (byte)1);
	}

	public ChatMessageS2CPacket(Text text, byte b) {
		this.text = text;
		this.type = b;
	}

	@Override
	public void read(PacketByteBuf buf) throws IOException {
		this.text = buf.readText();
		this.type = buf.readByte();
	}

	@Override
	public void write(PacketByteBuf buf) throws IOException {
		buf.writeText(this.text);
		buf.writeByte(this.type);
	}

	public void apply(ClientPlayPacketListener clientPlayPacketListener) {
		clientPlayPacketListener.onChatMessage(this);
	}

	public Text getMessage() {
		return this.text;
	}

	public boolean isNonChat() {
		return this.type == 1 || this.type == 2;
	}

	public byte getType() {
		return this.type;
	}
}
