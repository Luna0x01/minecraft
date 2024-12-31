package net.minecraft.network.packet.s2c.play;

import java.io.IOException;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LightningBoltEntity;
import net.minecraft.network.Packet;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.util.PacketByteBuf;
import net.minecraft.util.math.MathHelper;

public class EntitySpawnGlobalS2CPacket implements Packet<ClientPlayPacketListener> {
	private int id;
	private int x;
	private int y;
	private int z;
	private int entityTypeId;

	public EntitySpawnGlobalS2CPacket() {
	}

	public EntitySpawnGlobalS2CPacket(Entity entity) {
		this.id = entity.getEntityId();
		this.x = MathHelper.floor(entity.x * 32.0);
		this.y = MathHelper.floor(entity.y * 32.0);
		this.z = MathHelper.floor(entity.z * 32.0);
		if (entity instanceof LightningBoltEntity) {
			this.entityTypeId = 1;
		}
	}

	@Override
	public void read(PacketByteBuf buf) throws IOException {
		this.id = buf.readVarInt();
		this.entityTypeId = buf.readByte();
		this.x = buf.readInt();
		this.y = buf.readInt();
		this.z = buf.readInt();
	}

	@Override
	public void write(PacketByteBuf buf) throws IOException {
		buf.writeVarInt(this.id);
		buf.writeByte(this.entityTypeId);
		buf.writeInt(this.x);
		buf.writeInt(this.y);
		buf.writeInt(this.z);
	}

	public void apply(ClientPlayPacketListener clientPlayPacketListener) {
		clientPlayPacketListener.onEntitySpawnGlobal(this);
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

	public int getEntityTypeId() {
		return this.entityTypeId;
	}
}
