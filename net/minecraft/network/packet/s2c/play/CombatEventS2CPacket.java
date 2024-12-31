package net.minecraft.network.packet.s2c.play;

import java.io.IOException;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageTracker;
import net.minecraft.network.Packet;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.util.PacketByteBuf;

public class CombatEventS2CPacket implements Packet<ClientPlayPacketListener> {
	public CombatEventS2CPacket.Type type;
	public int entityId;
	public int attackerEntityId;
	public int timeSinceLastAttack;
	public String deathMessage;

	public CombatEventS2CPacket() {
	}

	public CombatEventS2CPacket(DamageTracker damageTracker, CombatEventS2CPacket.Type type) {
		this.type = type;
		LivingEntity livingEntity = damageTracker.getLastAttacker();
		switch (type) {
			case END_COMBAT:
				this.timeSinceLastAttack = damageTracker.getTimeSinceLastAttack();
				this.attackerEntityId = livingEntity == null ? -1 : livingEntity.getEntityId();
				break;
			case ENTITY_DIED:
				this.entityId = damageTracker.getEntity().getEntityId();
				this.attackerEntityId = livingEntity == null ? -1 : livingEntity.getEntityId();
				this.deathMessage = damageTracker.getDeathMessage().asUnformattedString();
		}
	}

	@Override
	public void read(PacketByteBuf buf) throws IOException {
		this.type = buf.readEnumConstant(CombatEventS2CPacket.Type.class);
		if (this.type == CombatEventS2CPacket.Type.END_COMBAT) {
			this.timeSinceLastAttack = buf.readVarInt();
			this.attackerEntityId = buf.readInt();
		} else if (this.type == CombatEventS2CPacket.Type.ENTITY_DIED) {
			this.entityId = buf.readVarInt();
			this.attackerEntityId = buf.readInt();
			this.deathMessage = buf.readString(32767);
		}
	}

	@Override
	public void write(PacketByteBuf buf) throws IOException {
		buf.writeEnum(this.type);
		if (this.type == CombatEventS2CPacket.Type.END_COMBAT) {
			buf.writeVarInt(this.timeSinceLastAttack);
			buf.writeInt(this.attackerEntityId);
		} else if (this.type == CombatEventS2CPacket.Type.ENTITY_DIED) {
			buf.writeVarInt(this.entityId);
			buf.writeInt(this.attackerEntityId);
			buf.writeString(this.deathMessage);
		}
	}

	public void apply(ClientPlayPacketListener clientPlayPacketListener) {
		clientPlayPacketListener.onCombatEvent(this);
	}

	public static enum Type {
		ENTER_COMBAT,
		END_COMBAT,
		ENTITY_DIED;
	}
}
