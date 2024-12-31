package net.minecraft.client.network.packet;

import java.io.IOException;
import java.util.Objects;
import javax.annotation.Nullable;
import net.minecraft.network.Packet;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.scoreboard.ServerScoreboard;
import net.minecraft.util.PacketByteBuf;

public class ScoreboardPlayerUpdateS2CPacket implements Packet<ClientPlayPacketListener> {
	private String playerName = "";
	@Nullable
	private String objectiveName;
	private int score;
	private ServerScoreboard.UpdateMode mode;

	public ScoreboardPlayerUpdateS2CPacket() {
	}

	public ScoreboardPlayerUpdateS2CPacket(ServerScoreboard.UpdateMode updateMode, @Nullable String string, String string2, int i) {
		if (updateMode != ServerScoreboard.UpdateMode.field_13430 && string == null) {
			throw new IllegalArgumentException("Need an objective name");
		} else {
			this.playerName = string2;
			this.objectiveName = string;
			this.score = i;
			this.mode = updateMode;
		}
	}

	@Override
	public void read(PacketByteBuf packetByteBuf) throws IOException {
		this.playerName = packetByteBuf.readString(40);
		this.mode = packetByteBuf.readEnumConstant(ServerScoreboard.UpdateMode.class);
		String string = packetByteBuf.readString(16);
		this.objectiveName = Objects.equals(string, "") ? null : string;
		if (this.mode != ServerScoreboard.UpdateMode.field_13430) {
			this.score = packetByteBuf.readVarInt();
		}
	}

	@Override
	public void write(PacketByteBuf packetByteBuf) throws IOException {
		packetByteBuf.writeString(this.playerName);
		packetByteBuf.writeEnumConstant(this.mode);
		packetByteBuf.writeString(this.objectiveName == null ? "" : this.objectiveName);
		if (this.mode != ServerScoreboard.UpdateMode.field_13430) {
			packetByteBuf.writeVarInt(this.score);
		}
	}

	public void apply(ClientPlayPacketListener clientPlayPacketListener) {
		clientPlayPacketListener.onScoreboardPlayerUpdate(this);
	}

	public String getPlayerName() {
		return this.playerName;
	}

	@Nullable
	public String getObjectiveName() {
		return this.objectiveName;
	}

	public int getScore() {
		return this.score;
	}

	public ServerScoreboard.UpdateMode getUpdateMode() {
		return this.mode;
	}
}
