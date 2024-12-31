package net.minecraft.network.packet.s2c.play;

import java.io.IOException;
import java.util.List;
import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.network.Packet;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.util.PacketByteBuf;

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
	private DataTracker tracker;
	private List<DataTracker.DataEntry<?>> entries;

	public MobSpawnS2CPacket() {
	}

	public MobSpawnS2CPacket(LivingEntity livingEntity) {
		this.id = livingEntity.getEntityId();
		this.uuid = livingEntity.getUuid();
		this.entityTypeId = EntityType.REGISTRY.getRawId(livingEntity.getClass());
		this.x = livingEntity.x;
		this.y = livingEntity.y;
		this.z = livingEntity.z;
		this.yaw = (byte)((int)(livingEntity.yaw * 256.0F / 360.0F));
		this.pitch = (byte)((int)(livingEntity.pitch * 256.0F / 360.0F));
		this.headYaw = (byte)((int)(livingEntity.headYaw * 256.0F / 360.0F));
		double d = 3.9;
		double e = livingEntity.velocityX;
		double f = livingEntity.velocityY;
		double g = livingEntity.velocityZ;
		if (e < -3.9) {
			e = -3.9;
		}

		if (f < -3.9) {
			f = -3.9;
		}

		if (g < -3.9) {
			g = -3.9;
		}

		if (e > 3.9) {
			e = 3.9;
		}

		if (f > 3.9) {
			f = 3.9;
		}

		if (g > 3.9) {
			g = 3.9;
		}

		this.velocityX = (int)(e * 8000.0);
		this.velocityY = (int)(f * 8000.0);
		this.velocityZ = (int)(g * 8000.0);
		this.tracker = livingEntity.getDataTracker();
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
		this.entries = DataTracker.method_12753(buf);
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
		this.tracker.write(buf);
	}

	public void apply(ClientPlayPacketListener clientPlayPacketListener) {
		clientPlayPacketListener.onMobSpawn(this);
	}

	@Nullable
	public List<DataTracker.DataEntry<?>> getEntries() {
		return this.entries;
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
