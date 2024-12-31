package net.minecraft.network.packet.s2c.play;

import java.io.IOException;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LightningBoltEntity;
import net.minecraft.network.Packet;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.util.PacketByteBuf;

public class EntitySpawnGlobalS2CPacket implements Packet<ClientPlayPacketListener> {
	private int id;
	private double x;
	private double y;
	private double z;
	private int entityTypeId;

	public EntitySpawnGlobalS2CPacket() {
	}

	public EntitySpawnGlobalS2CPacket(Entity entity) {
		this.id = entity.getEntityId();
		this.x = entity.x;
		this.y = entity.y;
		this.z = entity.z;
		if (entity instanceof LightningBoltEntity) {
			this.entityTypeId = 1;
		}
	}

	@Override
	public void read(PacketByteBuf buf) throws IOException {
		this.id = buf.readVarInt();
		this.entityTypeId = buf.readByte();
		this.x = buf.readDouble();
		this.y = buf.readDouble();
		this.z = buf.readDouble();
	}

	@Override
	public void write(PacketByteBuf buf) throws IOException {
		buf.writeVarInt(this.id);
		buf.writeByte(this.entityTypeId);
		buf.writeDouble(this.x);
		buf.writeDouble(this.y);
		buf.writeDouble(this.z);
	}

	public void apply(ClientPlayPacketListener clientPlayPacketListener) {
		clientPlayPacketListener.onEntitySpawnGlobal(this);
	}

	public int getId() {
		return this.id;
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

	public int getEntityTypeId() {
		return this.entityTypeId;
	}
}
