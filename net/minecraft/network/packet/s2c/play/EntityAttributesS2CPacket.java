package net.minecraft.network.packet.s2c.play;

import com.google.common.collect.Lists;
import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.UUID;
import net.minecraft.entity.attribute.AttributeModifier;
import net.minecraft.entity.attribute.EntityAttributeInstance;
import net.minecraft.network.Packet;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.util.PacketByteBuf;

public class EntityAttributesS2CPacket implements Packet<ClientPlayPacketListener> {
	private int entityId;
	private final List<EntityAttributesS2CPacket.Entry> entries = Lists.newArrayList();

	public EntityAttributesS2CPacket() {
	}

	public EntityAttributesS2CPacket(int i, Collection<EntityAttributeInstance> collection) {
		this.entityId = i;

		for (EntityAttributeInstance entityAttributeInstance : collection) {
			this.entries
				.add(
					new EntityAttributesS2CPacket.Entry(
						entityAttributeInstance.getAttribute().getId(), entityAttributeInstance.getBaseValue(), entityAttributeInstance.getModifiers()
					)
				);
		}
	}

	@Override
	public void read(PacketByteBuf buf) throws IOException {
		this.entityId = buf.readVarInt();
		int i = buf.readInt();

		for (int j = 0; j < i; j++) {
			String string = buf.readString(64);
			double d = buf.readDouble();
			List<AttributeModifier> list = Lists.newArrayList();
			int k = buf.readVarInt();

			for (int l = 0; l < k; l++) {
				UUID uUID = buf.readUuid();
				list.add(new AttributeModifier(uUID, "Unknown synced attribute modifier", buf.readDouble(), buf.readByte()));
			}

			this.entries.add(new EntityAttributesS2CPacket.Entry(string, d, list));
		}
	}

	@Override
	public void write(PacketByteBuf buf) throws IOException {
		buf.writeVarInt(this.entityId);
		buf.writeInt(this.entries.size());

		for (EntityAttributesS2CPacket.Entry entry : this.entries) {
			buf.writeString(entry.getId());
			buf.writeDouble(entry.getBaseValue());
			buf.writeVarInt(entry.getModifiers().size());

			for (AttributeModifier attributeModifier : entry.getModifiers()) {
				buf.writeUUID(attributeModifier.getId());
				buf.writeDouble(attributeModifier.getAmount());
				buf.writeByte(attributeModifier.getOperation());
			}
		}
	}

	public void apply(ClientPlayPacketListener clientPlayPacketListener) {
		clientPlayPacketListener.onEntityAttributes(this);
	}

	public int getEntityId() {
		return this.entityId;
	}

	public List<EntityAttributesS2CPacket.Entry> getEntries() {
		return this.entries;
	}

	public class Entry {
		private final String id;
		private final double baseValue;
		private final Collection<AttributeModifier> modifiers;

		public Entry(String string, double d, Collection<AttributeModifier> collection) {
			this.id = string;
			this.baseValue = d;
			this.modifiers = collection;
		}

		public String getId() {
			return this.id;
		}

		public double getBaseValue() {
			return this.baseValue;
		}

		public Collection<AttributeModifier> getModifiers() {
			return this.modifiers;
		}
	}
}
