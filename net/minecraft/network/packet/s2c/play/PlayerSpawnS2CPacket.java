package net.minecraft.network.packet.s2c.play;

import java.io.IOException;
import java.util.UUID;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.listener.ClientPlayPacketListener;

public class PlayerSpawnS2CPacket implements Packet<ClientPlayPacketListener> {
	private int id;
	private UUID uuid;
	private double x;
	private double y;
	private double z;
	private byte yaw;
	private byte pitch;

	public PlayerSpawnS2CPacket() {
	}

	public PlayerSpawnS2CPacket(PlayerEntity player) {
		this.id = player.getEntityId();
		this.uuid = player.getGameProfile().getId();
		this.x = player.getX();
		this.y = player.getY();
		this.z = player.getZ();
		this.yaw = (byte)((int)(player.yaw * 256.0F / 360.0F));
		this.pitch = (byte)((int)(player.pitch * 256.0F / 360.0F));
	}

	@Override
	public void read(PacketByteBuf buf) throws IOException {
		this.id = buf.readVarInt();
		this.uuid = buf.readUuid();
		this.x = buf.readDouble();
		this.y = buf.readDouble();
		this.z = buf.readDouble();
		this.yaw = buf.readByte();
		this.pitch = buf.readByte();
	}

	@Override
	public void write(PacketByteBuf buf) throws IOException {
		buf.writeVarInt(this.id);
		buf.writeUuid(this.uuid);
		buf.writeDouble(this.x);
		buf.writeDouble(this.y);
		buf.writeDouble(this.z);
		buf.writeByte(this.yaw);
		buf.writeByte(this.pitch);
	}

	public void apply(ClientPlayPacketListener clientPlayPacketListener) {
		clientPlayPacketListener.onPlayerSpawn(this);
	}

	public int getId() {
		return this.id;
	}

	public UUID getPlayerUuid() {
		return this.uuid;
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
}
