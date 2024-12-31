package net.minecraft.network.packet.s2c.play;

import java.io.IOException;
import net.minecraft.network.Packet;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.util.PacketByteBuf;
import net.minecraft.world.Difficulty;

public class DifficultyS2CPacket implements Packet<ClientPlayPacketListener> {
	private Difficulty difficulty;
	private boolean locked;

	public DifficultyS2CPacket() {
	}

	public DifficultyS2CPacket(Difficulty difficulty, boolean bl) {
		this.difficulty = difficulty;
		this.locked = bl;
	}

	public void apply(ClientPlayPacketListener clientPlayPacketListener) {
		clientPlayPacketListener.onDifficulty(this);
	}

	@Override
	public void read(PacketByteBuf buf) throws IOException {
		this.difficulty = Difficulty.byOrdinal(buf.readUnsignedByte());
	}

	@Override
	public void write(PacketByteBuf buf) throws IOException {
		buf.writeByte(this.difficulty.getId());
	}

	public boolean isDifficultyLocked() {
		return this.locked;
	}

	public Difficulty getDifficulty() {
		return this.difficulty;
	}
}
