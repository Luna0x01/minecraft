package net.minecraft.network.packet.s2c.play;

import java.io.IOException;
import net.minecraft.entity.Entity;
import net.minecraft.network.Packet;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.util.PacketByteBuf;
import net.minecraft.util.math.MathHelper;

public class EntitySpawnS2CPacket implements Packet<ClientPlayPacketListener> {
	private int id;
	private int x;
	private int y;
	private int z;
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
		this.x = MathHelper.floor(entity.x * 32.0);
		this.y = MathHelper.floor(entity.y * 32.0);
		this.z = MathHelper.floor(entity.z * 32.0);
		this.pitch = MathHelper.floor(entity.pitch * 256.0F / 360.0F);
		this.yaw = MathHelper.floor(entity.yaw * 256.0F / 360.0F);
		this.type = i;
		this.dataId = j;
		if (j > 0) {
			double d = entity.velocityX;
			double e = entity.velocityY;
			double f = entity.velocityZ;
			double g = 3.9;
			if (d < -g) {
				d = -g;
			}

			if (e < -g) {
				e = -g;
			}

			if (f < -g) {
				f = -g;
			}

			if (d > g) {
				d = g;
			}

			if (e > g) {
				e = g;
			}

			if (f > g) {
				f = g;
			}

			this.velocityX = (int)(d * 8000.0);
			this.velocityY = (int)(e * 8000.0);
			this.velocityZ = (int)(f * 8000.0);
		}
	}

	@Override
	public void read(PacketByteBuf buf) throws IOException {
		this.id = buf.readVarInt();
		this.type = buf.readByte();
		this.x = buf.readInt();
		this.y = buf.readInt();
		this.z = buf.readInt();
		this.pitch = buf.readByte();
		this.yaw = buf.readByte();
		this.dataId = buf.readInt();
		if (this.dataId > 0) {
			this.velocityX = buf.readShort();
			this.velocityY = buf.readShort();
			this.velocityZ = buf.readShort();
		}
	}

	@Override
	public void write(PacketByteBuf buf) throws IOException {
		buf.writeVarInt(this.id);
		buf.writeByte(this.type);
		buf.writeInt(this.x);
		buf.writeInt(this.y);
		buf.writeInt(this.z);
		buf.writeByte(this.pitch);
		buf.writeByte(this.yaw);
		buf.writeInt(this.dataId);
		if (this.dataId > 0) {
			buf.writeShort(this.velocityX);
			buf.writeShort(this.velocityY);
			buf.writeShort(this.velocityZ);
		}
	}

	public void apply(ClientPlayPacketListener clientPlayPacketListener) {
		clientPlayPacketListener.onEntitySpawn(this);
	}

	public int getId() {
		return this.id;
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

	public void setX(int x) {
		this.x = x;
	}

	public void setY(int y) {
		this.y = y;
	}

	public void setZ(int z) {
		this.z = z;
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
