package net.minecraft.client.network.packet;

import java.io.IOException;
import net.minecraft.network.Packet;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleType;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.util.PacketByteBuf;
import net.minecraft.util.registry.Registry;

public class ParticleS2CPacket implements Packet<ClientPlayPacketListener> {
	private double x;
	private double y;
	private double z;
	private float offsetX;
	private float offsetY;
	private float offsetZ;
	private float speed;
	private int count;
	private boolean longDistance;
	private ParticleEffect parameters;

	public ParticleS2CPacket() {
	}

	public <T extends ParticleEffect> ParticleS2CPacket(T particleEffect, boolean bl, double d, double e, double f, float g, float h, float i, float j, int k) {
		this.parameters = particleEffect;
		this.longDistance = bl;
		this.x = d;
		this.y = e;
		this.z = f;
		this.offsetX = g;
		this.offsetY = h;
		this.offsetZ = i;
		this.speed = j;
		this.count = k;
	}

	@Override
	public void read(PacketByteBuf packetByteBuf) throws IOException {
		ParticleType<?> particleType = Registry.field_11141.get(packetByteBuf.readInt());
		if (particleType == null) {
			particleType = ParticleTypes.field_11235;
		}

		this.longDistance = packetByteBuf.readBoolean();
		this.x = packetByteBuf.readDouble();
		this.y = packetByteBuf.readDouble();
		this.z = packetByteBuf.readDouble();
		this.offsetX = packetByteBuf.readFloat();
		this.offsetY = packetByteBuf.readFloat();
		this.offsetZ = packetByteBuf.readFloat();
		this.speed = packetByteBuf.readFloat();
		this.count = packetByteBuf.readInt();
		this.parameters = this.readParticleParameters(packetByteBuf, (ParticleType<ParticleEffect>)particleType);
	}

	private <T extends ParticleEffect> T readParticleParameters(PacketByteBuf packetByteBuf, ParticleType<T> particleType) {
		return particleType.getParametersFactory().read(particleType, packetByteBuf);
	}

	@Override
	public void write(PacketByteBuf packetByteBuf) throws IOException {
		packetByteBuf.writeInt(Registry.field_11141.getRawId((ParticleType<? extends ParticleEffect>)this.parameters.getType()));
		packetByteBuf.writeBoolean(this.longDistance);
		packetByteBuf.writeDouble(this.x);
		packetByteBuf.writeDouble(this.y);
		packetByteBuf.writeDouble(this.z);
		packetByteBuf.writeFloat(this.offsetX);
		packetByteBuf.writeFloat(this.offsetY);
		packetByteBuf.writeFloat(this.offsetZ);
		packetByteBuf.writeFloat(this.speed);
		packetByteBuf.writeInt(this.count);
		this.parameters.write(packetByteBuf);
	}

	public boolean isLongDistance() {
		return this.longDistance;
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

	public ParticleEffect getParameters() {
		return this.parameters;
	}

	public void apply(ClientPlayPacketListener clientPlayPacketListener) {
		clientPlayPacketListener.onParticle(this);
	}
}
