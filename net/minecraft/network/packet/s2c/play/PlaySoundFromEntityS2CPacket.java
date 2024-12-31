package net.minecraft.network.packet.s2c.play;

import java.io.IOException;
import net.minecraft.entity.Entity;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.registry.Registry;
import org.apache.commons.lang3.Validate;

public class PlaySoundFromEntityS2CPacket implements Packet<ClientPlayPacketListener> {
	private SoundEvent sound;
	private SoundCategory category;
	private int entityId;
	private float volume;
	private float pitch;

	public PlaySoundFromEntityS2CPacket() {
	}

	public PlaySoundFromEntityS2CPacket(SoundEvent sound, SoundCategory category, Entity entity, float volume, float pitch) {
		Validate.notNull(sound, "sound", new Object[0]);
		this.sound = sound;
		this.category = category;
		this.entityId = entity.getEntityId();
		this.volume = volume;
		this.pitch = pitch;
	}

	@Override
	public void read(PacketByteBuf buf) throws IOException {
		this.sound = Registry.SOUND_EVENT.get(buf.readVarInt());
		this.category = buf.readEnumConstant(SoundCategory.class);
		this.entityId = buf.readVarInt();
		this.volume = buf.readFloat();
		this.pitch = buf.readFloat();
	}

	@Override
	public void write(PacketByteBuf buf) throws IOException {
		buf.writeVarInt(Registry.SOUND_EVENT.getRawId(this.sound));
		buf.writeEnumConstant(this.category);
		buf.writeVarInt(this.entityId);
		buf.writeFloat(this.volume);
		buf.writeFloat(this.pitch);
	}

	public SoundEvent getSound() {
		return this.sound;
	}

	public SoundCategory getCategory() {
		return this.category;
	}

	public int getEntityId() {
		return this.entityId;
	}

	public float getVolume() {
		return this.volume;
	}

	public float getPitch() {
		return this.pitch;
	}

	public void apply(ClientPlayPacketListener clientPlayPacketListener) {
		clientPlayPacketListener.onPlaySoundFromEntity(this);
	}
}
