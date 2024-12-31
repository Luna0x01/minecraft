package net.minecraft.network.packet.s2c.play;

import com.google.common.collect.Maps;
import java.io.IOException;
import java.util.Map;
import java.util.Map.Entry;
import net.minecraft.network.Packet;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.stat.Stat;
import net.minecraft.stat.Stats;
import net.minecraft.util.PacketByteBuf;

public class StatsUpdateS2CPacket implements Packet<ClientPlayPacketListener> {
	private Map<Stat, Integer> stats;

	public StatsUpdateS2CPacket() {
	}

	public StatsUpdateS2CPacket(Map<Stat, Integer> map) {
		this.stats = map;
	}

	public void apply(ClientPlayPacketListener clientPlayPacketListener) {
		clientPlayPacketListener.onStatsUpdate(this);
	}

	@Override
	public void read(PacketByteBuf buf) throws IOException {
		int i = buf.readVarInt();
		this.stats = Maps.newHashMap();

		for (int j = 0; j < i; j++) {
			Stat stat = Stats.getAStat(buf.readString(32767));
			int k = buf.readVarInt();
			if (stat != null) {
				this.stats.put(stat, k);
			}
		}
	}

	@Override
	public void write(PacketByteBuf buf) throws IOException {
		buf.writeVarInt(this.stats.size());

		for (Entry<Stat, Integer> entry : this.stats.entrySet()) {
			buf.writeString(((Stat)entry.getKey()).name);
			buf.writeVarInt((Integer)entry.getValue());
		}
	}

	public Map<Stat, Integer> getStatMap() {
		return this.stats;
	}
}
