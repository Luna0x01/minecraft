package net.minecraft.network.packet.s2c.play;

import java.io.IOException;
import javax.annotation.Nullable;
import net.minecraft.entity.Entity;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.network.Packet;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.util.PacketByteBuf;
import net.minecraft.world.World;

public class RemoveEntityStatusEffectS2CPacket implements Packet<ClientPlayPacketListener> {
	private int entityId;
	private StatusEffect effect;

	public RemoveEntityStatusEffectS2CPacket() {
	}

	public RemoveEntityStatusEffectS2CPacket(int i, StatusEffect statusEffect) {
		this.entityId = i;
		this.effect = statusEffect;
	}

	@Override
	public void read(PacketByteBuf buf) throws IOException {
		this.entityId = buf.readVarInt();
		this.effect = StatusEffect.byIndex(buf.readUnsignedByte());
	}

	@Override
	public void write(PacketByteBuf buf) throws IOException {
		buf.writeVarInt(this.entityId);
		buf.writeByte(StatusEffect.getIndex(this.effect));
	}

	public void apply(ClientPlayPacketListener clientPlayPacketListener) {
		clientPlayPacketListener.onRemoveEntityEffect(this);
	}

	@Nullable
	public Entity getEntity(World world) {
		return world.getEntityById(this.entityId);
	}

	@Nullable
	public StatusEffect getEffect() {
		return this.effect;
	}
}
