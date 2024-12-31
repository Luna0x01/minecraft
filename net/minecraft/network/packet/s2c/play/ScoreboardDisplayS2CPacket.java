package net.minecraft.network.packet.s2c.play;

import java.io.IOException;
import java.util.Objects;
import javax.annotation.Nullable;
import net.minecraft.network.Packet;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.scoreboard.ScoreboardObjective;
import net.minecraft.util.PacketByteBuf;

public class ScoreboardDisplayS2CPacket implements Packet<ClientPlayPacketListener> {
	private int slot;
	private String name;

	public ScoreboardDisplayS2CPacket() {
	}

	public ScoreboardDisplayS2CPacket(int i, @Nullable ScoreboardObjective scoreboardObjective) {
		this.slot = i;
		if (scoreboardObjective == null) {
			this.name = "";
		} else {
			this.name = scoreboardObjective.getName();
		}
	}

	@Override
	public void read(PacketByteBuf buf) throws IOException {
		this.slot = buf.readByte();
		this.name = buf.readString(16);
	}

	@Override
	public void write(PacketByteBuf buf) throws IOException {
		buf.writeByte(this.slot);
		buf.writeString(this.name);
	}

	public void apply(ClientPlayPacketListener clientPlayPacketListener) {
		clientPlayPacketListener.onScoreboardDisplay(this);
	}

	public int getSlot() {
		return this.slot;
	}

	@Nullable
	public String getName() {
		return Objects.equals(this.name, "") ? null : this.name;
	}
}
