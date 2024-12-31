package net.minecraft.network.packet.s2c.play;

import java.io.IOException;
import java.util.UUID;
import net.minecraft.entity.Entity;
import net.minecraft.network.Packet;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.util.PacketByteBuf;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;

public class EntitySpawnS2CPacket implements Packet<ClientPlayPacketListener> {
	private int id;
	private UUID uuid;
	private double x;
	private double y;
	private double z;
	private int velocityX;
	private int velocityY;
	private int velocityZ;
	private int pitch;
	private int yaw;
	private int type;
	private int dataId;

	public EntitySpawnS2CPacket() {
	}

	public EntitySpawnS2CPacket(Entity entity, int i) {
		this(entity, i, 0);
	}

	public EntitySpawnS2CPacket(Entity entity, int i, int j) {
		this.id = entity.getEntityId();
		this.uuid = entity.getUuid();
		this.x = entity.x;
		this.y = entity.y;
		this.z = entity.z;
		this.pitch = MathHelper.floor(entity.pitch * 256.0F / 360.0F);
		this.yaw = MathHelper.floor(entity.yaw * 256.0F / 360.0F);
		this.type = i;
		this.dataId = j;
		double d = 3.9;
		this.velocityX = (int)(MathHelper.clamp(entity.velocityX, -3.9, 3.9) * 8000.0);
		this.velocityY = (int)(MathHelper.clamp(entity.velocityY, -3.9, 3.9) * 8000.0);
		this.velocityZ = (int)(MathHelper.clamp(entity.velocityZ, -3.9, 3.9) * 8000.0);
	}

	public EntitySpawnS2CPacket(Entity entity, int i, int j, BlockPos blockPos) {
		this(entity, i, j);
		this.x = (double)blockPos.getX();
		this.y = (double)blockPos.getY();
		this.z = (double)blockPos.getZ();
	}

	@Override
	public void read(PacketByteBuf buf) throws IOException {
		this.id = buf.readVarInt();
		this.uuid = buf.readUuid();
		this.type = buf.readByte();
		this.x = buf.readDouble();
		this.y = buf.readDouble();
		this.z = buf.readDouble();
		this.pitch = buf.readByte();
		this.yaw = buf.readByte();
		this.dataId = buf.readInt();
		this.velocityX = buf.readShort();
		this.velocityY = buf.readShort();
		this.velocityZ = buf.readShort();
	}

	@Override
	public void write(PacketByteBuf buf) throws IOException {
		buf.writeVarInt(this.id);
		buf.writeUuid(this.uuid);
		buf.writeByte(this.type);
		buf.writeDouble(this.x);
		buf.writeDouble(this.y);
		buf.writeDouble(this.z);
		buf.writeByte(this.pitch);
		buf.writeByte(this.yaw);
		buf.writeInt(this.dataId);
		buf.writeShort(this.velocityX);
		buf.writeShort(this.velocityY);
		buf.writeShort(this.velocityZ);
	}

	public void apply(ClientPlayPacketListener clientPlayPacketListener) {
		clientPlayPacketListener.onEntitySpawn(this);
	}

	public int getId() {
		return this.id;
	}

	public UUID getUuid() {
		return this.uuid;
	}

	public double getX() {
		return this.x;
	}

	public double getY() {
		return this.y;
	}

	public double getZ() {
		return this.z;
	}

	public int getVelocityX() {
		return this.velocityX;
	}

	public int getVelocityY() {
		return this.velocityY;
	}

	public int getVelocityZ() {
		return this.velocityZ;
	}

	public int getPitch() {
		return this.pitch;
	}

	public int getYaw() {
		return this.yaw;
	}

	public int getEntityData() {
		return this.type;
	}

	public int getDataId() {
		return this.dataId;
	}

	public void setVelocityX(int velocityX) {
		this.velocityX = velocityX;
	}

	public void setVelocityY(int velocityY) {
		this.velocityY = velocityY;
	}

	public void setVelocityZ(int velocityZ) {
		this.velocityZ = velocityZ;
	}

	public void setDataId(int dataId) {
		this.dataId = dataId;
	}
}
