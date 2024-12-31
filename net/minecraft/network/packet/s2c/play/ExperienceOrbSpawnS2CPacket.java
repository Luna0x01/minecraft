package net.minecraft.network.packet.s2c.play;

import java.io.IOException;
import net.minecraft.entity.ExperienceOrbEntity;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.listener.ClientPlayPacketListener;

public class ExperienceOrbSpawnS2CPacket implements Packet<ClientPlayPacketListener> {
	private int id;
	private double x;
	private double y;
	private double z;
	private int experience;

	public ExperienceOrbSpawnS2CPacket() {
	}

	public ExperienceOrbSpawnS2CPacket(ExperienceOrbEntity experienceOrbEntity) {
		this.id = experienceOrbEntity.getEntityId();
		this.x = experienceOrbEntity.getX();
		this.y = experienceOrbEntity.getY();
		this.z = experienceOrbEntity.getZ();
		this.experience = experienceOrbEntity.getExperienceAmount();
	}

	@Override
	public void read(PacketByteBuf buf) throws IOException {
		this.id = buf.readVarInt();
		this.x = buf.readDouble();
		this.y = buf.readDouble();
		this.z = buf.readDouble();
		this.experience = buf.readShort();
	}

	@Override
	public void write(PacketByteBuf buf) throws IOException {
		buf.writeVarInt(this.id);
		buf.writeDouble(this.x);
		buf.writeDouble(this.y);
		buf.writeDouble(this.z);
		buf.writeShort(this.experience);
	}

	public void apply(ClientPlayPacketListener clientPlayPacketListener) {
		clientPlayPacketListener.onExperienceOrbSpawn(this);
	}

	public int getId() {
		return this.id;
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

	public int getExperience() {
		return this.experience;
	}
}
