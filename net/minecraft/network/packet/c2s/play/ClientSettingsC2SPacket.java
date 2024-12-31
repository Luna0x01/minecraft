package net.minecraft.network.packet.c2s.play;

import java.io.IOException;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.Packet;
import net.minecraft.network.listener.ServerPlayPacketListener;
import net.minecraft.util.PacketByteBuf;

public class ClientSettingsC2SPacket implements Packet<ServerPlayPacketListener> {
	private String language;
	private int viewDistance;
	private PlayerEntity.ChatVisibilityType chatVisibilityType;
	private boolean chatColors;
	private int playerModelBitMask;

	public ClientSettingsC2SPacket() {
	}

	public ClientSettingsC2SPacket(String string, int i, PlayerEntity.ChatVisibilityType chatVisibilityType, boolean bl, int j) {
		this.language = string;
		this.viewDistance = i;
		this.chatVisibilityType = chatVisibilityType;
		this.chatColors = bl;
		this.playerModelBitMask = j;
	}

	@Override
	public void read(PacketByteBuf buf) throws IOException {
		this.language = buf.readString(7);
		this.viewDistance = buf.readByte();
		this.chatVisibilityType = PlayerEntity.ChatVisibilityType.getById(buf.readByte());
		this.chatColors = buf.readBoolean();
		this.playerModelBitMask = buf.readUnsignedByte();
	}

	@Override
	public void write(PacketByteBuf buf) throws IOException {
		buf.writeString(this.language);
		buf.writeByte(this.viewDistance);
		buf.writeByte(this.chatVisibilityType.getId());
		buf.writeBoolean(this.chatColors);
		buf.writeByte(this.playerModelBitMask);
	}

	public void apply(ServerPlayPacketListener serverPlayPacketListener) {
		serverPlayPacketListener.onClientSettings(this);
	}

	public String getLanguage() {
		return this.language;
	}

	public PlayerEntity.ChatVisibilityType getChatVisibility() {
		return this.chatVisibilityType;
	}

	public boolean hasChatColors() {
		return this.chatColors;
	}

	public int getPlayerModelBitMask() {
		return this.playerModelBitMask;
	}
}
