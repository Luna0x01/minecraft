package net.minecraft.client.network.packet;

import java.io.IOException;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.network.Packet;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.util.PacketByteBuf;

public class EntityPotionEffectS2CPacket implements Packet<ClientPlayPacketListener> {
	private int entityId;
	private byte effectId;
	private byte amplifier;
	private int duration;
	private byte flags;

	public EntityPotionEffectS2CPacket() {
	}

	public EntityPotionEffectS2CPacket(int i, StatusEffectInstance statusEffectInstance) {
		this.entityId = i;
		this.effectId = (byte)(StatusEffect.getRawId(statusEffectInstance.getEffectType()) & 0xFF);
		this.amplifier = (byte)(statusEffectInstance.getAmplifier() & 0xFF);
		if (statusEffectInstance.getDuration() > 32767) {
			this.duration = 32767;
		} else {
			this.duration = statusEffectInstance.getDuration();
		}

		this.flags = 0;
		if (statusEffectInstance.isAmbient()) {
			this.flags = (byte)(this.flags | 1);
		}

		if (statusEffectInstance.shouldShowParticles()) {
			this.flags = (byte)(this.flags | 2);
		}

		if (statusEffectInstance.shouldShowIcon()) {
			this.flags = (byte)(this.flags | 4);
		}
	}

	@Override
	public void read(PacketByteBuf packetByteBuf) throws IOException {
		this.entityId = packetByteBuf.readVarInt();
		this.effectId = packetByteBuf.readByte();
		this.amplifier = packetByteBuf.readByte();
		this.duration = packetByteBuf.readVarInt();
		this.flags = packetByteBuf.readByte();
	}

	@Override
	public void write(PacketByteBuf packetByteBuf) throws IOException {
		packetByteBuf.writeVarInt(this.entityId);
		packetByteBuf.writeByte(this.effectId);
		packetByteBuf.writeByte(this.amplifier);
		packetByteBuf.writeVarInt(this.duration);
		packetByteBuf.writeByte(this.flags);
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
