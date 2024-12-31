package net.minecraft.client.network.packet;

import java.io.IOException;
import java.util.List;
import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.data.DataTracker;
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
	private int yaw;
	private int pitch;
	private int headPitch;
	private byte velocityX;
	private byte velocityY;
	private byte velocityZ;
	private DataTracker dataTracker;
	private List<DataTracker.Entry<?>> trackedValues;

	public MobSpawnS2CPacket() {
	}

	public MobSpawnS2CPacket(LivingEntity livingEntity) {
		this.id = livingEntity.getEntityId();
		this.uuid = livingEntity.getUuid();
		this.entityTypeId = Registry.ENTITY_TYPE.getRawId(livingEntity.getType());
		this.x = livingEntity.x;
		this.y = livingEntity.y;
		this.z = livingEntity.z;
		this.velocityX = (byte)((int)(livingEntity.yaw * 256.0F / 360.0F));
		this.velocityY = (byte)((int)(livingEntity.pitch * 256.0F / 360.0F));
		this.velocityZ = (byte)((int)(livingEntity.headYaw * 256.0F / 360.0F));
		double d = 3.9;
		Vec3d vec3d = livingEntity.getVelocity();
		double e = MathHelper.clamp(vec3d.x, -3.9, 3.9);
		double f = MathHelper.clamp(vec3d.y, -3.9, 3.9);
		double g = MathHelper.clamp(vec3d.z, -3.9, 3.9);
		this.yaw = (int)(e * 8000.0);
		this.pitch = (int)(f * 8000.0);
		this.headPitch = (int)(g * 8000.0);
		this.dataTracker = livingEntity.getDataTracker();
	}

	@Override
	public void read(PacketByteBuf packetByteBuf) throws IOException {
		this.id = packetByteBuf.readVarInt();
		this.uuid = packetByteBuf.readUuid();
		this.entityTypeId = packetByteBuf.readVarInt();
		this.x = packetByteBuf.readDouble();
		this.y = packetByteBuf.readDouble();
		this.z = packetByteBuf.readDouble();
		this.velocityX = packetByteBuf.readByte();
		this.velocityY = packetByteBuf.readByte();
		this.velocityZ = packetByteBuf.readByte();
		this.yaw = packetByteBuf.readShort();
		this.pitch = packetByteBuf.readShort();
		this.headPitch = packetByteBuf.readShort();
		this.trackedValues = DataTracker.deserializePacket(packetByteBuf);
	}

	@Override
	public void write(PacketByteBuf packetByteBuf) throws IOException {
		packetByteBuf.writeVarInt(this.id);
		packetByteBuf.writeUuid(this.uuid);
		packetByteBuf.writeVarInt(this.entityTypeId);
		packetByteBuf.writeDouble(this.x);
		packetByteBuf.writeDouble(this.y);
		packetByteBuf.writeDouble(this.z);
		packetByteBuf.writeByte(this.velocityX);
		packetByteBuf.writeByte(this.velocityY);
		packetByteBuf.writeByte(this.velocityZ);
		packetByteBuf.writeShort(this.yaw);
		packetByteBuf.writeShort(this.pitch);
		packetByteBuf.writeShort(this.headPitch);
		this.dataTracker.toPacketByteBuf(packetByteBuf);
	}

	public void method_11217(ClientPlayPacketListener clientPlayPacketListener) {
		clientPlayPacketListener.onMobSpawn(this);
	}

	@Nullable
	public List<DataTracker.Entry<?>> getTrackedValues() {
		return this.trackedValues;
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

	public int getYaw() {
		return this.yaw;
	}

	public int getPitch() {
		return this.pitch;
	}

	public int getHeadPitch() {
		return this.headPitch;
	}

	public byte getVelocityX() {
		return this.velocityX;
	}

	public byte getVelocityY() {
		return this.velocityY;
	}

	public byte getVelocityZ() {
		return this.velocityZ;
	}
}
