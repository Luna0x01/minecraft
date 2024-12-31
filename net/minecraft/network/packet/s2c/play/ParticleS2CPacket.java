package net.minecraft.network.packet.s2c.play;

import java.io.IOException;
import net.minecraft.client.particle.ParticleType;
import net.minecraft.network.Packet;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.util.PacketByteBuf;

public class ParticleS2CPacket implements Packet<ClientPlayPacketListener> {
	private ParticleType particleType;
	private float x;
	private float y;
	private float z;
	private float offsetX;
	private float offsetY;
	private float offsetZ;
	private float speed;
	private int count;
	private boolean longDistance;
	private int[] args;

	public ParticleS2CPacket() {
	}

	public ParticleS2CPacket(ParticleType particleType, boolean bl, float f, float g, float h, float i, float j, float k, float l, int m, int... is) {
		this.particleType = particleType;
		this.longDistance = bl;
		this.x = f;
		this.y = g;
		this.z = h;
		this.offsetX = i;
		this.offsetY = j;
		this.offsetZ = k;
		this.speed = l;
		this.count = m;
		this.args = is;
	}

	@Override
	public void read(PacketByteBuf buf) throws IOException {
		this.particleType = ParticleType.getById(buf.readInt());
		if (this.particleType == null) {
			this.particleType = ParticleType.BARRIER;
		}

		this.longDistance = buf.readBoolean();
		this.x = buf.readFloat();
		this.y = buf.readFloat();
		this.z = buf.readFloat();
		this.offsetX = buf.readFloat();
		this.offsetY = buf.readFloat();
		this.offsetZ = buf.readFloat();
		this.speed = buf.readFloat();
		this.count = buf.readInt();
		int i = this.particleType.getArgs();
		this.args = new int[i];

		for (int j = 0; j < i; j++) {
			this.args[j] = buf.readVarInt();
		}
	}

	@Override
	public void write(PacketByteBuf buf) throws IOException {
		buf.writeInt(this.particleType.getId());
		buf.writeBoolean(this.longDistance);
		buf.writeFloat(this.x);
		buf.writeFloat(this.y);
		buf.writeFloat(this.z);
		buf.writeFloat(this.offsetX);
		buf.writeFloat(this.offsetY);
		buf.writeFloat(this.offsetZ);
		buf.writeFloat(this.speed);
		buf.writeInt(this.count);
		int i = this.particleType.getArgs();

		for (int j = 0; j < i; j++) {
			buf.writeVarInt(this.args[j]);
		}
	}

	public ParticleType getParameters() {
		return this.particleType;
	}

	public boolean isLongDistance() {
		return this.longDistance;
	}

	public double getX() {
		return (double)this.x;
	}

	public double getY() {
		return (double)this.y;
	}

	public double getZ() {
		return (double)this.z;
	}

	public float getOffsetX() {
		return this.offsetX;
	}

	public float getOffsetY() {
		return this.offsetY;
	}

	public float getOffsetZ() {
		return this.offsetZ;
	}

	public float getSpeed() {
		return this.speed;
	}

	public int getCount() {
		return this.count;
	}

	public int[] getArgs() {
		return this.args;
	}

	public void apply(ClientPlayPacketListener clientPlayPacketListener) {
		clientPlayPacketListener.onParticle(this);
	}
}
