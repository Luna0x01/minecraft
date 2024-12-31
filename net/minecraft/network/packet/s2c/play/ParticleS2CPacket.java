package net.minecraft.network.packet.s2c.play;

import java.io.IOException;
import net.minecraft.class_4342;
import net.minecraft.client.particle.ParticleType;
import net.minecraft.network.Packet;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.util.PacketByteBuf;
import net.minecraft.util.registry.Registry;

public class ParticleS2CPacket implements Packet<ClientPlayPacketListener> {
	private float x;
	private float y;
	private float z;
	private float offsetX;
	private float offsetY;
	private float offsetZ;
	private float speed;
	private int count;
	private boolean longDistance;
	private ParticleEffect field_11613;

	public ParticleS2CPacket() {
	}

	public <T extends ParticleEffect> ParticleS2CPacket(T particleEffect, boolean bl, float f, float g, float h, float i, float j, float k, float l, int m) {
		this.field_11613 = particleEffect;
		this.longDistance = bl;
		this.x = f;
		this.y = g;
		this.z = h;
		this.offsetX = i;
		this.offsetY = j;
		this.offsetZ = k;
		this.speed = l;
		this.count = m;
	}

	@Override
	public void read(PacketByteBuf buf) throws IOException {
		ParticleType<?> particleType = Registry.PARTICLE_TYPE.getByRawId(buf.readInt());
		if (particleType == null) {
			particleType = class_4342.field_21377;
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
		this.field_11613 = this.method_20241(buf, (ParticleType<ParticleEffect>)particleType);
	}

	private <T extends ParticleEffect> T method_20241(PacketByteBuf packetByteBuf, ParticleType<T> particleType) {
		return particleType.method_19987().method_19982(particleType, packetByteBuf);
	}

	@Override
	public void write(PacketByteBuf buf) throws IOException {
		buf.writeInt(Registry.PARTICLE_TYPE.getRawId((ParticleType<? extends ParticleEffect>)this.field_11613.particleType()));
		buf.writeBoolean(this.longDistance);
		buf.writeFloat(this.x);
		buf.writeFloat(this.y);
		buf.writeFloat(this.z);
		buf.writeFloat(this.offsetX);
		buf.writeFloat(this.offsetY);
		buf.writeFloat(this.offsetZ);
		buf.writeFloat(this.speed);
		buf.writeInt(this.count);
		this.field_11613.method_19979(buf);
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

	public ParticleEffect method_10663() {
		return this.field_11613;
	}

	public void apply(ClientPlayPacketListener clientPlayPacketListener) {
		clientPlayPacketListener.onParticle(this);
	}
}
