package net.minecraft.network.packet.c2s.play;

import java.io.IOException;
import net.minecraft.network.Packet;
import net.minecraft.network.listener.ServerPlayPacketListener;
import net.minecraft.util.PacketByteBuf;

public class PlayerMoveC2SPacket implements Packet<ServerPlayPacketListener> {
	protected double x;
	protected double y;
	protected double z;
	protected float yaw;
	protected float pitch;
	protected boolean onGround;
	protected boolean changePosition;
	protected boolean changeLook;

	public PlayerMoveC2SPacket() {
	}

	public PlayerMoveC2SPacket(boolean bl) {
		this.onGround = bl;
	}

	public void apply(ServerPlayPacketListener serverPlayPacketListener) {
		serverPlayPacketListener.onPlayerMove(this);
	}

	@Override
	public void read(PacketByteBuf buf) throws IOException {
		this.onGround = buf.readUnsignedByte() != 0;
	}

	@Override
	public void write(PacketByteBuf buf) throws IOException {
		buf.writeByte(this.onGround ? 1 : 0);
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

	public float getYaw() {
		return this.yaw;
	}

	public float getPitch() {
		return this.pitch;
	}

	public boolean isOnGround() {
		return this.onGround;
	}

	public boolean isPositionChanged() {
		return this.changePosition;
	}

	public boolean isLookChanged() {
		return this.changeLook;
	}

	public void setPositionChanged(boolean changePosition) {
		this.changePosition = changePosition;
	}

	public static class Both extends PlayerMoveC2SPacket {
		public Both() {
			this.changePosition = true;
			this.changeLook = true;
		}

		public Both(double d, double e, double f, float g, float h, boolean bl) {
			this.x = d;
			this.y = e;
			this.z = f;
			this.yaw = g;
			this.pitch = h;
			this.onGround = bl;
			this.changeLook = true;
			this.changePosition = true;
		}

		@Override
		public void read(PacketByteBuf buf) throws IOException {
			this.x = buf.readDouble();
			this.y = buf.readDouble();
			this.z = buf.readDouble();
			this.yaw = buf.readFloat();
			this.pitch = buf.readFloat();
			super.read(buf);
		}

		@Override
		public void write(PacketByteBuf buf) throws IOException {
			buf.writeDouble(this.x);
			buf.writeDouble(this.y);
			buf.writeDouble(this.z);
			buf.writeFloat(this.yaw);
			buf.writeFloat(this.pitch);
			super.write(buf);
		}
	}

	public static class LookOnly extends PlayerMoveC2SPacket {
		public LookOnly() {
			this.changeLook = true;
		}

		public LookOnly(float f, float g, boolean bl) {
			this.yaw = f;
			this.pitch = g;
			this.onGround = bl;
			this.changeLook = true;
		}

		@Override
		public void read(PacketByteBuf buf) throws IOException {
			this.yaw = buf.readFloat();
			this.pitch = buf.readFloat();
			super.read(buf);
		}

		@Override
		public void write(PacketByteBuf buf) throws IOException {
			buf.writeFloat(this.yaw);
			buf.writeFloat(this.pitch);
			super.write(buf);
		}
	}

	public static class PositionOnly extends PlayerMoveC2SPacket {
		public PositionOnly() {
			this.changePosition = true;
		}

		public PositionOnly(double d, double e, double f, boolean bl) {
			this.x = d;
			this.y = e;
			this.z = f;
			this.onGround = bl;
			this.changePosition = true;
		}

		@Override
		public void read(PacketByteBuf buf) throws IOException {
			this.x = buf.readDouble();
			this.y = buf.readDouble();
			this.z = buf.readDouble();
			super.read(buf);
		}

		@Override
		public void write(PacketByteBuf buf) throws IOException {
			buf.writeDouble(this.x);
			buf.writeDouble(this.y);
			buf.writeDouble(this.z);
			super.write(buf);
		}
	}
}
