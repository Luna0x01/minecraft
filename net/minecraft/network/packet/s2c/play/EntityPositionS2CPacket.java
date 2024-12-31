package net.minecraft.network.packet.s2c.play;

import java.io.IOException;
import net.minecraft.entity.Entity;
import net.minecraft.network.Packet;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.util.PacketByteBuf;
import net.minecraft.util.math.MathHelper;

public class EntityPositionS2CPacket implements Packet<ClientPlayPacketListener> {
	private int id;
	private int x;
	private int y;
	private int z;
	private byte yaw;
	private byte pitch;
	private boolean onGround;

	public EntityPositionS2CPacket() {
	}

	public EntityPositionS2CPacket(Entity entity) {
		this.id = entity.getEntityId();
		this.x = MathHelper.floor(entity.x * 32.0);
		this.y = MathHelper.floor(entity.y * 32.0);
		this.z = MathHelper.floor(entity.z * 32.0);
		this.yaw = (byte)((int)(entity.yaw * 256.0F / 360.0F));
		this.pitch = (byte)((int)(entity.pitch * 256.0F / 360.0F));
		this.onGround = entity.onGround;
	}

	public EntityPositionS2CPacket(int i, int j, int k, int l, byte b, byte c, boolean bl) {
		this.id = i;
		this.x = j;
		this.y = k;
		this.z = l;
		this.yaw = b;
		this.pitch = c;
		this.onGround = bl;
	}

	@Override
	public void read(PacketByteBuf buf) throws IOException {
		this.id = buf.readVarInt();
		this.x = buf.readInt();
		this.y = buf.readInt();
		this.z = buf.readInt();
		this.yaw = buf.readByte();
		this.pitch = buf.readByte();
		this.onGround = buf.readBoolean();
	}

	@Override
	public void write(PacketByteBuf buf) throws IOException {
		buf.writeVarInt(this.id);
		buf.writeInt(this.x);
		buf.writeInt(this.y);
		buf.writeInt(this.z);
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

	public int getX() {
		return this.x;
	}

	public int getY() {
		return this.y;
	}

	public int getZ() {
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
