package net.minecraft.network.packet.s2c.play;

import java.io.IOException;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.listener.ClientPlayPacketListener;

public class EntityStatusEffectS2CPacket implements Packet<ClientPlayPacketListener> {
	private int entityId;
	private byte effectId;
	private byte amplifier;
	private int duration;
	private byte flags;

	public EntityStatusEffectS2CPacket() {
	}

	public EntityStatusEffectS2CPacket(int entityId, StatusEffectInstance effect) {
		this.entityId = entityId;
		this.effectId = (byte)(StatusEffect.getRawId(effect.getEffectType()) & 0xFF);
		this.amplifier = (byte)(effect.getAmplifier() & 0xFF);
		if (effect.getDuration() > 32767) {
			this.duration = 32767;
		} else {
			this.duration = effect.getDuration();
		}

		this.flags = 0;
		if (effect.isAmbient()) {
			this.flags = (byte)(this.flags | 1);
		}

		if (effect.shouldShowParticles()) {
			this.flags = (byte)(this.flags | 2);
		}

		if (effect.shouldShowIcon()) {
			this.flags = (byte)(this.flags | 4);
		}
	}

	@Override
	public void read(PacketByteBuf buf) throws IOException {
		this.entityId = buf.readVarInt();
		this.effectId = buf.readByte();
		this.amplifier = buf.readByte();
		this.duration = buf.readVarInt();
		this.flags = buf.readByte();
	}

	@Override
	public void write(PacketByteBuf buf) throws IOException {
		buf.writeVarInt(this.entityId);
		buf.writeByte(this.effectId);
		buf.writeByte(this.amplifier);
		buf.writeVarInt(this.duration);
		buf.writeByte(this.flags);
	}

	public boolean isPermanent() {
		return this.duration == 32767;
	}

	public void apply(ClientPlayPacketListener clientPlayPacketListener) {
		clientPlayPacketListener.onEntityPotionEffect(this);
	}

	public int getEntityId() {
		return this.entityId;
	}

	public byte getEffectId() {
		return this.effectId;
	}

	public byte getAmplifier() {
		return this.amplifier;
	}

	public int getDuration() {
		return this.duration;
	}

	public boolean shouldShowParticles() {
		return (this.flags & 2) == 2;
	}

	public boolean isAmbient() {
		return (this.flags & 1) == 1;
	}

	public boolean shouldShowIcon() {
		return (this.flags & 4) == 4;
	}
}
