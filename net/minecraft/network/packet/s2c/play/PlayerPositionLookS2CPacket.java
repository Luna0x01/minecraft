package net.minecraft.network.packet.s2c.play;

import java.io.IOException;
import java.util.EnumSet;
import java.util.Set;
import net.minecraft.network.Packet;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.util.PacketByteBuf;

public class PlayerPositionLookS2CPacket implements Packet<ClientPlayPacketListener> {
	private double x;
	private double y;
	private double z;
	private float yaw;
	private float pitch;
	private Set<PlayerPositionLookS2CPacket.Flag> flags;

	public PlayerPositionLookS2CPacket() {
	}

	public PlayerPositionLookS2CPacket(double d, double e, double f, float g, float h, Set<PlayerPositionLookS2CPacket.Flag> set) {
		this.x = d;
		this.y = e;
		this.z = f;
		this.yaw = g;
		this.pitch = h;
		this.flags = set;
	}

	@Override
	public void read(PacketByteBuf buf) throws IOException {
		this.x = buf.readDouble();
		this.y = buf.readDouble();
		this.z = buf.readDouble();
		this.yaw = buf.readFloat();
		this.pitch = buf.readFloat();
		this.flags = PlayerPositionLookS2CPacket.Flag.getFlags(buf.readUnsignedByte());
	}

	@Override
	public void write(PacketByteBuf buf) throws IOException {
		buf.writeDouble(this.x);
		buf.writeDouble(this.y);
		buf.writeDouble(this.z);
		buf.writeFloat(this.yaw);
		buf.writeFloat(this.pitch);
		buf.writeByte(PlayerPositionLookS2CPacket.Flag.getBitField(this.flags));
	}

	public void apply(ClientPlayPacketListener clientPlayPacketListener) {
		clientPlayPacketListener.onPlayerPositionLook(this);
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

	public Set<PlayerPositionLookS2CPacket.Flag> getFlags() {
		return this.flags;
	}

	public static enum Flag {
		X(0),
		Y(1),
		Z(2),
		Y_ROT(3),
		X_ROT(4);

		private int shift;

		private Flag(int j) {
			this.shift = j;
		}

		private int getMask() {
			return 1 << this.shift;
		}

		private boolean isSet(int i) {
			return (i & this.getMask()) == this.getMask();
		}

		public static Set<PlayerPositionLookS2CPacket.Flag> getFlags(int i) {
			Set<PlayerPositionLookS2CPacket.Flag> set = EnumSet.noneOf(PlayerPositionLookS2CPacket.Flag.class);

			for (PlayerPositionLookS2CPacket.Flag flag : values()) {
				if (flag.isSet(i)) {
					set.add(flag);
				}
			}

			return set;
		}

		public static int getBitField(Set<PlayerPositionLookS2CPacket.Flag> set) {
			int i = 0;

			for (PlayerPositionLookS2CPacket.Flag flag : set) {
				i |= flag.getMask();
			}

			return i;
		}
	}
}
