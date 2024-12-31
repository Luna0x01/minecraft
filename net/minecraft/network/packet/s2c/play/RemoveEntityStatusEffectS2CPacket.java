package net.minecraft.network.packet.s2c.play;

import java.io.IOException;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.network.Packet;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.util.PacketByteBuf;

public class RemoveEntityStatusEffectS2CPacket implements Packet<ClientPlayPacketListener> {
	private int entityId;
	private int effectId;

	public RemoveEntityStatusEffectS2CPacket() {
	}

	public RemoveEntityStatusEffectS2CPacket(int i, StatusEffectInstance statusEffectInstance) {
		this.entityId = i;
		this.effectId = statusEffectInstance.getEffectId();
	}

	@Override
	public void read(PacketByteBuf buf) throws IOException {
		this.entityId = buf.readVarInt();
		this.effectId = buf.readUnsignedByte();
	}

	@Override
	public void write(PacketByteBuf buf) throws IOException {
		buf.writeVarInt(this.entityId);
		buf.writeByte(this.effectId);
	}

	public void apply(ClientPlayPacketListener clientPlayPacketListener) {
		clientPlayPacketListener.onRemoveEntityEffect(this);
	}

	public int getId() {
		return this.entityId;
	}

	public int getEffectType() {
		return this.effectId;
	}
}
