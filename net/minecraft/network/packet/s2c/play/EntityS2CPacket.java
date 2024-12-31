package net.minecraft.network.packet.s2c.play;

import java.io.IOException;
import net.minecraft.entity.Entity;
import net.minecraft.network.Packet;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.util.PacketByteBuf;
import net.minecraft.world.World;

public class EntityS2CPacket implements Packet<ClientPlayPacketListener> {
	protected int id;
	protected byte x;
	protected byte y;
	protected byte z;
	protected byte yaw;
	protected byte pitch;
	protected boolean onGround;
	protected boolean rotate;

	public EntityS2CPacket() {
	}

	public EntityS2CPacket(int i) {
		this.id = i;
	}

	@Override
	public void read(PacketByteBuf buf) throws IOException {
		this.id = buf.readVarInt();
	}

	@Override
	public void write(PacketByteBuf buf) throws IOException {
		buf.writeVarInt(this.id);
	}

	public void apply(ClientPlayPacketListener clientPlayPacketListener) {
		clientPlayPacketListener.onEntityUpdate(this);
	}

	public String toString() {
		return "Entity_" + super.toString();
	}

	public Entity getEntity(World world) {
		return world.getEntityById(this.id);
	}

	public byte getX() {
		return this.x;
	}

	public byte getY() {
		return this.y;
	}

	public byte getZ() {
		return this.z;
	}

	public byte getYaw() {
		return this.yaw;
	}

	public byte getPitch() {
		return this.pitch;
	}

	public boolean shouldRotate() {
		return this.rotate;
	}

	public boolean isOnGround() {
		return this.onGround;
	}

	public static class MoveRelative extends EntityS2CPacket {
		public MoveRelative() {
		}

		public MoveRelative(int i, byte b, byte c, byte d, boolean bl) {
			super(i);
			this.x = b;
			this.y = c;
			this.z = d;
			this.onGround = bl;
		}

		@Override
		public void read(PacketByteBuf buf) throws IOException {
			super.read(buf);
			this.x = buf.readByte();
			this.y = buf.readByte();
			this.z = buf.readByte();
			this.onGround = buf.readBoolean();
		}

		@Override
		public void write(PacketByteBuf buf) throws IOException {
			super.write(buf);
			buf.writeByte(this.x);
			buf.writeByte(this.y);
			buf.writeByte(this.z);
			buf.writeBoolean(this.onGround);
		}
	}

	public static class Rotate extends EntityS2CPacket {
		public Rotate() {
			this.rotate = true;
		}

		public Rotate(int i, byte b, byte c, boolean bl) {
			super(i);
			this.yaw = b;
			this.pitch = c;
			this.rotate = true;
			this.onGround = bl;
		}

		@Override
		public void read(PacketByteBuf buf) throws IOException {
			super.read(buf);
			this.yaw = buf.readByte();
			this.pitch = buf.readByte();
			this.onGround = buf.readBoolean();
		}

		@Override
		public void write(PacketByteBuf buf) throws IOException {
			super.write(buf);
			buf.writeByte(this.yaw);
			buf.writeByte(this.pitch);
			buf.writeBoolean(this.onGround);
		}
	}

	public static class RotateAndMoveRelative extends EntityS2CPacket {
		public RotateAndMoveRelative() {
			this.rotate = true;
		}

		public RotateAndMoveRelative(int i, byte b, byte c, byte d, byte e, byte f, boolean bl) {
			super(i);
			this.x = b;
			this.y = c;
			this.z = d;
			this.yaw = e;
			this.pitch = f;
			this.onGround = bl;
			this.rotate = true;
		}

		@Override
		public void read(PacketByteBuf buf) throws IOException {
			super.read(buf);
			this.x = buf.readByte();
			this.y = buf.readByte();
			this.z = buf.readByte();
			this.yaw = buf.readByte();
			this.pitch = buf.readByte();
			this.onGround = buf.readBoolean();
		}

		@Override
		public void write(PacketByteBuf buf) throws IOException {
			super.write(buf);
			buf.writeByte(this.x);
			buf.writeByte(this.y);
			buf.writeByte(this.z);
			buf.writeByte(this.yaw);
			buf.writeByte(this.pitch);
			buf.writeBoolean(this.onGround);
		}
	}
}
