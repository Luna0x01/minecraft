package net.minecraft.network.packet.s2c.play;

import java.io.IOException;
import net.minecraft.network.Packet;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.util.PacketByteBuf;
import net.minecraft.world.Difficulty;
import net.minecraft.world.GameMode;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.level.LevelGeneratorType;

public class PlayerRespawnS2CPacket implements Packet<ClientPlayPacketListener> {
	private DimensionType field_21557;
	private Difficulty difficulty;
	private GameMode field_8706;
	private LevelGeneratorType generatorType;

	public PlayerRespawnS2CPacket() {
	}

	public PlayerRespawnS2CPacket(DimensionType dimensionType, Difficulty difficulty, LevelGeneratorType levelGeneratorType, GameMode gameMode) {
		this.field_21557 = dimensionType;
		this.difficulty = difficulty;
		this.field_8706 = gameMode;
		this.generatorType = levelGeneratorType;
	}

	public void apply(ClientPlayPacketListener clientPlayPacketListener) {
		clientPlayPacketListener.onPlayerRespawn(this);
	}

	@Override
	public void read(PacketByteBuf buf) throws IOException {
		this.field_21557 = DimensionType.method_17195(buf.readInt());
		this.difficulty = Difficulty.byOrdinal(buf.readUnsignedByte());
		this.field_8706 = GameMode.setGameModeWithId(buf.readUnsignedByte());
		this.generatorType = LevelGeneratorType.getTypeFromName(buf.readString(16));
		if (this.generatorType == null) {
			this.generatorType = LevelGeneratorType.DEFAULT;
		}
	}

	@Override
	public void write(PacketByteBuf buf) throws IOException {
		buf.writeInt(this.field_21557.method_17201());
		buf.writeByte(this.difficulty.getId());
		buf.writeByte(this.field_8706.getGameModeId());
		buf.writeString(this.generatorType.getName());
	}

	public DimensionType method_7846() {
		return this.field_21557;
	}

	public Difficulty getDifficulty() {
		return this.difficulty;
	}

	public GameMode method_7848() {
		return this.field_8706;
	}

	public LevelGeneratorType getGeneratorType() {
		return this.generatorType;
	}
}
