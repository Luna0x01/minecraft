package net.minecraft.stat;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.internal.Streams;
import com.google.gson.stream.JsonReader;
import com.mojang.datafixers.DataFixTypes;
import com.mojang.datafixers.DataFixer;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import javax.annotation.Nullable;
import net.minecraft.class_4472;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.network.packet.s2c.play.StatsUpdateS2CPacket;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ServerStatHandler extends StatHandler {
	private static final Logger LOGGER = LogManager.getLogger();
	private final MinecraftServer server;
	private final File file;
	private final Set<class_4472<?>> pendingStats = Sets.newHashSet();
	private int lastStatsUpdate = -300;

	public ServerStatHandler(MinecraftServer minecraftServer, File file) {
		this.server = minecraftServer;
		this.file = file;
		if (file.isFile()) {
			try {
				this.method_21414(minecraftServer.method_20343(), FileUtils.readFileToString(file));
			} catch (IOException var4) {
				LOGGER.error("Couldn't read statistics file {}", file, var4);
			} catch (JsonParseException var5) {
				LOGGER.error("Couldn't parse statistics file {}", file, var5);
			}
		}
	}

	public void save() {
		try {
			FileUtils.writeStringToFile(this.file, this.method_21417());
		} catch (IOException var2) {
			LOGGER.error("Couldn't save stats", var2);
		}
	}

	@Override
	public void method_8300(PlayerEntity playerEntity, class_4472<?> arg, int i) {
		super.method_8300(playerEntity, arg, i);
		this.pendingStats.add(arg);
	}

	private Set<class_4472<?>> takePendingStats() {
		Set<class_4472<?>> set = Sets.newHashSet(this.pendingStats);
		this.pendingStats.clear();
		return set;
	}

	public void method_21414(DataFixer dataFixer, String string) {
		try {
			JsonReader jsonReader = new JsonReader(new StringReader(string));
			Throwable var4 = null;

			try {
				jsonReader.setLenient(false);
				JsonElement jsonElement = Streams.parse(jsonReader);
				if (!jsonElement.isJsonNull()) {
					NbtCompound nbtCompound = method_21413(jsonElement.getAsJsonObject());
					if (!nbtCompound.contains("DataVersion", 99)) {
						nbtCompound.putInt("DataVersion", 1343);
					}

					nbtCompound = NbtHelper.method_20141(dataFixer, DataFixTypes.STATS, nbtCompound, nbtCompound.getInt("DataVersion"));
					if (nbtCompound.contains("stats", 10)) {
						NbtCompound nbtCompound2 = nbtCompound.getCompound("stats");

						for (String string2 : nbtCompound2.getKeys()) {
							if (nbtCompound2.contains(string2, 10)) {
								StatType<?> statType = Registry.STATS.getByIdentifier(new Identifier(string2));
								if (statType == null) {
									LOGGER.warn("Invalid statistic type in {}: Don't know what {} is", this.file, string2);
								} else {
									NbtCompound nbtCompound3 = nbtCompound2.getCompound(string2);

									for (String string3 : nbtCompound3.getKeys()) {
										if (nbtCompound3.contains(string3, 99)) {
											class_4472<?> lv = this.method_21416(statType, string3);
											if (lv == null) {
												LOGGER.warn("Invalid statistic in {}: Don't know what {} is", this.file, string3);
											} else {
												this.field_22141.put(lv, nbtCompound3.getInt(string3));
											}
										} else {
											LOGGER.warn("Invalid statistic value in {}: Don't know what {} is for key {}", this.file, nbtCompound3.get(string3), string3);
										}
									}
								}
							}
						}
					}
				} else {
					LOGGER.error("Unable to parse Stat data from {}", this.file);
				}
			} catch (Throwable var23) {
				var4 = var23;
				throw var23;
			} finally {
				if (jsonReader != null) {
					if (var4 != null) {
						try {
							jsonReader.close();
						} catch (Throwable var22) {
							var4.addSuppressed(var22);
						}
					} else {
						jsonReader.close();
					}
				}
			}
		} catch (IOException | JsonParseException var25) {
			LOGGER.error("Unable to parse Stat data from {}", this.file, var25);
		}
	}

	@Nullable
	private <T> class_4472<T> method_21416(StatType<T> statType, String string) {
		Identifier identifier = Identifier.fromString(string);
		if (identifier == null) {
			return null;
		} else {
			T object = statType.method_21424().getByIdentifier(identifier);
			return object == null ? null : statType.method_21429(object);
		}
	}

	private static NbtCompound method_21413(JsonObject jsonObject) {
		NbtCompound nbtCompound = new NbtCompound();

		for (Entry<String, JsonElement> entry : jsonObject.entrySet()) {
			JsonElement jsonElement = (JsonElement)entry.getValue();
			if (jsonElement.isJsonObject()) {
				nbtCompound.put((String)entry.getKey(), method_21413(jsonElement.getAsJsonObject()));
			} else if (jsonElement.isJsonPrimitive()) {
				JsonPrimitive jsonPrimitive = jsonElement.getAsJsonPrimitive();
				if (jsonPrimitive.isNumber()) {
					nbtCompound.putInt((String)entry.getKey(), jsonPrimitive.getAsInt());
				}
			}
		}

		return nbtCompound;
	}

	protected String method_21417() {
		Map<StatType<?>, JsonObject> map = Maps.newHashMap();
		ObjectIterator jsonObject = this.field_22141.object2IntEntrySet().iterator();

		while (jsonObject.hasNext()) {
			it.unimi.dsi.fastutil.objects.Object2IntMap.Entry<class_4472<?>> entry = (it.unimi.dsi.fastutil.objects.Object2IntMap.Entry<class_4472<?>>)jsonObject.next();
			class_4472<?> lv = (class_4472<?>)entry.getKey();
			((JsonObject)map.computeIfAbsent(lv.method_21419(), statType -> new JsonObject())).addProperty(method_21418(lv).toString(), entry.getIntValue());
		}

		JsonObject jsonObjectx = new JsonObject();

		for (Entry<StatType<?>, JsonObject> entry2 : map.entrySet()) {
			jsonObjectx.add(Registry.STATS.getId((StatType<?>)entry2.getKey()).toString(), (JsonElement)entry2.getValue());
		}

		JsonObject jsonObject2 = new JsonObject();
		jsonObject2.add("stats", jsonObjectx);
		jsonObject2.addProperty("DataVersion", 1631);
		return jsonObject2.toString();
	}

	private static <T> Identifier method_21418(class_4472<T> arg) {
		return arg.method_21419().method_21424().getId(arg.method_21423());
	}

	public void updateStatSet() {
		this.pendingStats.addAll(this.field_22141.keySet());
	}

	public void method_8273(ServerPlayerEntity serverPlayerEntity) {
		int i = this.server.getTicks();
		Object2IntMap<class_4472<?>> object2IntMap = new Object2IntOpenHashMap();
		if (i - this.lastStatsUpdate > 300) {
			this.lastStatsUpdate = i;

			for (class_4472<?> lv : this.takePendingStats()) {
				object2IntMap.put(lv, this.method_21434(lv));
			}
		}

		serverPlayerEntity.networkHandler.sendPacket(new StatsUpdateS2CPacket(object2IntMap));
	}
}
