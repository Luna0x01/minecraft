package net.minecraft.datafixers.fixes;

import com.google.common.base.Splitter;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.gson.JsonElement;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.Typed;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.DynamicOps;
import com.mojang.datafixers.types.JsonOps;
import com.mojang.datafixers.types.Type;
import com.mojang.datafixers.util.Pair;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import net.minecraft.datafixers.TypeReferences;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.SystemUtil;

public class LevelDataGeneratorOptionsFix extends DataFix {
	static final Map<String, String> NUMERICAL_IDS_TO_BIOME_IDS = SystemUtil.consume(Maps.newHashMap(), hashMap -> {
		hashMap.put("0", "minecraft:ocean");
		hashMap.put("1", "minecraft:plains");
		hashMap.put("2", "minecraft:desert");
		hashMap.put("3", "minecraft:mountains");
		hashMap.put("4", "minecraft:forest");
		hashMap.put("5", "minecraft:taiga");
		hashMap.put("6", "minecraft:swamp");
		hashMap.put("7", "minecraft:river");
		hashMap.put("8", "minecraft:nether");
		hashMap.put("9", "minecraft:the_end");
		hashMap.put("10", "minecraft:frozen_ocean");
		hashMap.put("11", "minecraft:frozen_river");
		hashMap.put("12", "minecraft:snowy_tundra");
		hashMap.put("13", "minecraft:snowy_mountains");
		hashMap.put("14", "minecraft:mushroom_fields");
		hashMap.put("15", "minecraft:mushroom_field_shore");
		hashMap.put("16", "minecraft:beach");
		hashMap.put("17", "minecraft:desert_hills");
		hashMap.put("18", "minecraft:wooded_hills");
		hashMap.put("19", "minecraft:taiga_hills");
		hashMap.put("20", "minecraft:mountain_edge");
		hashMap.put("21", "minecraft:jungle");
		hashMap.put("22", "minecraft:jungle_hills");
		hashMap.put("23", "minecraft:jungle_edge");
		hashMap.put("24", "minecraft:deep_ocean");
		hashMap.put("25", "minecraft:stone_shore");
		hashMap.put("26", "minecraft:snowy_beach");
		hashMap.put("27", "minecraft:birch_forest");
		hashMap.put("28", "minecraft:birch_forest_hills");
		hashMap.put("29", "minecraft:dark_forest");
		hashMap.put("30", "minecraft:snowy_taiga");
		hashMap.put("31", "minecraft:snowy_taiga_hills");
		hashMap.put("32", "minecraft:giant_tree_taiga");
		hashMap.put("33", "minecraft:giant_tree_taiga_hills");
		hashMap.put("34", "minecraft:wooded_mountains");
		hashMap.put("35", "minecraft:savanna");
		hashMap.put("36", "minecraft:savanna_plateau");
		hashMap.put("37", "minecraft:badlands");
		hashMap.put("38", "minecraft:wooded_badlands_plateau");
		hashMap.put("39", "minecraft:badlands_plateau");
		hashMap.put("40", "minecraft:small_end_islands");
		hashMap.put("41", "minecraft:end_midlands");
		hashMap.put("42", "minecraft:end_highlands");
		hashMap.put("43", "minecraft:end_barrens");
		hashMap.put("44", "minecraft:warm_ocean");
		hashMap.put("45", "minecraft:lukewarm_ocean");
		hashMap.put("46", "minecraft:cold_ocean");
		hashMap.put("47", "minecraft:deep_warm_ocean");
		hashMap.put("48", "minecraft:deep_lukewarm_ocean");
		hashMap.put("49", "minecraft:deep_cold_ocean");
		hashMap.put("50", "minecraft:deep_frozen_ocean");
		hashMap.put("127", "minecraft:the_void");
		hashMap.put("129", "minecraft:sunflower_plains");
		hashMap.put("130", "minecraft:desert_lakes");
		hashMap.put("131", "minecraft:gravelly_mountains");
		hashMap.put("132", "minecraft:flower_forest");
		hashMap.put("133", "minecraft:taiga_mountains");
		hashMap.put("134", "minecraft:swamp_hills");
		hashMap.put("140", "minecraft:ice_spikes");
		hashMap.put("149", "minecraft:modified_jungle");
		hashMap.put("151", "minecraft:modified_jungle_edge");
		hashMap.put("155", "minecraft:tall_birch_forest");
		hashMap.put("156", "minecraft:tall_birch_hills");
		hashMap.put("157", "minecraft:dark_forest_hills");
		hashMap.put("158", "minecraft:snowy_taiga_mountains");
		hashMap.put("160", "minecraft:giant_spruce_taiga");
		hashMap.put("161", "minecraft:giant_spruce_taiga_hills");
		hashMap.put("162", "minecraft:modified_gravelly_mountains");
		hashMap.put("163", "minecraft:shattered_savanna");
		hashMap.put("164", "minecraft:shattered_savanna_plateau");
		hashMap.put("165", "minecraft:eroded_badlands");
		hashMap.put("166", "minecraft:modified_wooded_badlands_plateau");
		hashMap.put("167", "minecraft:modified_badlands_plateau");
	});

	public LevelDataGeneratorOptionsFix(Schema schema, boolean bl) {
		super(schema, bl);
	}

	protected TypeRewriteRule makeRule() {
		Type<?> type = this.getOutputSchema().getType(TypeReferences.LEVEL);
		return this.fixTypeEverywhereTyped("LevelDataGeneratorOptionsFix", this.getInputSchema().getType(TypeReferences.LEVEL), type, typed -> {
			Dynamic<?> dynamic = typed.write();
			Optional<String> optional = dynamic.get("generatorOptions").asString();
			Dynamic<?> dynamic2;
			if ("flat".equalsIgnoreCase(dynamic.get("generatorName").asString(""))) {
				String string = (String)optional.orElse("");
				dynamic2 = dynamic.set("generatorOptions", fixGeneratorOptions(string, dynamic.getOps()));
			} else if ("buffet".equalsIgnoreCase(dynamic.get("generatorName").asString("")) && optional.isPresent()) {
				Dynamic<JsonElement> dynamic3 = new Dynamic(JsonOps.INSTANCE, JsonHelper.deserialize((String)optional.get(), true));
				dynamic2 = dynamic.set("generatorOptions", dynamic3.convert(dynamic.getOps()));
			} else {
				dynamic2 = dynamic;
			}

			return (Typed)((Optional)type.readTyped(dynamic2).getSecond()).orElseThrow(() -> new IllegalStateException("Could not read new level type."));
		});
	}

	private static <T> Dynamic<T> fixGeneratorOptions(String string, DynamicOps<T> dynamicOps) {
		Iterator<String> iterator = Splitter.on(';').split(string).iterator();
		String string2 = "minecraft:plains";
		Map<String, Map<String, String>> map = Maps.newHashMap();
		List<Pair<Integer, String>> list;
		if (!string.isEmpty() && iterator.hasNext()) {
			list = parseFlatLayers((String)iterator.next());
			if (!list.isEmpty()) {
				if (iterator.hasNext()) {
					string2 = (String)NUMERICAL_IDS_TO_BIOME_IDS.getOrDefault(iterator.next(), "minecraft:plains");
				}

				if (iterator.hasNext()) {
					String[] strings = ((String)iterator.next()).toLowerCase(Locale.ROOT).split(",");

					for (String string3 : strings) {
						String[] strings2 = string3.split("\\(", 2);
						if (!strings2[0].isEmpty()) {
							map.put(strings2[0], Maps.newHashMap());
							if (strings2.length > 1 && strings2[1].endsWith(")") && strings2[1].length() > 1) {
								String[] strings3 = strings2[1].substring(0, strings2[1].length() - 1).split(" ");

								for (String string4 : strings3) {
									String[] strings4 = string4.split("=", 2);
									if (strings4.length == 2) {
										((Map)map.get(strings2[0])).put(strings4[0], strings4[1]);
									}
								}
							}
						}
					}
				} else {
					map.put("village", Maps.newHashMap());
				}
			}
		} else {
			list = Lists.newArrayList();
			list.add(Pair.of(1, "minecraft:bedrock"));
			list.add(Pair.of(2, "minecraft:dirt"));
			list.add(Pair.of(1, "minecraft:grass_block"));
			map.put("village", Maps.newHashMap());
		}

		T object = (T)dynamicOps.createList(
			list.stream()
				.map(
					pair -> dynamicOps.createMap(
							ImmutableMap.of(
								dynamicOps.createString("height"),
								dynamicOps.createInt((Integer)pair.getFirst()),
								dynamicOps.createString("block"),
								dynamicOps.createString((String)pair.getSecond())
							)
						)
				)
		);
		T object2 = (T)dynamicOps.createMap(
			(Map)map.entrySet()
				.stream()
				.map(
					entry -> Pair.of(
							dynamicOps.createString(((String)entry.getKey()).toLowerCase(Locale.ROOT)),
							dynamicOps.createMap(
								(Map)((Map)entry.getValue())
									.entrySet()
									.stream()
									.map(entryx -> Pair.of(dynamicOps.createString((String)entryx.getKey()), dynamicOps.createString((String)entryx.getValue())))
									.collect(Collectors.toMap(Pair::getFirst, Pair::getSecond))
							)
						)
				)
				.collect(Collectors.toMap(Pair::getFirst, Pair::getSecond))
		);
		return new Dynamic(
			dynamicOps,
			dynamicOps.createMap(
				ImmutableMap.of(
					dynamicOps.createString("layers"),
					object,
					dynamicOps.createString("biome"),
					dynamicOps.createString(string2),
					dynamicOps.createString("structures"),
					object2
				)
			)
		);
	}

	@Nullable
	private static Pair<Integer, String> parseFlatLayer(String string) {
		String[] strings = string.split("\\*", 2);
		int i;
		if (strings.length == 2) {
			try {
				i = Integer.parseInt(strings[0]);
			} catch (NumberFormatException var4) {
				return null;
			}
		} else {
			i = 1;
		}

		String string2 = strings[strings.length - 1];
		return Pair.of(i, string2);
	}

	private static List<Pair<Integer, String>> parseFlatLayers(String string) {
		List<Pair<Integer, String>> list = Lists.newArrayList();
		String[] strings = string.split(",");

		for (String string2 : strings) {
			Pair<Integer, String> pair = parseFlatLayer(string2);
			if (pair == null) {
				return Collections.emptyList();
			}

			list.add(pair);
		}

		return list;
	}
}
