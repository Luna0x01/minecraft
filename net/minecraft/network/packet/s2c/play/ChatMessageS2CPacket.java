package net.minecraft.network.packet.s2c.play;

import java.io.IOException;
import net.minecraft.network.Packet;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.text.Text;
import net.minecraft.util.ChatMessageType;
import net.minecraft.util.PacketByteBuf;

public class ChatMessageS2CPacket implements Packet<ClientPlayPacketListener> {
	private Text text;
	private ChatMessageType messageType;

	public ChatMessageS2CPacket() {
	}

	public ChatMessageS2CPacket(Text text) {
		this(text, ChatMessageType.SYSTEM);
	}

	public ChatMessageS2CPacket(Text text, ChatMessageType chatMessageType) {
		this.text = text;
		this.messageType = chatMessageType;
	}

	@Override
	public void read(PacketByteBuf buf) throws IOException {
		this.text = buf.readText();
		this.messageType = ChatMessageType.method_14784(buf.readByte());
	}

	@Override
	public void write(PacketByteBuf buf) throws IOException {
		buf.writeText(this.text);
		buf.writeByte(this.messageType.method_14783());
	}

	public void apply(ClientPlayPacketListener clientPlayPacketListener) {
		clientPlayPacketListener.onChatMessage(this);
	}

	public Text getMessage() {
		return this.text;
	}

	public boolean isNonChat() {
		return this.messageType == ChatMessageType.SYSTEM || this.messageType == ChatMessageType.GAME_INFO;
	}

	public ChatMessageType getMessageType() {
		return this.messageType;
	}

	@Override
	public boolean method_20197() {
		return true;
	}
}
