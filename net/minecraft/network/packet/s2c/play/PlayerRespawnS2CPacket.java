package net.minecraft.network.packet.s2c.play;

import java.io.IOException;
import net.minecraft.network.Packet;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.util.PacketByteBuf;
import net.minecraft.world.Difficulty;
import net.minecraft.world.level.LevelGeneratorType;
import net.minecraft.world.level.LevelInfo;

public class PlayerRespawnS2CPacket implements Packet<ClientPlayPacketListener> {
	private int dimensionId;
	private Difficulty difficulty;
	private LevelInfo.GameMode gameMode;
	private LevelGeneratorType generatorType;

	public PlayerRespawnS2CPacket() {
	}

	public PlayerRespawnS2CPacket(int i, Difficulty difficulty, LevelGeneratorType levelGeneratorType, LevelInfo.GameMode gameMode) {
		this.dimensionId = i;
		this.difficulty = difficulty;
		this.gameMode = gameMode;
		this.generatorType = levelGeneratorType;
	}

	public void apply(ClientPlayPacketListener clientPlayPacketListener) {
		clientPlayPacketListener.onPlayerRespawn(this);
	}

	@Override
	public void read(PacketByteBuf buf) throws IOException {
		this.dimensionId = buf.readInt();
		this.difficulty = Difficulty.byOrdinal(buf.readUnsignedByte());
		this.gameMode = LevelInfo.GameMode.byId(buf.readUnsignedByte());
		this.generatorType = LevelGeneratorType.getTypeFromName(buf.readString(16));
		if (this.generatorType == null) {
			this.generatorType = LevelGeneratorType.DEFAULT;
		}
	}

	@Override
	public void write(PacketByteBuf buf) throws IOException {
		buf.writeInt(this.dimensionId);
		buf.writeByte(this.difficulty.getId());
		buf.writeByte(this.gameMode.getId());
		buf.writeString(this.generatorType.getName());
	}

	public int getDimensionId() {
		return this.dimensionId;
	}

	public Difficulty getDifficulty() {
		return this.difficulty;
	}

	public LevelInfo.GameMode getGameMode() {
		return this.gameMode;
	}

	public LevelGeneratorType getGeneratorType() {
		return this.generatorType;
	}
}
