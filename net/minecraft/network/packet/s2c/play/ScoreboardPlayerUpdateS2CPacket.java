package net.minecraft.network.packet.s2c.play;

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
	private ServerScoreboard.class_4401 field_11673;

	public ScoreboardPlayerUpdateS2CPacket() {
	}

	public ScoreboardPlayerUpdateS2CPacket(ServerScoreboard.class_4401 arg, @Nullable String string, String string2, int i) {
		if (arg != ServerScoreboard.class_4401.REMOVE && string == null) {
			throw new IllegalArgumentException("Need an objective name");
		} else {
			this.playerName = string2;
			this.objectiveName = string;
			this.score = i;
			this.field_11673 = arg;
		}
	}

	@Override
	public void read(PacketByteBuf buf) throws IOException {
		this.playerName = buf.readString(40);
		this.field_11673 = buf.readEnumConstant(ServerScoreboard.class_4401.class);
		String string = buf.readString(16);
		this.objectiveName = Objects.equals(string, "") ? null : string;
		if (this.field_11673 != ServerScoreboard.class_4401.REMOVE) {
			this.score = buf.readVarInt();
		}
	}

	@Override
	public void write(PacketByteBuf buf) throws IOException {
		buf.writeString(this.playerName);
		buf.writeEnumConstant(this.field_11673);
		buf.writeString(this.objectiveName == null ? "" : this.objectiveName);
		if (this.field_11673 != ServerScoreboard.class_4401.REMOVE) {
			buf.writeVarInt(this.score);
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

	public ServerScoreboard.class_4401 method_7897() {
		return this.field_11673;
	}
}
