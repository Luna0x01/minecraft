package net.minecraft.network.packet.s2c.play;

import java.io.IOException;
import java.util.List;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.network.Packet;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.util.PacketByteBuf;
import net.minecraft.util.math.MathHelper;

public class MobSpawnS2CPacket implements Packet<ClientPlayPacketListener> {
	private int id;
	private int entityTypeId;
	private int x;
	private int y;
	private int z;
	private int velocityX;
	private int velocityY;
	private int velocityZ;
	private byte yaw;
	private byte pitch;
	private byte headYaw;
	private DataTracker tracker;
	private List<DataTracker.DataEntry> entries;

	public MobSpawnS2CPacket() {
	}

	public MobSpawnS2CPacket(LivingEntity livingEntity) {
		this.id = livingEntity.getEntityId();
		this.entityTypeId = (byte)EntityType.getIdByEntity(livingEntity);
		this.x = MathHelper.floor(livingEntity.x * 32.0);
		this.y = MathHelper.floor(livingEntity.y * 32.0);
		this.z = MathHelper.floor(livingEntity.z * 32.0);
		this.yaw = (byte)((int)(livingEntity.yaw * 256.0F / 360.0F));
		this.pitch = (byte)((int)(livingEntity.pitch * 256.0F / 360.0F));
		this.headYaw = (byte)((int)(livingEntity.headYaw * 256.0F / 360.0F));
		double d = 3.9;
		double e = livingEntity.velocityX;
		double f = livingEntity.velocityY;
		double g = livingEntity.velocityZ;
		if (e < -d) {
			e = -d;
		}

		if (f < -d) {
			f = -d;
		}

		if (g < -d) {
			g = -d;
		}

		if (e > d) {
			e = d;
		}

		if (f > d) {
			f = d;
		}

		if (g > d) {
			g = d;
		}

		this.velocityX = (int)(e * 8000.0);
		this.velocityY = (int)(f * 8000.0);
		this.velocityZ = (int)(g * 8000.0);
		this.tracker = livingEntity.getDataTracker();
	}

	@Override
	public void read(PacketByteBuf buf) throws IOException {
		this.id = buf.readVarInt();
		this.entityTypeId = buf.readByte() & 255;
		this.x = buf.readInt();
		this.y = buf.readInt();
		this.z = buf.readInt();
		this.yaw = buf.readByte();
		this.pitch = buf.readByte();
		this.headYaw = buf.readByte();
		this.velocityX = buf.readShort();
		this.velocityY = buf.readShort();
		this.velocityZ = buf.readShort();
		this.entries = DataTracker.deserializePacket(buf);
	}

	@Override
	public void write(PacketByteBuf buf) throws IOException {
		buf.writeVarInt(this.id);
		buf.writeByte(this.entityTypeId & 0xFF);
		buf.writeInt(this.x);
		buf.writeInt(this.y);
		buf.writeInt(this.z);
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

	public List<DataTracker.DataEntry> getEntries() {
		if (this.entries == null) {
			this.entries = this.tracker.getEntries();
		}

		return this.entries;
	}

	public int getId() {
		return this.id;
	}

	public int getEntityTypeId() {
		return this.entityTypeId;
	}

	public int getX() {
		return this.x;
	}

	public int getY() {
		return this.y;
	}

	public int getZ() {
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
