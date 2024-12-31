package net.minecraft.stat;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import net.minecraft.advancement.Achievement;
import net.minecraft.advancement.AchievementsAndCriterions;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.packet.s2c.play.StatsUpdateS2CPacket;
import net.minecraft.server.MinecraftServer;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.JsonElementProvider;
import net.minecraft.util.JsonIntSerializable;
import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ServerStatHandler extends StatHandler {
	private static final Logger LOGGER = LogManager.getLogger();
	private final MinecraftServer server;
	private final File file;
	private final Set<Stat> pendingStats = Sets.newHashSet();
	private int lastStatsUpdate = -300;
	private boolean field_9036 = false;

	public ServerStatHandler(MinecraftServer minecraftServer, File file) {
		this.server = minecraftServer;
		this.file = file;
	}

	public void method_8270() {
		if (this.file.isFile()) {
			try {
				this.stats.clear();
				this.stats.putAll(this.method_8271(FileUtils.readFileToString(this.file)));
			} catch (IOException var2) {
				LOGGER.error("Couldn't read statistics file " + this.file, var2);
			} catch (JsonParseException var3) {
				LOGGER.error("Couldn't parse statistics file " + this.file, var3);
			}
		}
	}

	public void save() {
		try {
			FileUtils.writeStringToFile(this.file, method_8272(this.stats));
		} catch (IOException var2) {
			LOGGER.error("Couldn't save stats", var2);
		}
	}

	@Override
	public void setStatLevel(PlayerEntity player, Stat stat, int amount) {
		int i = stat.isAchievement() ? this.getStatLevel(stat) : 0;
		super.setStatLevel(player, stat, amount);
		this.pendingStats.add(stat);
		if (stat.isAchievement() && i == 0 && amount > 0) {
			this.field_9036 = true;
			if (this.server.shouldAnnouncePlayerAchievements()) {
				this.server.getPlayerManager().sendToAll(new TranslatableText("chat.type.achievement", player.getName(), stat.method_8281()));
			}
		}

		if (stat.isAchievement() && i > 0 && amount == 0) {
			this.field_9036 = true;
			if (this.server.shouldAnnouncePlayerAchievements()) {
				this.server.getPlayerManager().sendToAll(new TranslatableText("chat.type.achievement.taken", player.getName(), stat.method_8281()));
			}
		}
	}

	public Set<Stat> takePendingStats() {
		Set<Stat> set = Sets.newHashSet(this.pendingStats);
		this.pendingStats.clear();
		this.field_9036 = false;
		return set;
	}

	public Map<Stat, JsonIntSerializable> method_8271(String string) {
		JsonElement jsonElement = new JsonParser().parse(string);
		if (!jsonElement.isJsonObject()) {
			return Maps.newHashMap();
		} else {
			JsonObject jsonObject = jsonElement.getAsJsonObject();
			Map<Stat, JsonIntSerializable> map = Maps.newHashMap();

			for (Entry<String, JsonElement> entry : jsonObject.entrySet()) {
				Stat stat = Stats.getAStat((String)entry.getKey());
				if (stat != null) {
					JsonIntSerializable jsonIntSerializable = new JsonIntSerializable();
					if (((JsonElement)entry.getValue()).isJsonPrimitive() && ((JsonElement)entry.getValue()).getAsJsonPrimitive().isNumber()) {
						jsonIntSerializable.setValue(((JsonElement)entry.getValue()).getAsInt());
					} else if (((JsonElement)entry.getValue()).isJsonObject()) {
						JsonObject jsonObject2 = ((JsonElement)entry.getValue()).getAsJsonObject();
						if (jsonObject2.has("value") && jsonObject2.get("value").isJsonPrimitive() && jsonObject2.get("value").getAsJsonPrimitive().isNumber()) {
							jsonIntSerializable.setValue(jsonObject2.getAsJsonPrimitive("value").getAsInt());
						}

						if (jsonObject2.has("progress") && stat.getJsonElementProvider() != null) {
							try {
								Constructor<? extends JsonElementProvider> constructor = stat.getJsonElementProvider().getConstructor();
								JsonElementProvider jsonElementProvider = (JsonElementProvider)constructor.newInstance();
								jsonElementProvider.read(jsonObject2.get("progress"));
								jsonIntSerializable.setJsonElementProvider(jsonElementProvider);
							} catch (Throwable var12) {
								LOGGER.warn("Invalid statistic progress in " + this.file, var12);
							}
						}
					}

					map.put(stat, jsonIntSerializable);
				} else {
					LOGGER.warn("Invalid statistic in " + this.file + ": Don't know what " + (String)entry.getKey() + " is");
				}
			}

			return map;
		}
	}

	public static String method_8272(Map<Stat, JsonIntSerializable> map) {
		JsonObject jsonObject = new JsonObject();

		for (Entry<Stat, JsonIntSerializable> entry : map.entrySet()) {
			if (((JsonIntSerializable)entry.getValue()).getJsonElementProvider() != null) {
				JsonObject jsonObject2 = new JsonObject();
				jsonObject2.addProperty("value", ((JsonIntSerializable)entry.getValue()).getValue());

				try {
					jsonObject2.add("progress", ((JsonIntSerializable)entry.getValue()).<JsonElementProvider>getJsonElementProvider().write());
				} catch (Throwable var6) {
					LOGGER.warn("Couldn't save statistic " + ((Stat)entry.getKey()).getText() + ": error serializing progress", var6);
				}

				jsonObject.add(((Stat)entry.getKey()).name, jsonObject2);
			} else {
				jsonObject.addProperty(((Stat)entry.getKey()).name, ((JsonIntSerializable)entry.getValue()).getValue());
			}
		}

		return jsonObject.toString();
	}

	public void updateStatSet() {
		for (Stat stat : this.stats.keySet()) {
			this.pendingStats.add(stat);
		}
	}

	public void method_8273(ServerPlayerEntity serverPlayerEntity) {
		int i = this.server.getTicks();
		Map<Stat, Integer> map = Maps.newHashMap();
		if (this.field_9036 || i - this.lastStatsUpdate > 300) {
			this.lastStatsUpdate = i;

			for (Stat stat : this.takePendingStats()) {
				map.put(stat, this.getStatLevel(stat));
			}
		}

		serverPlayerEntity.networkHandler.sendPacket(new StatsUpdateS2CPacket(map));
	}

	public void method_8275(ServerPlayerEntity serverPlayerEntity) {
		Map<Stat, Integer> map = Maps.newHashMap();

		for (Achievement achievement : AchievementsAndCriterions.ACHIEVEMENTS) {
			if (this.hasAchievement(achievement)) {
				map.put(achievement, this.getStatLevel(achievement));
				this.pendingStats.remove(achievement);
			}
		}

		serverPlayerEntity.networkHandler.sendPacket(new StatsUpdateS2CPacket(map));
	}

	public boolean method_8278() {
		return this.field_9036;
	}
}
