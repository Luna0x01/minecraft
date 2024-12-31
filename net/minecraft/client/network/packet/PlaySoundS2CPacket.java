package net.minecraft.client.network.packet;

import java.io.IOException;
import net.minecraft.network.Packet;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.PacketByteBuf;
import net.minecraft.util.registry.Registry;
import org.apache.commons.lang3.Validate;

public class PlaySoundS2CPacket implements Packet<ClientPlayPacketListener> {
	private SoundEvent sound;
	private SoundCategory category;
	private int fixedX;
	private int fixedY;
	private int fixedZ;
	private float volume;
	private float pitch;

	public PlaySoundS2CPacket() {
	}

	public PlaySoundS2CPacket(SoundEvent soundEvent, SoundCategory soundCategory, double d, double e, double f, float g, float h) {
		Validate.notNull(soundEvent, "sound", new Object[0]);
		this.sound = soundEvent;
		this.category = soundCategory;
		this.fixedX = (int)(d * 8.0);
		this.fixedY = (int)(e * 8.0);
		this.fixedZ = (int)(f * 8.0);
		this.volume = g;
		this.pitch = h;
	}

	@Override
	public void read(PacketByteBuf packetByteBuf) throws IOException {
		this.sound = Registry.field_11156.get(packetByteBuf.readVarInt());
		this.category = packetByteBuf.readEnumConstant(SoundCategory.class);
		this.fixedX = packetByteBuf.readInt();
		this.fixedY = packetByteBuf.readInt();
		this.fixedZ = packetByteBuf.readInt();
		this.volume = packetByteBuf.readFloat();
		this.pitch = packetByteBuf.readFloat();
	}

	@Override
	public void write(PacketByteBuf packetByteBuf) throws IOException {
		packetByteBuf.writeVarInt(Registry.field_11156.getRawId(this.sound));
		packetByteBuf.writeEnumConstant(this.category);
		packetByteBuf.writeInt(this.fixedX);
		packetByteBuf.writeInt(this.fixedY);
		packetByteBuf.writeInt(this.fixedZ);
		packetByteBuf.writeFloat(this.volume);
		packetByteBuf.writeFloat(this.pitch);
	}

	public SoundEvent getSound() {
		return this.sound;
	}

	public SoundCategory getCategory() {
		return this.category;
	}

	public double getX() {
		return (double)((float)this.fixedX / 8.0F);
	}

	public double getY() {
		return (double)((float)this.fixedY / 8.0F);
	}

	public double getZ() {
		return (double)((float)this.fixedZ / 8.0F);
	}

	public float getVolume() {
		return this.volume;
	}

	public float getPitch() {
		return this.pitch;
	}

	public void apply(ClientPlayPacketListener clientPlayPacketListener) {
		clientPlayPacketListener.onPlaySound(this);
	}
}
