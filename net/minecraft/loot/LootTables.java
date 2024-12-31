package net.minecraft.loot;

import com.google.common.collect.Sets;
import java.util.Collections;
import java.util.Set;
import net.minecraft.util.Identifier;

public class LootTables {
	private static final Set<Identifier> LOOT_TABLES = Sets.newHashSet();
	private static final Set<Identifier> field_13150 = Collections.unmodifiableSet(LOOT_TABLES);
	public static final Identifier EMPTY = register("empty");
	public static final Identifier SPAWN_BONUS_CHEST_CHEST = register("chests/spawn_bonus_chest");
	public static final Identifier END_CITY_TREASURE_CHEST = register("chests/end_city_treasure");
	public static final Identifier SIMPLE_DUNGEON_CHEST = register("chests/simple_dungeon");
	public static final Identifier VILLAGE_BLACKSMITH_CHEST = register("chests/village_blacksmith");
	public static final Identifier ABANDONED_MINESHAFT_CHEST = register("chests/abandoned_mineshaft");
	public static final Identifier NETHER_BRIDGE_CHEST = register("chests/nether_bridge");
	public static final Identifier STRONGHOLD_LIBRARY_CHEST = register("chests/stronghold_library");
	public static final Identifier STRONGHOLD_CROSSING_CHEST = register("chests/stronghold_crossing");
	public static final Identifier STRONGHOLD_CORRIDOR_CHEST = register("chests/stronghold_corridor");
	public static final Identifier DESERT_PYRAMID_CHEST = register("chests/desert_pyramid");
	public static final Identifier JUNGLE_TEMPLE_CHEST = register("chests/jungle_temple");
	public static final Identifier JUNGLE_TEMPLE_DISPENSER_CHEST = register("chests/jungle_temple_dispenser");
	public static final Identifier IGLOO_CHEST_CHEST = register("chests/igloo_chest");
	public static final Identifier WITCH_ENTITIE = register("entities/witch");
	public static final Identifier BLAZE_ENTITIE = register("entities/blaze");
	public static final Identifier CREEPER_ENTITIE = register("entities/creeper");
	public static final Identifier SPIDER_ENTITIE = register("entities/spider");
	public static final Identifier CAVE_SPIDER_ENTITIE = register("entities/cave_spider");
	public static final Identifier GIANT_ENTITIE = register("entities/giant");
	public static final Identifier SILVERFISH_ENTITIE = register("entities/silverfish");
	public static final Identifier ENDERMAN_ENTITIE = register("entities/enderman");
	public static final Identifier GUARDIAN_ENTITIE = register("entities/guardian");
	public static final Identifier ELDER_GUARDIAN_ENTITIE = register("entities/elder_guardian");
	public static final Identifier SHULKER_ENTITIE = register("entities/shulker");
	public static final Identifier IRON_GOLEM_ENTITIE = register("entities/iron_golem");
	public static final Identifier SNOWMAN_ENTITIE = register("entities/snowman");
	public static final Identifier RABBIT_ENTITIE = register("entities/rabbit");
	public static final Identifier CHICKEN_ENTITIE = register("entities/chicken");
	public static final Identifier PIG_ENTITIE = register("entities/pig");
	public static final Identifier POLAR_BEAR_ENTITIE = register("entities/polar_bear");
	public static final Identifier HORSE_ENTITIE = register("entities/horse");
	public static final Identifier ZOMBIE_HORSE_ENTITIE = register("entities/zombie_horse");
	public static final Identifier SKELETON_HORSE_ENTITIE = register("entities/skeleton_horse");
	public static final Identifier COW_ENTITIE = register("entities/cow");
	public static final Identifier MUSHROOM_COW_ENTITIE = register("entities/mushroom_cow");
	public static final Identifier WOLF_ENTITIE = register("entities/wolf");
	public static final Identifier OCELOT_ENTITIE = register("entities/ocelot");
	public static final Identifier SHEEP_ENTITIE = register("entities/sheep");
	public static final Identifier SHEEP_WHITE_ENTITIE = register("entities/sheep/white");
	public static final Identifier SHEEP_ORANGE_ENTITIE = register("entities/sheep/orange");
	public static final Identifier SHEEP_MAGENTA_ENTITIE = register("entities/sheep/magenta");
	public static final Identifier SHEEP_LIGHT_BLUE_ENTITIE = register("entities/sheep/light_blue");
	public static final Identifier SHEEP_YELLOW_ENTITIE = register("entities/sheep/yellow");
	public static final Identifier SHEEP_LIME_ENTITIE = register("entities/sheep/lime");
	public static final Identifier SHEEP_PINK_ENTITIE = register("entities/sheep/pink");
	public static final Identifier SHEEP_GRAY_ENTITIE = register("entities/sheep/gray");
	public static final Identifier SHEEP_SILVER_ENTITIE = register("entities/sheep/silver");
	public static final Identifier SHEEP_CYAN_ENTITIE = register("entities/sheep/cyan");
	public static final Identifier SHEEP_PURPLE_ENTITIE = register("entities/sheep/purple");
	public static final Identifier SHEEP_BLUE_ENTITIE = register("entities/sheep/blue");
	public static final Identifier SHEEP_BROWN_ENTITIE = register("entities/sheep/brown");
	public static final Identifier SHEEP_GREEN_ENTITIE = register("entities/sheep/green");
	public static final Identifier SHEEP_RED_ENTITIE = register("entities/sheep/red");
	public static final Identifier SHEEP_BLACK_ENTITIE = register("entities/sheep/black");
	public static final Identifier BAT_ENTITIE = register("entities/bat");
	public static final Identifier SLIME_ENTITIE = register("entities/slime");
	public static final Identifier MAGMA_CUBE_ENTITIE = register("entities/magma_cube");
	public static final Identifier GHAST_ENTITIE = register("entities/ghast");
	public static final Identifier SQUID_ENTITIE = register("entities/squid");
	public static final Identifier ENDERMITE_ENTITIE = register("entities/endermite");
	public static final Identifier ZOMBIE_ENTITIE = register("entities/zombie");
	public static final Identifier ZOMBIE_PIGMAN_ENTITIE = register("entities/zombie_pigman");
	public static final Identifier SKELETON_ENTITIE = register("entities/skeleton");
	public static final Identifier WITHER_SKELETON_ENTITIE = register("entities/wither_skeleton");
	public static final Identifier STRAY_ENTITIE = register("entities/stray");
	public static final Identifier FISHING_GAMEPLAY = register("gameplay/fishing");
	public static final Identifier FISHING_JUNK_GAMEPLAY = register("gameplay/fishing/junk");
	public static final Identifier FISHING_TREASURE_GAMEPLAY = register("gameplay/fishing/treasure");
	public static final Identifier FISHING_FISH_GAMEPLAY = register("gameplay/fishing/fish");

	private static Identifier register(String id) {
		return registerLootTable(new Identifier("minecraft", id));
	}

	public static Identifier registerLootTable(Identifier id) {
		if (LOOT_TABLES.add(id)) {
			return id;
		} else {
			throw new IllegalArgumentException(id + " is already a registered built-in loot table");
		}
	}

	public static Set<Identifier> method_11961() {
		return field_13150;
	}
}
