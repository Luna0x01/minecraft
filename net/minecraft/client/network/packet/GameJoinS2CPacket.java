package net.minecraft.client.network.packet;

import java.io.IOException;
import net.minecraft.network.Packet;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.util.PacketByteBuf;
import net.minecraft.world.GameMode;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.level.LevelGeneratorType;

public class GameJoinS2CPacket implements Packet<ClientPlayPacketListener> {
	private int playerEntityId;
	private long seed;
	private boolean hardcore;
	private GameMode gameMode;
	private DimensionType dimension;
	private int maxPlayers;
	private LevelGeneratorType generatorType;
	private int chunkLoadDistance;
	private boolean reducedDebugInfo;
	private boolean showsDeathScreen;

	public GameJoinS2CPacket() {
	}

	public GameJoinS2CPacket(
		int i, GameMode gameMode, long l, boolean bl, DimensionType dimensionType, int j, LevelGeneratorType levelGeneratorType, int k, boolean bl2, boolean bl3
	) {
		this.playerEntityId = i;
		this.dimension = dimensionType;
		this.seed = l;
		this.gameMode = gameMode;
		this.maxPlayers = j;
		this.hardcore = bl;
		this.generatorType = levelGeneratorType;
		this.chunkLoadDistance = k;
		this.reducedDebugInfo = bl2;
		this.showsDeathScreen = bl3;
	}

	@Override
	public void read(PacketByteBuf packetByteBuf) throws IOException {
		this.playerEntityId = packetByteBuf.readInt();
		int i = packetByteBuf.readUnsignedByte();
		this.hardcore = (i & 8) == 8;
		i &= -9;
		this.gameMode = GameMode.byId(i);
		this.dimension = DimensionType.byRawId(packetByteBuf.readInt());
		this.seed = packetByteBuf.readLong();
		this.maxPlayers = packetByteBuf.readUnsignedByte();
		this.generatorType = LevelGeneratorType.getTypeFromName(packetByteBuf.readString(16));
		if (this.generatorType == null) {
			this.generatorType = LevelGeneratorType.DEFAULT;
		}

		this.chunkLoadDistance = packetByteBuf.readVarInt();
		this.reducedDebugInfo = packetByteBuf.readBoolean();
		this.showsDeathScreen = packetByteBuf.readBoolean();
	}

	@Override
	public void write(PacketByteBuf packetByteBuf) throws IOException {
		packetByteBuf.writeInt(this.playerEntityId);
		int i = this.gameMode.getId();
		if (this.hardcore) {
			i |= 8;
		}

		packetByteBuf.writeByte(i);
		packetByteBuf.writeInt(this.dimension.getRawId());
		packetByteBuf.writeLong(this.seed);
		packetByteBuf.writeByte(this.maxPlayers);
		packetByteBuf.writeString(this.generatorType.getName());
		packetByteBuf.writeVarInt(this.chunkLoadDistance);
		packetByteBuf.writeBoolean(this.reducedDebugInfo);
		packetByteBuf.writeBoolean(this.showsDeathScreen);
	}

	public void apply(ClientPlayPacketListener clientPlayPacketListener) {
		clientPlayPacketListener.onGameJoin(this);
	}

	public int getEntityId() {
		return this.playerEntityId;
	}

	public long getSeed() {
		return this.seed;
	}

	public boolean isHardcore() {
		return this.hardcore;
	}

	public GameMode getGameMode() {
		return this.gameMode;
	}

	public DimensionType getDimension() {
		return this.dimension;
	}

	public LevelGeneratorType getGeneratorType() {
		return this.generatorType;
	}

	public int getChunkLoadDistance() {
		return this.chunkLoadDistance;
	}

	public boolean hasReducedDebugInfo() {
		return this.reducedDebugInfo;
	}

	public boolean showsDeathScreen() {
		return this.showsDeathScreen;
	}
}
