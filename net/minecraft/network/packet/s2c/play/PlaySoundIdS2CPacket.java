package net.minecraft.network.packet.s2c.play;

import java.io.IOException;
import net.minecraft.client.sound.SoundCategory;
import net.minecraft.network.Packet;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.sound.Sound;
import net.minecraft.util.PacketByteBuf;
import net.minecraft.util.math.MathHelper;
import org.apache.commons.lang3.Validate;

public class PlaySoundIdS2CPacket implements Packet<ClientPlayPacketListener> {
	private Sound sound;
	private SoundCategory category;
	private int fixedX;
	private int fixedY;
	private int fixedZ;
	private float volume;
	private int pitch;

	public PlaySoundIdS2CPacket() {
	}

	public PlaySoundIdS2CPacket(Sound sound, SoundCategory soundCategory, double d, double e, double f, float g, float h) {
		Validate.notNull(sound, "sound", new Object[0]);
		this.sound = sound;
		this.category = soundCategory;
		this.fixedX = (int)(d * 8.0);
		this.fixedY = (int)(e * 8.0);
		this.fixedZ = (int)(f * 8.0);
		this.volume = g;
		this.pitch = (int)(h * 63.0F);
		h = MathHelper.clamp(h, 0.0F, 255.0F);
	}

	@Override
	public void read(PacketByteBuf buf) throws IOException {
		this.sound = Sound.REGISTRY.getByRawId(buf.readVarInt());
		this.category = buf.readEnumConstant(SoundCategory.class);
		this.fixedX = buf.readInt();
		this.fixedY = buf.readInt();
		this.fixedZ = buf.readInt();
		this.volume = buf.readFloat();
		this.pitch = buf.readUnsignedByte();
	}

	@Override
	public void write(PacketByteBuf buf) throws IOException {
		buf.writeVarInt(Sound.REGISTRY.getRawId(this.sound));
		buf.writeEnumConstant(this.category);
		buf.writeInt(this.fixedX);
		buf.writeInt(this.fixedY);
		buf.writeInt(this.fixedZ);
		buf.writeFloat(this.volume);
		buf.writeByte(this.pitch);
	}

	public Sound getSound() {
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
		return (float)this.pitch / 63.0F;
	}

	public void apply(ClientPlayPacketListener clientPlayPacketListener) {
		clientPlayPacketListener.onPlaySound(this);
	}
}
