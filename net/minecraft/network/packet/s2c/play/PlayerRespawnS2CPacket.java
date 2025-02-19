package net.minecraft.network.packet.s2c.play;

import java.util.function.Supplier;
import javax.annotation.Nullable;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.GameMode;
import net.minecraft.world.World;
import net.minecraft.world.dimension.DimensionType;

public class PlayerRespawnS2CPacket implements Packet<ClientPlayPacketListener> {
	private final DimensionType dimensionType;
	private final RegistryKey<World> dimension;
	private final long sha256Seed;
	private final GameMode gameMode;
	@Nullable
	private final GameMode previousGameMode;
	private final boolean debugWorld;
	private final boolean flatWorld;
	private final boolean keepPlayerAttributes;

	public PlayerRespawnS2CPacket(
		DimensionType dimensionType,
		RegistryKey<World> dimension,
		long sha256Seed,
		GameMode gameMode,
		@Nullable GameMode previousGameMode,
		boolean debugWorld,
		boolean flatWorld,
		boolean keepPlayerAttributes
	) {
		this.dimensionType = dimensionType;
		this.dimension = dimension;
		this.sha256Seed = sha256Seed;
		this.gameMode = gameMode;
		this.previousGameMode = previousGameMode;
		this.debugWorld = debugWorld;
		this.flatWorld = flatWorld;
		this.keepPlayerAttributes = keepPlayerAttributes;
	}

	public PlayerRespawnS2CPacket(PacketByteBuf buf) {
		this.dimensionType = (DimensionType)buf.<Supplier>decode(DimensionType.REGISTRY_CODEC).get();
		this.dimension = RegistryKey.of(Registry.WORLD_KEY, buf.readIdentifier());
		this.sha256Seed = buf.readLong();
		this.gameMode = GameMode.byId(buf.readUnsignedByte());
		this.previousGameMode = GameMode.getOrNull(buf.readByte());
		this.debugWorld = buf.readBoolean();
		this.flatWorld = buf.readBoolean();
		this.keepPlayerAttributes = buf.readBoolean();
	}

	@Override
	public void write(PacketByteBuf buf) {
		buf.encode(DimensionType.REGISTRY_CODEC, () -> this.dimensionType);
		buf.writeIdentifier(this.dimension.getValue());
		buf.writeLong(this.sha256Seed);
		buf.writeByte(this.gameMode.getId());
		buf.writeByte(GameMode.getId(this.previousGameMode));
		buf.writeBoolean(this.debugWorld);
		buf.writeBoolean(this.flatWorld);
		buf.writeBoolean(this.keepPlayerAttributes);
	}

	public void apply(ClientPlayPacketListener clientPlayPacketListener) {
		clientPlayPacketListener.onPlayerRespawn(this);
	}

	public DimensionType getDimensionType() {
		return this.dimensionType;
	}

	public RegistryKey<World> getDimension() {
		return this.dimension;
	}

	public long getSha256Seed() {
		return this.sha256Seed;
	}

	public GameMode getGameMode() {
		return this.gameMode;
	}

	@Nullable
	public GameMode getPreviousGameMode() {
		return this.previousGameMode;
	}

	public boolean isDebugWorld() {
		return this.debugWorld;
	}

	public boolean isFlatWorld() {
		return this.flatWorld;
	}

	public boolean shouldKeepPlayerAttributes() {
		return this.keepPlayerAttributes;
	}
}
