package net.minecraft.client.network.packet;

import java.io.IOException;
import net.minecraft.network.Packet;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.util.PacketByteBuf;

public class ExperienceBarUpdateS2CPacket implements Packet<ClientPlayPacketListener> {
	private float barProgress;
	private int experienceLevel;
	private int experience;

	public ExperienceBarUpdateS2CPacket() {
	}

	public ExperienceBarUpdateS2CPacket(float f, int i, int j) {
		this.barProgress = f;
		this.experienceLevel = i;
		this.experience = j;
	}

	@Override
	public void read(PacketByteBuf packetByteBuf) throws IOException {
		this.barProgress = packetByteBuf.readFloat();
		this.experience = packetByteBuf.readVarInt();
		this.experienceLevel = packetByteBuf.readVarInt();
	}

	@Override
	public void write(PacketByteBuf packetByteBuf) throws IOException {
		packetByteBuf.writeFloat(this.barProgress);
		packetByteBuf.writeVarInt(this.experience);
		packetByteBuf.writeVarInt(this.experienceLevel);
	}

	public void apply(ClientPlayPacketListener clientPlayPacketListener) {
		clientPlayPacketListener.onExperienceBarUpdate(this);
	}

	public float getBarProgress() {
		return this.barProgress;
	}

	public int getExperienceLevel() {
		return this.experienceLevel;
	}

	public int getExperience() {
		return this.experience;
	}
}
