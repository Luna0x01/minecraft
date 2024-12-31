package net.minecraft.client.network.packet;

import java.io.IOException;
import java.util.UUID;
import net.minecraft.entity.LivingEntity;
import net.minecraft.network.Packet;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.util.PacketByteBuf;
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

	public MobSpawnS2CPacket(LivingEntity livingEntity) {
		this.id = livingEntity.getEntityId();
		this.uuid = livingEntity.getUuid();
		this.entityTypeId = Registry.field_11145.getRawId(livingEntity.getType());
		this.x = livingEntity.getX();
		this.y = livingEntity.getY();
		this.z = livingEntity.getZ();
		this.yaw = (byte)((int)(livingEntity.yaw * 256.0F / 360.0F));
		this.pitch = (byte)((int)(livingEntity.pitch * 256.0F / 360.0F));
		this.headYaw = (byte)((int)(livingEntity.headYaw * 256.0F / 360.0F));
		double d = 3.9;
		Vec3d vec3d = livingEntity.getVelocity();
		double e = MathHelper.clamp(vec3d.x, -3.9, 3.9);
		double f = MathHelper.clamp(vec3d.y, -3.9, 3.9);
		double g = MathHelper.clamp(vec3d.z, -3.9, 3.9);
		this.velocityX = (int)(e * 8000.0);
		this.velocityY = (int)(f * 8000.0);
		this.velocityZ = (int)(g * 8000.0);
	}

	@Override
	public void read(PacketByteBuf packetByteBuf) throws IOException {
		this.id = packetByteBuf.readVarInt();
		this.uuid = packetByteBuf.readUuid();
		this.entityTypeId = packetByteBuf.readVarInt();
		this.x = packetByteBuf.readDouble();
		this.y = packetByteBuf.readDouble();
		this.z = packetByteBuf.readDouble();
		this.yaw = packetByteBuf.readByte();
		this.pitch = packetByteBuf.readByte();
		this.headYaw = packetByteBuf.readByte();
		this.velocityX = packetByteBuf.readShort();
		this.velocityY = packetByteBuf.readShort();
		this.velocityZ = packetByteBuf.readShort();
	}

	@Override
	public void write(PacketByteBuf packetByteBuf) throws IOException {
		packetByteBuf.writeVarInt(this.id);
		packetByteBuf.writeUuid(this.uuid);
		packetByteBuf.writeVarInt(this.entityTypeId);
		packetByteBuf.writeDouble(this.x);
		packetByteBuf.writeDouble(this.y);
		packetByteBuf.writeDouble(this.z);
		packetByteBuf.writeByte(this.yaw);
		packetByteBuf.writeByte(this.pitch);
		packetByteBuf.writeByte(this.headYaw);
		packetByteBuf.writeShort(this.velocityX);
		packetByteBuf.writeShort(this.velocityY);
		packetByteBuf.writeShort(this.velocityZ);
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
