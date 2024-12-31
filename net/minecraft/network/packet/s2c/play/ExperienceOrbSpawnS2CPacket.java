package net.minecraft.network.packet.s2c.play;

import java.io.IOException;
import net.minecraft.entity.ExperienceOrbEntity;
import net.minecraft.network.Packet;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.util.PacketByteBuf;
import net.minecraft.util.math.MathHelper;

public class ExperienceOrbSpawnS2CPacket implements Packet<ClientPlayPacketListener> {
	private int id;
	private int x;
	private int y;
	private int z;
	private int experience;

	public ExperienceOrbSpawnS2CPacket() {
	}

	public ExperienceOrbSpawnS2CPacket(ExperienceOrbEntity experienceOrbEntity) {
		this.id = experienceOrbEntity.getEntityId();
		this.x = MathHelper.floor(experienceOrbEntity.x * 32.0);
		this.y = MathHelper.floor(experienceOrbEntity.y * 32.0);
		this.z = MathHelper.floor(experienceOrbEntity.z * 32.0);
		this.experience = experienceOrbEntity.getExperienceAmount();
	}

	@Override
	public void read(PacketByteBuf buf) throws IOException {
		this.id = buf.readVarInt();
		this.x = buf.readInt();
		this.y = buf.readInt();
		this.z = buf.readInt();
		this.experience = buf.readShort();
	}

	@Override
	public void write(PacketByteBuf buf) throws IOException {
		buf.writeVarInt(this.id);
		buf.writeInt(this.x);
		buf.writeInt(this.y);
		buf.writeInt(this.z);
		buf.writeShort(this.experience);
	}

	public void apply(ClientPlayPacketListener clientPlayPacketListener) {
		clientPlayPacketListener.onExperienceOrbSpawn(this);
	}

	public int getId() {
		return this.id;
	}

	public int getX() {
		return this.x;
	}

	public int getY() {
		return this.y;
	}

	public int getZ() {
		return this.z;
	}

	public int getExperience() {
		return this.experience;
	}
}
