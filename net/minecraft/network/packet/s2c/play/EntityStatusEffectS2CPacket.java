package net.minecraft.network.packet.s2c.play;

import java.io.IOException;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.network.Packet;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.util.PacketByteBuf;

public class EntityStatusEffectS2CPacket implements Packet<ClientPlayPacketListener> {
	private int entityId;
	private byte effectId;
	private byte amplifier;
	private int duration;
	private byte flags;

	public EntityStatusEffectS2CPacket() {
	}

	public EntityStatusEffectS2CPacket(int i, StatusEffectInstance statusEffectInstance) {
		this.entityId = i;
		this.effectId = (byte)(statusEffectInstance.getEffectId() & 0xFF);
		this.amplifier = (byte)(statusEffectInstance.getAmplifier() & 0xFF);
		if (statusEffectInstance.getDuration() > 32767) {
			this.duration = 32767;
		} else {
			this.duration = statusEffectInstance.getDuration();
		}

		this.flags = (byte)(statusEffectInstance.shouldShowParticles() ? 1 : 0);
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
		return this.flags != 0;
	}
}
