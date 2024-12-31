package net.minecraft.client.network.packet;

import java.io.IOException;
import net.minecraft.network.Packet;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.util.PacketByteBuf;

public class HealthUpdateS2CPacket implements Packet<ClientPlayPacketListener> {
	private float health;
	private int food;
	private float saturation;

	public HealthUpdateS2CPacket() {
	}

	public HealthUpdateS2CPacket(float f, int i, float g) {
		this.health = f;
		this.food = i;
		this.saturation = g;
	}

	@Override
	public void read(PacketByteBuf packetByteBuf) throws IOException {
		this.health = packetByteBuf.readFloat();
		this.food = packetByteBuf.readVarInt();
		this.saturation = packetByteBuf.readFloat();
	}

	@Override
	public void write(PacketByteBuf packetByteBuf) throws IOException {
		packetByteBuf.writeFloat(this.health);
		packetByteBuf.writeVarInt(this.food);
		packetByteBuf.writeFloat(this.saturation);
	}

	public void method_11832(ClientPlayPacketListener clientPlayPacketListener) {
		clientPlayPacketListener.onHealthUpdate(this);
	}

	public float getHealth() {
		return this.health;
	}

	public int getFood() {
		return this.food;
	}

	public float getSaturation() {
		return this.saturation;
	}
}
