package net.minecraft.network.packet.s2c.play;

import java.io.IOException;
import net.minecraft.network.Packet;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.scoreboard.GenericScoreboardCriteria;
import net.minecraft.scoreboard.ScoreboardObjective;
import net.minecraft.text.Text;
import net.minecraft.util.PacketByteBuf;

public class ScoreboardObjectiveUpdateS2CPacket implements Packet<ClientPlayPacketListener> {
	private String name;
	private Text field_21565;
	private GenericScoreboardCriteria.class_4104 field_11670;
	private int mode;

	public ScoreboardObjectiveUpdateS2CPacket() {
	}

	public ScoreboardObjectiveUpdateS2CPacket(ScoreboardObjective scoreboardObjective, int i) {
		this.name = scoreboardObjective.getName();
		this.field_21565 = scoreboardObjective.method_4849();
		this.field_11670 = scoreboardObjective.method_9351();
		this.mode = i;
	}

	@Override
	public void read(PacketByteBuf buf) throws IOException {
		this.name = buf.readString(16);
		this.mode = buf.readByte();
		if (this.mode == 0 || this.mode == 2) {
			this.field_21565 = buf.readText();
			this.field_11670 = buf.readEnumConstant(GenericScoreboardCriteria.class_4104.class);
		}
	}

	@Override
	public void write(PacketByteBuf buf) throws IOException {
		buf.writeString(this.name);
		buf.writeByte(this.mode);
		if (this.mode == 0 || this.mode == 2) {
			buf.writeText(this.field_21565);
			buf.writeEnumConstant(this.field_11670);
		}
	}

	public void apply(ClientPlayPacketListener clientPlayPacketListener) {
		clientPlayPacketListener.onScoreboardObjectiveUpdate(this);
	}

	public String getName() {
		return this.name;
	}

	public Text method_7883() {
		return this.field_21565;
	}

	public int getMode() {
		return this.mode;
	}

	public GenericScoreboardCriteria.class_4104 method_10682() {
		return this.field_11670;
	}
}
