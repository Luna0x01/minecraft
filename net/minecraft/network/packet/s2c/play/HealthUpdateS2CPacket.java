package net.minecraft.network.packet.s2c.play;

import java.io.IOException;
import net.minecraft.network.Packet;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.util.PacketByteBuf;

public class HealthUpdateS2CPacket implements Packet<ClientPlayPacketListener> {
	private float barProgress;
	private int xpLevel;
	private float saturation;

	public HealthUpdateS2CPacket() {
	}

	public HealthUpdateS2CPacket(float f, int i, float g) {
		this.barProgress = f;
		this.xpLevel = i;
		this.saturation = g;
	}

	@Override
	public void read(PacketByteBuf buf) throws IOException {
		this.barProgress = buf.readFloat();
		this.xpLevel = buf.readVarInt();
		this.saturation = buf.readFloat();
	}

	@Override
	public void write(PacketByteBuf buf) throws IOException {
		buf.writeFloat(this.barProgress);
		buf.writeVarInt(this.xpLevel);
		buf.writeFloat(this.saturation);
	}

	public void apply(ClientPlayPacketListener clientPlayPacketListener) {
		clientPlayPacketListener.onExperienceBarUpdate(this);
	}

	public float getHealth() {
		return this.barProgress;
	}

	public int getFood() {
		return this.xpLevel;
	}

	public float getSaturation() {
		return this.saturation;
	}
}
