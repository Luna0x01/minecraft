package net.minecraft.network.packet.s2c.play;

import java.io.IOException;
import java.util.UUID;
import net.minecraft.entity.LivingEntity;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.registry.Registry;

public class MobSpawnS2CPacket implements Packet<ClientPlayPacketListener> {
	private int id;
	private UUID uuid;
	private int entityTypeId;
	private double x;
	private double y;
	private double z;
	private int velocityX;
	private int velocityY;
	private int velocityZ;
	private byte yaw;
	private byte pitch;
	private byte headYaw;

	public MobSpawnS2CPacket() {
	}

	public MobSpawnS2CPacket(LivingEntity entity) {
		this.id = entity.getEntityId();
		this.uuid = entity.getUuid();
		this.entityTypeId = Registry.ENTITY_TYPE.getRawId(entity.getType());
		this.x = entity.getX();
		this.y = entity.getY();
		this.z = entity.getZ();
		this.yaw = (byte)((int)(entity.yaw * 256.0F / 360.0F));
		this.pitch = (byte)((int)(entity.pitch * 256.0F / 360.0F));
		this.headYaw = (byte)((int)(entity.headYaw * 256.0F / 360.0F));
		double d = 3.9;
		Vec3d vec3d = entity.getVelocity();
		double e = MathHelper.clamp(vec3d.x, -3.9, 3.9);
		double f = MathHelper.clamp(vec3d.y, -3.9, 3.9);
		double g = MathHelper.clamp(vec3d.z, -3.9, 3.9);
		this.velocityX = (int)(e * 8000.0);
		this.velocityY = (int)(f * 8000.0);
		this.velocityZ = (int)(g * 8000.0);
	}

	@Override
	public void read(PacketByteBuf buf) throws IOException {
		this.id = buf.readVarInt();
		this.uuid = buf.readUuid();
		this.entityTypeId = buf.readVarInt();
		this.x = buf.readDouble();
		this.y = buf.readDouble();
		this.z = buf.readDouble();
		this.yaw = buf.readByte();
		this.pitch = buf.readByte();
		this.headYaw = buf.readByte();
		this.velocityX = buf.readShort();
		this.velocityY = buf.readShort();
		this.velocityZ = buf.readShort();
	}

	@Override
	public void write(PacketByteBuf buf) throws IOException {
		buf.writeVarInt(this.id);
		buf.writeUuid(this.uuid);
		buf.writeVarInt(this.entityTypeId);
		buf.writeDouble(this.x);
		buf.writeDouble(this.y);
		buf.writeDouble(this.z);
		buf.writeByte(this.yaw);
		buf.writeByte(this.pitch);
		buf.writeByte(this.headYaw);
		buf.writeShort(this.velocityX);
		buf.writeShort(this.velocityY);
		buf.writeShort(this.velocityZ);
	}

	public void apply(ClientPlayPacketListener clientPlayPacketListener) {
		clientPlayPacketListener.onMobSpawn(this);
	}

	public int getId() {
		return this.id;
	}

	public UUID getUuid() {
		return this.uuid;
	}

	public int getEntityTypeId() {
		return this.entityTypeId;
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

	public byte getYaw() {
		return this.yaw;
	}

	public byte getPitch() {
		return this.pitch;
	}

	public byte getHeadYaw() {
		return this.headYaw;
	}
}
