package net.minecraft.client.network.packet;

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
	public void read(PacketByteBuf packetByteBuf) throws IOException {
		this.id = packetByteBuf.readVarInt();
		this.x = packetByteBuf.readDouble();
		this.y = packetByteBuf.readDouble();
		this.z = packetByteBuf.readDouble();
		this.yaw = packetByteBuf.readByte();
		this.pitch = packetByteBuf.readByte();
		this.onGround = packetByteBuf.readBoolean();
	}

	@Override
	public void write(PacketByteBuf packetByteBuf) throws IOException {
		packetByteBuf.writeVarInt(this.id);
		packetByteBuf.writeDouble(this.x);
		packetByteBuf.writeDouble(this.y);
		packetByteBuf.writeDouble(this.z);
		packetByteBuf.writeByte(this.yaw);
		packetByteBuf.writeByte(this.pitch);
		packetByteBuf.writeBoolean(this.onGround);
	}

	public void method_11922(ClientPlayPacketListener clientPlayPacketListener) {
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
