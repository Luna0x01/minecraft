package net.minecraft.network.packet.s2c.play;

import java.io.IOException;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.Packet;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.util.PacketByteBuf;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class BedSleepS2CPacket implements Packet<ClientPlayPacketListener> {
	private int entityId;
	private BlockPos bedPos;

	public BedSleepS2CPacket() {
	}

	public BedSleepS2CPacket(PlayerEntity playerEntity, BlockPos blockPos) {
		this.entityId = playerEntity.getEntityId();
		this.bedPos = blockPos;
	}

	@Override
	public void read(PacketByteBuf buf) throws IOException {
		this.entityId = buf.readVarInt();
		this.bedPos = buf.readBlockPos();
	}

	@Override
	public void write(PacketByteBuf buf) throws IOException {
		buf.writeVarInt(this.entityId);
		buf.writeBlockPos(this.bedPos);
	}

	public void apply(ClientPlayPacketListener clientPlayPacketListener) {
		clientPlayPacketListener.onBedSleep(this);
	}

	public PlayerEntity getPlayer(World world) {
		return (PlayerEntity)world.getEntityById(this.entityId);
	}

	public BlockPos getBedPos() {
		return this.bedPos;
	}
}
