package net.minecraft.network.packet.s2c.play;

import java.io.IOException;
import net.minecraft.client.sound.SoundCategory;
import net.minecraft.network.Packet;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.util.Identifier;
import net.minecraft.util.PacketByteBuf;
import net.minecraft.util.math.Vec3d;

public class PlaySoundNameS2CPacket implements Packet<ClientPlayPacketListener> {
	private Identifier field_21540;
	private SoundCategory category;
	private int x;
	private int y = Integer.MAX_VALUE;
	private int z;
	private float volume;
	private float pitch;

	public PlaySoundNameS2CPacket() {
	}

	public PlaySoundNameS2CPacket(Identifier identifier, SoundCategory soundCategory, Vec3d vec3d, float f, float g) {
		this.field_21540 = identifier;
		this.category = soundCategory;
		this.x = (int)(vec3d.x * 8.0);
		this.y = (int)(vec3d.y * 8.0);
		this.z = (int)(vec3d.z * 8.0);
		this.volume = f;
		this.pitch = g;
	}

	@Override
	public void read(PacketByteBuf buf) throws IOException {
		this.field_21540 = buf.readIdentifier();
		this.category = buf.readEnumConstant(SoundCategory.class);
		this.x = buf.readInt();
		this.y = buf.readInt();
		this.z = buf.readInt();
		this.volume = buf.readFloat();
		this.pitch = buf.readFloat();
	}

	@Override
	public void write(PacketByteBuf buf) throws IOException {
		buf.writeIdentifier(this.field_21540);
		buf.writeEnumConstant(this.category);
		buf.writeInt(this.x);
		buf.writeInt(this.y);
		buf.writeInt(this.z);
		buf.writeFloat(this.volume);
		buf.writeFloat(this.pitch);
	}

	public Identifier method_12644() {
		return this.field_21540;
	}

	public SoundCategory getCategory() {
		return this.category;
	}

	public double getX() {
		return (double)((float)this.x / 8.0F);
	}

	public double getY() {
		return (double)((float)this.y / 8.0F);
	}

	public double getZ() {
		return (double)((float)this.z / 8.0F);
	}

	public float getVolume() {
		return this.volume;
	}

	public float getPitch() {
		return this.pitch;
	}

	public void apply(ClientPlayPacketListener clientPlayPacketListener) {
		clientPlayPacketListener.onPlaySoundName(this);
	}
}
