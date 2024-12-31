package net.minecraft.stat;

import com.google.common.collect.Maps;
import java.util.Map;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.JsonIntSerializable;

public class StatHandler {
	protected final Map<Stat, JsonIntSerializable> stats = Maps.newConcurrentMap();

	public void addStatLevel(PlayerEntity player, Stat stat, int amount) {
		this.setStatLevel(player, stat, this.getStatLevel(stat) + amount);
	}

	public void setStatLevel(PlayerEntity player, Stat stat, int amount) {
		JsonIntSerializable jsonIntSerializable = (JsonIntSerializable)this.stats.get(stat);
		if (jsonIntSerializable == null) {
			jsonIntSerializable = new JsonIntSerializable();
			this.stats.put(stat, jsonIntSerializable);
		}

		jsonIntSerializable.setValue(amount);
	}

	public int getStatLevel(Stat stat) {
		JsonIntSerializable jsonIntSerializable = (JsonIntSerializable)this.stats.get(stat);
		return jsonIntSerializable == null ? 0 : jsonIntSerializable.getValue();
	}
}
