package net.minecraft.network.packet.s2c.play;

import java.io.IOException;
import net.minecraft.network.Packet;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.util.PacketByteBuf;
import net.minecraft.world.Difficulty;
import net.minecraft.world.level.LevelGeneratorType;
import net.minecraft.world.level.LevelInfo;

public class GameJoinS2CPacket implements Packet<ClientPlayPacketListener> {
	private int playerEntityId;
	private boolean hardcore;
	private LevelInfo.GameMode gameMode;
	private int viewDistance;
	private Difficulty difficulty;
	private int maxPlayers;
	private LevelGeneratorType levelGeneratorType;
	private boolean reducedDebugInfo;

	public GameJoinS2CPacket() {
	}

	public GameJoinS2CPacket(
		int i, LevelInfo.GameMode gameMode, boolean bl, int j, Difficulty difficulty, int k, LevelGeneratorType levelGeneratorType, boolean bl2
	) {
		this.playerEntityId = i;
		this.viewDistance = j;
		this.difficulty = difficulty;
		this.gameMode = gameMode;
		this.maxPlayers = k;
		this.hardcore = bl;
		this.levelGeneratorType = levelGeneratorType;
		this.reducedDebugInfo = bl2;
	}

	@Override
	public void read(PacketByteBuf buf) throws IOException {
		this.playerEntityId = buf.readInt();
		int i = buf.readUnsignedByte();
		this.hardcore = (i & 8) == 8;
		i &= -9;
		this.gameMode = LevelInfo.GameMode.byId(i);
		this.viewDistance = buf.readByte();
		this.difficulty = Difficulty.byOrdinal(buf.readUnsignedByte());
		this.maxPlayers = buf.readUnsignedByte();
		this.levelGeneratorType = LevelGeneratorType.getTypeFromName(buf.readString(16));
		if (this.levelGeneratorType == null) {
			this.levelGeneratorType = LevelGeneratorType.DEFAULT;
		}

		this.reducedDebugInfo = buf.readBoolean();
	}

	@Override
	public void write(PacketByteBuf buf) throws IOException {
		buf.writeInt(this.playerEntityId);
		int i = this.gameMode.getId();
		if (this.hardcore) {
			i |= 8;
		}

		buf.writeByte(i);
		buf.writeByte(this.viewDistance);
		buf.writeByte(this.difficulty.getId());
		buf.writeByte(this.maxPlayers);
		buf.writeString(this.levelGeneratorType.getName());
		buf.writeBoolean(this.reducedDebugInfo);
	}

	public void apply(ClientPlayPacketListener clientPlayPacketListener) {
		clientPlayPacketListener.onGameJoin(this);
	}

	public int getEntityId() {
		return this.playerEntityId;
	}

	public boolean isHardcore() {
		return this.hardcore;
	}

	public LevelInfo.GameMode getGameMode() {
		return this.gameMode;
	}

	public int getChunkLoadDistance() {
		return this.viewDistance;
	}

	public Difficulty getDifficulty() {
		return this.difficulty;
	}

	public int getMaxPlayers() {
		return this.maxPlayers;
	}

	public LevelGeneratorType getGeneratorType() {
		return this.levelGeneratorType;
	}

	public boolean hasReducedDebugInfo() {
		return this.reducedDebugInfo;
	}
}
