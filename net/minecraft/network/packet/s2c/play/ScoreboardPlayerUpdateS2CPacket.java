package net.minecraft.network.packet.s2c.play;

import java.io.IOException;
import net.minecraft.network.Packet;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.scoreboard.ScoreboardObjective;
import net.minecraft.scoreboard.ScoreboardPlayerScore;
import net.minecraft.util.PacketByteBuf;

public class ScoreboardPlayerUpdateS2CPacket implements Packet<ClientPlayPacketListener> {
	private String playerName = "";
	private String objectiveName = "";
	private int score;
	private ScoreboardPlayerUpdateS2CPacket.UpdateType type;

	public ScoreboardPlayerUpdateS2CPacket() {
	}

	public ScoreboardPlayerUpdateS2CPacket(ScoreboardPlayerScore scoreboardPlayerScore) {
		this.playerName = scoreboardPlayerScore.getPlayerName();
		this.objectiveName = scoreboardPlayerScore.getObjective().getName();
		this.score = scoreboardPlayerScore.getScore();
		this.type = ScoreboardPlayerUpdateS2CPacket.UpdateType.CHANGE;
	}

	public ScoreboardPlayerUpdateS2CPacket(String string) {
		this.playerName = string;
		this.objectiveName = "";
		this.score = 0;
		this.type = ScoreboardPlayerUpdateS2CPacket.UpdateType.REMOVE;
	}

	public ScoreboardPlayerUpdateS2CPacket(String string, ScoreboardObjective scoreboardObjective) {
		this.playerName = string;
		this.objectiveName = scoreboardObjective.getName();
		this.score = 0;
		this.type = ScoreboardPlayerUpdateS2CPacket.UpdateType.REMOVE;
	}

	@Override
	public void read(PacketByteBuf buf) throws IOException {
		this.playerName = buf.readString(40);
		this.type = buf.readEnumConstant(ScoreboardPlayerUpdateS2CPacket.UpdateType.class);
		this.objectiveName = buf.readString(16);
		if (this.type != ScoreboardPlayerUpdateS2CPacket.UpdateType.REMOVE) {
			this.score = buf.readVarInt();
		}
	}

	@Override
	public void write(PacketByteBuf buf) throws IOException {
		buf.writeString(this.playerName);
		buf.writeEnum(this.type);
		buf.writeString(this.objectiveName);
		if (this.type != ScoreboardPlayerUpdateS2CPacket.UpdateType.REMOVE) {
			buf.writeVarInt(this.score);
		}
	}

	public void apply(ClientPlayPacketListener clientPlayPacketListener) {
		clientPlayPacketListener.onScoreboardPlayerUpdate(this);
	}

	public String getPlayerName() {
		return this.playerName;
	}

	public String getObjectiveName() {
		return this.objectiveName;
	}

	public int getScore() {
		return this.score;
	}

	public ScoreboardPlayerUpdateS2CPacket.UpdateType getType() {
		return this.type;
	}

	public static enum UpdateType {
		CHANGE,
		REMOVE;
	}
}
