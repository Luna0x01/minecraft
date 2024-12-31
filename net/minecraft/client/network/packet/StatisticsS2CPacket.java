package net.minecraft.client.network.packet;

import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import it.unimi.dsi.fastutil.objects.Object2IntMap.Entry;
import java.io.IOException;
import java.util.Map;
import net.minecraft.network.Packet;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.stat.Stat;
import net.minecraft.stat.StatType;
import net.minecraft.util.PacketByteBuf;
import net.minecraft.util.registry.Registry;

public class StatisticsS2CPacket implements Packet<ClientPlayPacketListener> {
	private Object2IntMap<Stat<?>> stats;

	public StatisticsS2CPacket() {
	}

	public StatisticsS2CPacket(Object2IntMap<Stat<?>> object2IntMap) {
		this.stats = object2IntMap;
	}

	public void apply(ClientPlayPacketListener clientPlayPacketListener) {
		clientPlayPacketListener.onStatistics(this);
	}

	@Override
	public void read(PacketByteBuf packetByteBuf) throws IOException {
		int i = packetByteBuf.readVarInt();
		this.stats = new Object2IntOpenHashMap(i);

		for (int j = 0; j < i; j++) {
			this.readStat(Registry.field_11152.get(packetByteBuf.readVarInt()), packetByteBuf);
		}
	}

	private <T> void readStat(StatType<T> statType, PacketByteBuf packetByteBuf) {
		int i = packetByteBuf.readVarInt();
		int j = packetByteBuf.readVarInt();
		this.stats.put(statType.getOrCreateStat(statType.getRegistry().get(i)), j);
	}

	@Override
	public void write(PacketByteBuf packetByteBuf) throws IOException {
		packetByteBuf.writeVarInt(this.stats.size());
		ObjectIterator var2 = this.stats.object2IntEntrySet().iterator();

		while (var2.hasNext()) {
			Entry<Stat<?>> entry = (Entry<Stat<?>>)var2.next();
			Stat<?> stat = (Stat<?>)entry.getKey();
			packetByteBuf.writeVarInt(Registry.field_11152.getRawId(stat.getType()));
			packetByteBuf.writeVarInt(this.getStatId(stat));
			packetByteBuf.writeVarInt(entry.getIntValue());
		}
	}

	private <T> int getStatId(Stat<T> stat) {
		return stat.getType().getRegistry().getRawId(stat.getValue());
	}

	public Map<Stat<?>, Integer> getStatMap() {
		return this.stats;
	}
}
