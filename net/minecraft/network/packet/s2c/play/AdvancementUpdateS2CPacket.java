package net.minecraft.network.packet.s2c.play;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import java.io.IOException;
import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import net.minecraft.advancement.Advancement;
import net.minecraft.advancement.AdvancementProgress;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.util.Identifier;

public class AdvancementUpdateS2CPacket implements Packet<ClientPlayPacketListener> {
	private boolean clearCurrent;
	private Map<Identifier, Advancement.Task> toEarn;
	private Set<Identifier> toRemove;
	private Map<Identifier, AdvancementProgress> toSetProgress;

	public AdvancementUpdateS2CPacket() {
	}

	public AdvancementUpdateS2CPacket(
		boolean clearCurrent, Collection<Advancement> toEarn, Set<Identifier> toRemove, Map<Identifier, AdvancementProgress> toSetProgress
	) {
		this.clearCurrent = clearCurrent;
		this.toEarn = Maps.newHashMap();

		for (Advancement advancement : toEarn) {
			this.toEarn.put(advancement.getId(), advancement.createTask());
		}

		this.toRemove = toRemove;
		this.toSetProgress = Maps.newHashMap(toSetProgress);
	}

	public void apply(ClientPlayPacketListener clientPlayPacketListener) {
		clientPlayPacketListener.onAdvancements(this);
	}

	@Override
	public void read(PacketByteBuf buf) throws IOException {
		this.clearCurrent = buf.readBoolean();
		this.toEarn = Maps.newHashMap();
		this.toRemove = Sets.newLinkedHashSet();
		this.toSetProgress = Maps.newHashMap();
		int i = buf.readVarInt();

		for (int j = 0; j < i; j++) {
			Identifier identifier = buf.readIdentifier();
			Advancement.Task task = Advancement.Task.fromPacket(buf);
			this.toEarn.put(identifier, task);
		}

		i = buf.readVarInt();

		for (int k = 0; k < i; k++) {
			Identifier identifier2 = buf.readIdentifier();
			this.toRemove.add(identifier2);
		}

		i = buf.readVarInt();

		for (int l = 0; l < i; l++) {
			Identifier identifier3 = buf.readIdentifier();
			this.toSetProgress.put(identifier3, AdvancementProgress.fromPacket(buf));
		}
	}

	@Override
	public void write(PacketByteBuf buf) throws IOException {
		buf.writeBoolean(this.clearCurrent);
		buf.writeVarInt(this.toEarn.size());

		for (Entry<Identifier, Advancement.Task> entry : this.toEarn.entrySet()) {
			Identifier identifier = (Identifier)entry.getKey();
			Advancement.Task task = (Advancement.Task)entry.getValue();
			buf.writeIdentifier(identifier);
			task.toPacket(buf);
		}

		buf.writeVarInt(this.toRemove.size());

		for (Identifier identifier2 : this.toRemove) {
			buf.writeIdentifier(identifier2);
		}

		buf.writeVarInt(this.toSetProgress.size());

		for (Entry<Identifier, AdvancementProgress> entry2 : this.toSetProgress.entrySet()) {
			buf.writeIdentifier((Identifier)entry2.getKey());
			((AdvancementProgress)entry2.getValue()).toPacket(buf);
		}
	}

	public Map<Identifier, Advancement.Task> getAdvancementsToEarn() {
		return this.toEarn;
	}

	public Set<Identifier> getAdvancementIdsToRemove() {
		return this.toRemove;
	}

	public Map<Identifier, AdvancementProgress> getAdvancementsToProgress() {
		return this.toSetProgress;
	}

	public boolean shouldClearCurrent() {
		return this.clearCurrent;
	}
}
