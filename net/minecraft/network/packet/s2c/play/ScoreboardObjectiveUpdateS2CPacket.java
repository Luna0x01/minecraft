package net.minecraft.network.packet.s2c.play;

import java.io.IOException;
import net.minecraft.network.Packet;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.scoreboard.ScoreboardCriterion;
import net.minecraft.scoreboard.ScoreboardObjective;
import net.minecraft.util.PacketByteBuf;

public class ScoreboardObjectiveUpdateS2CPacket implements Packet<ClientPlayPacketListener> {
	private String name;
	private String displayName;
	private ScoreboardCriterion.RenderType type;
	private int mode;

	public ScoreboardObjectiveUpdateS2CPacket() {
	}

	public ScoreboardObjectiveUpdateS2CPacket(ScoreboardObjective scoreboardObjective, int i) {
		this.name = scoreboardObjective.getName();
		this.displayName = scoreboardObjective.getDisplayName();
		this.type = scoreboardObjective.getCriterion().getRenderType();
		this.mode = i;
	}

	@Override
	public void read(PacketByteBuf buf) throws IOException {
		this.name = buf.readString(16);
		this.mode = buf.readByte();
		if (this.mode == 0 || this.mode == 2) {
			this.displayName = buf.readString(32);
			this.type = ScoreboardCriterion.RenderType.getByName(buf.readString(16));
		}
	}

	@Override
	public void write(PacketByteBuf buf) throws IOException {
		buf.writeString(this.name);
		buf.writeByte(this.mode);
		if (this.mode == 0 || this.mode == 2) {
			buf.writeString(this.displayName);
			buf.writeString(this.type.getName());
		}
	}

	public void apply(ClientPlayPacketListener clientPlayPacketListener) {
		clientPlayPacketListener.onScoreboardObjectiveUpdate(this);
	}

	public String getName() {
		return this.name;
	}

	public String getDisplayName() {
		return this.displayName;
	}

	public int getMode() {
		return this.mode;
	}

	public ScoreboardCriterion.RenderType getType() {
		return this.type;
	}
}
