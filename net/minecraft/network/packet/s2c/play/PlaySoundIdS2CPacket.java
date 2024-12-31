package net.minecraft.network.packet.s2c.play;

import java.io.IOException;
import net.minecraft.network.Packet;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.util.PacketByteBuf;
import net.minecraft.util.math.MathHelper;
import org.apache.commons.lang3.Validate;

public class PlaySoundIdS2CPacket implements Packet<ClientPlayPacketListener> {
	private String name;
	private int fixedX;
	private int fixedY = Integer.MAX_VALUE;
	private int fixedZ;
	private float volume;
	private int pitch;

	public PlaySoundIdS2CPacket() {
	}

	public PlaySoundIdS2CPacket(String string, double d, double e, double f, float g, float h) {
		Validate.notNull(string, "name", new Object[0]);
		this.name = string;
		this.fixedX = (int)(d * 8.0);
		this.fixedY = (int)(e * 8.0);
		this.fixedZ = (int)(f * 8.0);
		this.volume = g;
		this.pitch = (int)(h * 63.0F);
		h = MathHelper.clamp(h, 0.0F, 255.0F);
	}

	@Override
	public void read(PacketByteBuf buf) throws IOException {
		this.name = buf.readString(256);
		this.fixedX = buf.readInt();
		this.fixedY = buf.readInt();
		this.fixedZ = buf.readInt();
		this.volume = buf.readFloat();
		this.pitch = buf.readUnsignedByte();
	}

	@Override
	public void write(PacketByteBuf buf) throws IOException {
		buf.writeString(this.name);
		buf.writeInt(this.fixedX);
		buf.writeInt(this.fixedY);
		buf.writeInt(this.fixedZ);
		buf.writeFloat(this.volume);
		buf.writeByte(this.pitch);
	}

	public String getSound() {
		return this.name;
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
