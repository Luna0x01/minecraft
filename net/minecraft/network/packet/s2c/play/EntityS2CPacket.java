package net.minecraft.network.packet.s2c.play;

import java.io.IOException;
import net.minecraft.entity.Entity;
import net.minecraft.network.Packet;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.util.PacketByteBuf;
import net.minecraft.world.World;

public class EntityS2CPacket implements Packet<ClientPlayPacketListener> {
	protected int id;
	protected int field_13775;
	protected int field_13776;
	protected int field_13777;
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

	public int method_7808() {
		return this.field_13775;
	}

	public int method_7809() {
		return this.field_13776;
	}

	public int method_7810() {
		return this.field_13777;
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

		public MoveRelative(int i, long l, long m, long n, boolean bl) {
			super(i);
			this.field_13775 = (int)l;
			this.field_13776 = (int)m;
			this.field_13777 = (int)n;
			this.onGround = bl;
		}

		@Override
		public void read(PacketByteBuf buf) throws IOException {
			super.read(buf);
			this.field_13775 = buf.readShort();
			this.field_13776 = buf.readShort();
			this.field_13777 = buf.readShort();
			this.onGround = buf.readBoolean();
		}

		@Override
		public void write(PacketByteBuf buf) throws IOException {
			super.write(buf);
			buf.writeShort(this.field_13775);
			buf.writeShort(this.field_13776);
			buf.writeShort(this.field_13777);
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

		public RotateAndMoveRelative(int i, long l, long m, long n, byte b, byte c, boolean bl) {
			super(i);
			this.field_13775 = (int)l;
			this.field_13776 = (int)m;
			this.field_13777 = (int)n;
			this.yaw = b;
			this.pitch = c;
			this.onGround = bl;
			this.rotate = true;
		}

		@Override
		public void read(PacketByteBuf buf) throws IOException {
			super.read(buf);
			this.field_13775 = buf.readShort();
			this.field_13776 = buf.readShort();
			this.field_13777 = buf.readShort();
			this.yaw = buf.readByte();
			this.pitch = buf.readByte();
			this.onGround = buf.readBoolean();
		}

		@Override
		public void write(PacketByteBuf buf) throws IOException {
			super.write(buf);
			buf.writeShort(this.field_13775);
			buf.writeShort(this.field_13776);
			buf.writeShort(this.field_13777);
			buf.writeByte(this.yaw);
			buf.writeByte(this.pitch);
			buf.writeBoolean(this.onGround);
		}
	}
}
