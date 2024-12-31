package net.minecraft.network.packet.c2s.play;

import java.io.IOException;
import net.minecraft.client.gui.screen.options.HandOption;
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
	private HandOption field_13797;

	public ClientSettingsC2SPacket() {
	}

	public ClientSettingsC2SPacket(String string, int i, PlayerEntity.ChatVisibilityType chatVisibilityType, boolean bl, int j, HandOption handOption) {
		this.language = string;
		this.viewDistance = i;
		this.chatVisibilityType = chatVisibilityType;
		this.chatColors = bl;
		this.playerModelBitMask = j;
		this.field_13797 = handOption;
	}

	@Override
	public void read(PacketByteBuf buf) throws IOException {
		this.language = buf.readString(16);
		this.viewDistance = buf.readByte();
		this.chatVisibilityType = buf.readEnumConstant(PlayerEntity.ChatVisibilityType.class);
		this.chatColors = buf.readBoolean();
		this.playerModelBitMask = buf.readUnsignedByte();
		this.field_13797 = buf.readEnumConstant(HandOption.class);
	}

	@Override
	public void write(PacketByteBuf buf) throws IOException {
		buf.writeString(this.language);
		buf.writeByte(this.viewDistance);
		buf.writeEnumConstant(this.chatVisibilityType);
		buf.writeBoolean(this.chatColors);
		buf.writeByte(this.playerModelBitMask);
		buf.writeEnumConstant(this.field_13797);
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

	public HandOption method_12685() {
		return this.field_13797;
	}
}
