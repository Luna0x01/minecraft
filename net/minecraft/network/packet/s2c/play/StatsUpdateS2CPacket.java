package net.minecraft.network.packet.s2c.play;

import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import it.unimi.dsi.fastutil.objects.Object2IntMap.Entry;
import java.io.IOException;
import java.util.Map;
import net.minecraft.class_4472;
import net.minecraft.network.Packet;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.stat.StatType;
import net.minecraft.util.PacketByteBuf;
import net.minecraft.util.registry.Registry;

public class StatsUpdateS2CPacket implements Packet<ClientPlayPacketListener> {
	private Object2IntMap<class_4472<?>> field_21522;

	public StatsUpdateS2CPacket() {
	}

	public StatsUpdateS2CPacket(Object2IntMap<class_4472<?>> object2IntMap) {
		this.field_21522 = object2IntMap;
	}

	public void apply(ClientPlayPacketListener clientPlayPacketListener) {
		clientPlayPacketListener.onStatsUpdate(this);
	}

	@Override
	public void read(PacketByteBuf buf) throws IOException {
		int i = buf.readVarInt();
		this.field_21522 = new Object2IntOpenHashMap(i);

		for (int j = 0; j < i; j++) {
			this.method_20207(Registry.STATS.getByRawId(buf.readVarInt()), buf);
		}
	}

	private <T> void method_20207(StatType<T> statType, PacketByteBuf packetByteBuf) {
		int i = packetByteBuf.readVarInt();
		int j = packetByteBuf.readVarInt();
		this.field_21522.put(statType.method_21429(statType.method_21424().getByRawId(i)), j);
	}

	@Override
	public void write(PacketByteBuf buf) throws IOException {
		buf.writeVarInt(this.field_21522.size());
		ObjectIterator var2 = this.field_21522.object2IntEntrySet().iterator();

		while (var2.hasNext()) {
			Entry<class_4472<?>> entry = (Entry<class_4472<?>>)var2.next();
			class_4472<?> lv = (class_4472<?>)entry.getKey();
			buf.writeVarInt(Registry.STATS.getRawId(lv.method_21419()));
			buf.writeVarInt(this.method_20206(lv));
			buf.writeVarInt(entry.getIntValue());
		}
	}

	private <T> int method_20206(class_4472<T> arg) {
		return arg.method_21419().method_21424().getRawId(arg.method_21423());
	}

	public Map<class_4472<?>, Integer> getStatMap() {
		return this.field_21522;
	}
}
