package net.minecraft.network.packet.s2c.play;

import java.io.IOException;
import java.util.UUID;
import net.minecraft.entity.decoration.painting.PaintingEntity;
import net.minecraft.entity.decoration.painting.PaintingMotive;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.registry.Registry;

public class PaintingSpawnS2CPacket implements Packet<ClientPlayPacketListener> {
	private int id;
	private UUID uuid;
	private BlockPos pos;
	private Direction facing;
	private int motiveId;

	public PaintingSpawnS2CPacket() {
	}

	public PaintingSpawnS2CPacket(PaintingEntity entity) {
		this.id = entity.getEntityId();
		this.uuid = entity.getUuid();
		this.pos = entity.getDecorationBlockPos();
		this.facing = entity.getHorizontalFacing();
		this.motiveId = Registry.PAINTING_MOTIVE.getRawId(entity.motive);
	}

	@Override
	public void read(PacketByteBuf buf) throws IOException {
		this.id = buf.readVarInt();
		this.uuid = buf.readUuid();
		this.motiveId = buf.readVarInt();
		this.pos = buf.readBlockPos();
		this.facing = Direction.fromHorizontal(buf.readUnsignedByte());
	}

	@Override
	public void write(PacketByteBuf buf) throws IOException {
		buf.writeVarInt(this.id);
		buf.writeUuid(this.uuid);
		buf.writeVarInt(this.motiveId);
		buf.writeBlockPos(this.pos);
		buf.writeByte(this.facing.getHorizontal());
	}

	public void apply(ClientPlayPacketListener clientPlayPacketListener) {
		clientPlayPacketListener.onPaintingSpawn(this);
	}

	public int getId() {
		return this.id;
	}

	public UUID getPaintingUuid() {
		return this.uuid;
	}

	public BlockPos getPos() {
		return this.pos;
	}

	public Direction getFacing() {
		return this.facing;
	}

	public PaintingMotive getMotive() {
		return Registry.PAINTING_MOTIVE.get(this.motiveId);
	}
}
