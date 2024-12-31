package net.minecraft.network.packet.s2c.play;

import java.io.IOException;
import net.minecraft.client.sound.SoundCategory;
import net.minecraft.network.Packet;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.util.PacketByteBuf;
import org.apache.commons.lang3.Validate;

public class PlaySoundNameS2CPacket implements Packet<ClientPlayPacketListener> {
	private String name;
	private SoundCategory category;
	private int x;
	private int y = Integer.MAX_VALUE;
	private int z;
	private float volume;
	private float pitch;

	public PlaySoundNameS2CPacket() {
	}

	public PlaySoundNameS2CPacket(String string, SoundCategory soundCategory, double d, double e, double f, float g, float h) {
		Validate.notNull(string, "name", new Object[0]);
		this.name = string;
		this.category = soundCategory;
		this.x = (int)(d * 8.0);
		this.y = (int)(e * 8.0);
		this.z = (int)(f * 8.0);
		this.volume = g;
		this.pitch = h;
	}

	@Override
	public void read(PacketByteBuf buf) throws IOException {
		this.name = buf.readString(256);
		this.category = buf.readEnumConstant(SoundCategory.class);
		this.x = buf.readInt();
		this.y = buf.readInt();
		this.z = buf.readInt();
		this.volume = buf.readFloat();
		this.pitch = buf.readFloat();
	}

	@Override
	public void write(PacketByteBuf buf) throws IOException {
		buf.writeString(this.name);
		buf.writeEnumConstant(this.category);
		buf.writeInt(this.x);
		buf.writeInt(this.y);
		buf.writeInt(this.z);
		buf.writeFloat(this.volume);
		buf.writeFloat(this.pitch);
	}

	public String getName() {
		return this.name;
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
