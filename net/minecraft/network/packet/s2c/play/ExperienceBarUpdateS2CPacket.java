package net.minecraft.network.packet.s2c.play;

import java.io.IOException;
import net.minecraft.network.Packet;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.util.PacketByteBuf;

public class ExperienceBarUpdateS2CPacket implements Packet<ClientPlayPacketListener> {
	private float health;
	private int food;
	private int xpLevel;

	public ExperienceBarUpdateS2CPacket() {
	}

	public ExperienceBarUpdateS2CPacket(float f, int i, int j) {
		this.health = f;
		this.food = i;
		this.xpLevel = j;
	}

	@Override
	public void read(PacketByteBuf buf) throws IOException {
		this.health = buf.readFloat();
		this.xpLevel = buf.readVarInt();
		this.food = buf.readVarInt();
	}

	@Override
	public void write(PacketByteBuf buf) throws IOException {
		buf.writeFloat(this.health);
		buf.writeVarInt(this.xpLevel);
		buf.writeVarInt(this.food);
	}

	public void apply(ClientPlayPacketListener clientPlayPacketListener) {
		clientPlayPacketListener.onHealthUpdate(this);
	}

	public float getBarProgress() {
		return this.health;
	}

	public int getExperienceLevel() {
		return this.food;
	}

	public int getExperience() {
		return this.xpLevel;
	}
}
