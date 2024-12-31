package net.minecraft.server.network.packet;

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
	public void read(PacketByteBuf packetByteBuf) throws IOException {
		this.onGround = packetByteBuf.readUnsignedByte() != 0;
	}

	@Override
	public void write(PacketByteBuf packetByteBuf) throws IOException {
		packetByteBuf.writeByte(this.onGround ? 1 : 0);
	}

	public double getX(double d) {
		return this.changePosition ? this.x : d;
	}

	public double getY(double d) {
		return this.changePosition ? this.y : d;
	}

	public double getZ(double d) {
		return this.changePosition ? this.z : d;
	}

	public float getYaw(float f) {
		return this.changeLook ? this.yaw : f;
	}

	public float getPitch(float f) {
		return this.changeLook ? this.pitch : f;
	}

	public boolean isOnGround() {
		return this.onGround;
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
		public void read(PacketByteBuf packetByteBuf) throws IOException {
			this.x = packetByteBuf.readDouble();
			this.y = packetByteBuf.readDouble();
			this.z = packetByteBuf.readDouble();
			this.yaw = packetByteBuf.readFloat();
			this.pitch = packetByteBuf.readFloat();
			super.read(packetByteBuf);
		}

		@Override
		public void write(PacketByteBuf packetByteBuf) throws IOException {
			packetByteBuf.writeDouble(this.x);
			packetByteBuf.writeDouble(this.y);
			packetByteBuf.writeDouble(this.z);
			packetByteBuf.writeFloat(this.yaw);
			packetByteBuf.writeFloat(this.pitch);
			super.write(packetByteBuf);
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
		public void read(PacketByteBuf packetByteBuf) throws IOException {
			this.yaw = packetByteBuf.readFloat();
			this.pitch = packetByteBuf.readFloat();
			super.read(packetByteBuf);
		}

		@Override
		public void write(PacketByteBuf packetByteBuf) throws IOException {
			packetByteBuf.writeFloat(this.yaw);
			packetByteBuf.writeFloat(this.pitch);
			super.write(packetByteBuf);
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
		public void read(PacketByteBuf packetByteBuf) throws IOException {
			this.x = packetByteBuf.readDouble();
			this.y = packetByteBuf.readDouble();
			this.z = packetByteBuf.readDouble();
			super.read(packetByteBuf);
		}

		@Override
		public void write(PacketByteBuf packetByteBuf) throws IOException {
			packetByteBuf.writeDouble(this.x);
			packetByteBuf.writeDouble(this.y);
			packetByteBuf.writeDouble(this.z);
			super.write(packetByteBuf);
		}
	}
}
