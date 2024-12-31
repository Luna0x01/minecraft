package net.minecraft.network.packet.s2c.play;

import java.io.IOException;
import java.util.UUID;
import net.minecraft.entity.decoration.painting.PaintingEntity;
import net.minecraft.network.Packet;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.util.PacketByteBuf;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;

public class PaintingSpawnS2CPacket implements Packet<ClientPlayPacketListener> {
	private int id;
	private UUID field_13739;
	private BlockPos pos;
	private Direction direction;
	private String name;

	public PaintingSpawnS2CPacket() {
	}

	public PaintingSpawnS2CPacket(PaintingEntity paintingEntity) {
		this.id = paintingEntity.getEntityId();
		this.field_13739 = paintingEntity.getUuid();
		this.pos = paintingEntity.getTilePos();
		this.direction = paintingEntity.direction;
		this.name = paintingEntity.type.name;
	}

	@Override
	public void read(PacketByteBuf buf) throws IOException {
		this.id = buf.readVarInt();
		this.field_13739 = buf.readUuid();
		this.name = buf.readString(PaintingEntity.PaintingMotive.LENGTH);
		this.pos = buf.readBlockPos();
		this.direction = Direction.fromHorizontal(buf.readUnsignedByte());
	}

	@Override
	public void write(PacketByteBuf buf) throws IOException {
		buf.writeVarInt(this.id);
		buf.writeUuid(this.field_13739);
		buf.writeString(this.name);
		buf.writeBlockPos(this.pos);
		buf.writeByte(this.direction.getHorizontal());
	}

	public void apply(ClientPlayPacketListener clientPlayPacketListener) {
		clientPlayPacketListener.onPaintingSpawn(this);
	}

	public int getId() {
		return this.id;
	}

	public UUID method_12627() {
		return this.field_13739;
	}

	public BlockPos getPos() {
		return this.pos;
	}

	public Direction getFacing() {
		return this.direction;
	}

	public String getName() {
		return this.name;
	}
}
