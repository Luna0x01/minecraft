package net.minecraft.network.packet.s2c.play;

import java.io.IOException;
import net.minecraft.entity.Entity;
import net.minecraft.network.Packet;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.util.PacketByteBuf;

public class EntityPositionS2CPacket implements Packet<ClientPlayPacketListener> {
	private int id;
	private double x;
	private double y;
	private double z;
	private byte yaw;
	private byte pitch;
	private boolean onGround;

	public EntityPositionS2CPacket() {
	}

	public EntityPositionS2CPacket(Entity entity) {
		this.id = entity.getEntityId();
		this.x = entity.x;
		this.y = entity.y;
		this.z = entity.z;
		this.yaw = (byte)((int)(entity.yaw * 256.0F / 360.0F));
		this.pitch = (byte)((int)(entity.pitch * 256.0F / 360.0F));
		this.onGround = entity.onGround;
	}

	@Override
	public void read(PacketByteBuf buf) throws IOException {
		this.id = buf.readVarInt();
		this.x = buf.readDouble();
		this.y = buf.readDouble();
		this.z = buf.readDouble();
		this.yaw = buf.readByte();
		this.pitch = buf.readByte();
		this.onGround = buf.readBoolean();
	}

	@Override
	public void write(PacketByteBuf buf) throws IOException {
		buf.writeVarInt(this.id);
		buf.writeDouble(this.x);
		buf.writeDouble(this.y);
		buf.writeDouble(this.z);
		buf.writeByte(this.yaw);
		buf.writeByte(this.pitch);
		buf.writeBoolean(this.onGround);
	}

	public void apply(ClientPlayPacketListener clientPlayPacketListener) {
		clientPlayPacketListener.onEntityPosition(this);
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

	public byte getYaw() {
		return this.yaw;
	}

	public byte getPitch() {
		return this.pitch;
	}

	public boolean isOnGround() {
		return this.onGround;
	}
}
