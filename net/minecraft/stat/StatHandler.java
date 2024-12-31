package net.minecraft.stat;

import com.google.common.collect.Maps;
import java.util.Map;
import net.minecraft.advancement.Achievement;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.JsonElementProvider;
import net.minecraft.util.JsonIntSerializable;

public class StatHandler {
	protected final Map<Stat, JsonIntSerializable> stats = Maps.newConcurrentMap();

	public boolean hasAchievement(Achievement achievement) {
		return this.getStatLevel(achievement) > 0;
	}

	public boolean hasParentAchievement(Achievement achievement) {
		return achievement.parent == null || this.hasAchievement(achievement.parent);
	}

	public int getAchievementDepth(Achievement achievement) {
		if (this.hasAchievement(achievement)) {
			return 0;
		} else {
			int i = 0;

			for (Achievement achievement2 = achievement.parent; achievement2 != null && !this.hasAchievement(achievement2); i++) {
				achievement2 = achievement2.parent;
			}

			return i;
		}
	}

	public void addStatLevel(PlayerEntity player, Stat stat, int amount) {
		if (!stat.isAchievement() || this.hasParentAchievement((Achievement)stat)) {
			this.setStatLevel(player, stat, this.getStatLevel(stat) + amount);
		}
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

	public <T extends JsonElementProvider> T getStat(Stat stat) {
		JsonIntSerializable jsonIntSerializable = (JsonIntSerializable)this.stats.get(stat);
		return jsonIntSerializable != null ? jsonIntSerializable.getJsonElementProvider() : null;
	}

	public <T extends JsonElementProvider> T setStat(Stat stat, T provider) {
		JsonIntSerializable jsonIntSerializable = (JsonIntSerializable)this.stats.get(stat);
		if (jsonIntSerializable == null) {
			jsonIntSerializable = new JsonIntSerializable();
			this.stats.put(stat, jsonIntSerializable);
		}

		jsonIntSerializable.setJsonElementProvider(provider);
		return provider;
	}
}
