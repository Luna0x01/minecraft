package net.minecraft.network.packet.c2s.play;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import java.io.IOException;
import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import net.minecraft.advancement.AdvancementProgress;
import net.minecraft.advancement.SimpleAdvancement;
import net.minecraft.network.Packet;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.util.Identifier;
import net.minecraft.util.PacketByteBuf;

public class AdvancementUpdatePacket implements Packet<ClientPlayPacketListener> {
	private boolean field_16299;
	private Map<Identifier, SimpleAdvancement.TaskAdvancement> tasks;
	private Set<Identifier> updatedAdvancements;
	private Map<Identifier, AdvancementProgress> progresses;

	public AdvancementUpdatePacket() {
	}

	public AdvancementUpdatePacket(boolean bl, Collection<SimpleAdvancement> collection, Set<Identifier> set, Map<Identifier, AdvancementProgress> map) {
		this.field_16299 = bl;
		this.tasks = Maps.newHashMap();

		for (SimpleAdvancement simpleAdvancement : collection) {
			this.tasks.put(simpleAdvancement.getIdentifier(), simpleAdvancement.asTaskAdvancement());
		}

		this.updatedAdvancements = set;
		this.progresses = Maps.newHashMap(map);
	}

	public void apply(ClientPlayPacketListener clientPlayPacketListener) {
		clientPlayPacketListener.onAdvancementsUpdate(this);
	}

	@Override
	public void read(PacketByteBuf buf) throws IOException {
		this.field_16299 = buf.readBoolean();
		this.tasks = Maps.newHashMap();
		this.updatedAdvancements = Sets.newLinkedHashSet();
		this.progresses = Maps.newHashMap();
		int i = buf.readVarInt();

		for (int j = 0; j < i; j++) {
			Identifier identifier = buf.readIdentifier();
			SimpleAdvancement.TaskAdvancement taskAdvancement = SimpleAdvancement.TaskAdvancement.fromPacketByteBuf(buf);
			this.tasks.put(identifier, taskAdvancement);
		}

		i = buf.readVarInt();

		for (int k = 0; k < i; k++) {
			Identifier identifier2 = buf.readIdentifier();
			this.updatedAdvancements.add(identifier2);
		}

		i = buf.readVarInt();

		for (int l = 0; l < i; l++) {
			Identifier identifier3 = buf.readIdentifier();
			this.progresses.put(identifier3, AdvancementProgress.fromPacketByteBuf(buf));
		}
	}

	@Override
	public void write(PacketByteBuf buf) throws IOException {
		buf.writeBoolean(this.field_16299);
		buf.writeVarInt(this.tasks.size());

		for (Entry<Identifier, SimpleAdvancement.TaskAdvancement> entry : this.tasks.entrySet()) {
			Identifier identifier = (Identifier)entry.getKey();
			SimpleAdvancement.TaskAdvancement taskAdvancement = (SimpleAdvancement.TaskAdvancement)entry.getValue();
			buf.writeIdentifier(identifier);
			taskAdvancement.writeToByteBuf(buf);
		}

		buf.writeVarInt(this.updatedAdvancements.size());

		for (Identifier identifier2 : this.updatedAdvancements) {
			buf.writeIdentifier(identifier2);
		}

		buf.writeVarInt(this.progresses.size());

		for (Entry<Identifier, AdvancementProgress> entry2 : this.progresses.entrySet()) {
			buf.writeIdentifier((Identifier)entry2.getKey());
			((AdvancementProgress)entry2.getValue()).writeToByteBuf(buf);
		}
	}

	public Map<Identifier, SimpleAdvancement.TaskAdvancement> getTasks() {
		return this.tasks;
	}

	public Set<Identifier> getUpdatedAdvancementIdentifiers() {
		return this.updatedAdvancements;
	}

	public Map<Identifier, AdvancementProgress> getAdvancementProgresses() {
		return this.progresses;
	}

	public boolean method_14855() {
		return this.field_16299;
	}
}
