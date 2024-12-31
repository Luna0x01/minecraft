package net.minecraft.stat;

import net.minecraft.class_4473;
import net.minecraft.block.Block;
import net.minecraft.entity.EntityType;
import net.minecraft.item.Item;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class Stats {
	public static final StatType<Block> MINED = method_21432("mined", Registry.BLOCK);
	public static final StatType<Item> CRAFTED = method_21432("crafted", Registry.ITEM);
	public static final StatType<Item> USED = method_21432("used", Registry.ITEM);
	public static final StatType<Item> BROKEN = method_21432("broken", Registry.ITEM);
	public static final StatType<Item> PICKED_UP = method_21432("picked_up", Registry.ITEM);
	public static final StatType<Item> DROPPED = method_21432("dropped", Registry.ITEM);
	public static final StatType<EntityType<?>> KILLED = method_21432("killed", Registry.ENTITY_TYPE);
	public static final StatType<EntityType<?>> KILLED_BY = method_21432("killed_by", Registry.ENTITY_TYPE);
	public static final StatType<Identifier> CUSTOM = method_21432("custom", Registry.CUSTOM_STAT);
	public static final Identifier LEAVE_GAME = method_21433("leave_game", class_4473.DEFAULT);
	public static final Identifier PLAY_ONE_MINUTE = method_21433("play_one_minute", class_4473.TIME);
	public static final Identifier TIME_SINCE_DEATH = method_21433("time_since_death", class_4473.TIME);
	public static final Identifier TIME_SINCE_REST = method_21433("time_since_rest", class_4473.TIME);
	public static final Identifier SNEAK_TIME = method_21433("sneak_time", class_4473.TIME);
	public static final Identifier WALK_ONE_CM = method_21433("walk_one_cm", class_4473.DISTANCE);
	public static final Identifier CROUCH_ONE_CM = method_21433("crouch_one_cm", class_4473.DISTANCE);
	public static final Identifier SPRINT_ONE_CM = method_21433("sprint_one_cm", class_4473.DISTANCE);
	public static final Identifier WALK_ON_WATER_ONE_CM = method_21433("walk_on_water_one_cm", class_4473.DISTANCE);
	public static final Identifier FALL_ONE_CM = method_21433("fall_one_cm", class_4473.DISTANCE);
	public static final Identifier CLIMB_ONE_CM = method_21433("climb_one_cm", class_4473.DISTANCE);
	public static final Identifier FLY_ONE_CM = method_21433("fly_one_cm", class_4473.DISTANCE);
	public static final Identifier WALK_UNDER_WATER_ONE_CM = method_21433("walk_under_water_one_cm", class_4473.DISTANCE);
	public static final Identifier MINECART_ONE_CM = method_21433("minecart_one_cm", class_4473.DISTANCE);
	public static final Identifier BOAT_ONE_CM = method_21433("boat_one_cm", class_4473.DISTANCE);
	public static final Identifier PIG_ONE_CM = method_21433("pig_one_cm", class_4473.DISTANCE);
	public static final Identifier HORSE_ONE_CM = method_21433("horse_one_cm", class_4473.DISTANCE);
	public static final Identifier AVIATE_ONE_CM = method_21433("aviate_one_cm", class_4473.DISTANCE);
	public static final Identifier SWIM_ONE_CM = method_21433("swim_one_cm", class_4473.DISTANCE);
	public static final Identifier JUMP = method_21433("jump", class_4473.DEFAULT);
	public static final Identifier DROP = method_21433("drop", class_4473.DEFAULT);
	public static final Identifier DAMAGE_DEALT = method_21433("damage_dealt", class_4473.DIVIDE_BY_TEN);
	public static final Identifier DAMAGE_DEALT_ABSORBED = method_21433("damage_dealt_absorbed", class_4473.DIVIDE_BY_TEN);
	public static final Identifier DAMAGE_DEALT_RESISTED = method_21433("damage_dealt_resisted", class_4473.DIVIDE_BY_TEN);
	public static final Identifier DAMAGE_TAKEN = method_21433("damage_taken", class_4473.DIVIDE_BY_TEN);
	public static final Identifier DAMAGE_BLOCKED_BY_SHIELD = method_21433("damage_blocked_by_shield", class_4473.DIVIDE_BY_TEN);
	public static final Identifier DAMAGE_ABSORBED = method_21433("damage_absorbed", class_4473.DIVIDE_BY_TEN);
	public static final Identifier DAMAGE_RESISTED = method_21433("damage_resisted", class_4473.DIVIDE_BY_TEN);
	public static final Identifier DEATHS = method_21433("deaths", class_4473.DEFAULT);
	public static final Identifier MOB_KILLS = method_21433("mob_kills", class_4473.DEFAULT);
	public static final Identifier ANIMALS_BRED = method_21433("animals_bred", class_4473.DEFAULT);
	public static final Identifier PLAYER_KILLS = method_21433("player_kills", class_4473.DEFAULT);
	public static final Identifier FISH_CAUGHT = method_21433("fish_caught", class_4473.DEFAULT);
	public static final Identifier TALKED_TO_VILLAGER = method_21433("talked_to_villager", class_4473.DEFAULT);
	public static final Identifier TRADED_WITH_VILLAGER = method_21433("traded_with_villager", class_4473.DEFAULT);
	public static final Identifier EAT_CAKE_SLICE = method_21433("eat_cake_slice", class_4473.DEFAULT);
	public static final Identifier FILL_CAULDRON = method_21433("fill_cauldron", class_4473.DEFAULT);
	public static final Identifier USE_CAULDRON = method_21433("use_cauldron", class_4473.DEFAULT);
	public static final Identifier CLEAN_ARMOR = method_21433("clean_armor", class_4473.DEFAULT);
	public static final Identifier CLEAN_BANNER = method_21433("clean_banner", class_4473.DEFAULT);
	public static final Identifier CLEAN_SHULKER_BOX = method_21433("clean_shulker_box", class_4473.DEFAULT);
	public static final Identifier INTERACT_WITH_BREWINGSTAND = method_21433("interact_with_brewingstand", class_4473.DEFAULT);
	public static final Identifier INTERACT_WITH_BEACON = method_21433("interact_with_beacon", class_4473.DEFAULT);
	public static final Identifier INSPECT_DROPPER = method_21433("inspect_dropper", class_4473.DEFAULT);
	public static final Identifier INSPECT_HOPPER = method_21433("inspect_hopper", class_4473.DEFAULT);
	public static final Identifier INSPECT_DISPENSER = method_21433("inspect_dispenser", class_4473.DEFAULT);
	public static final Identifier PLAY_NOTEBLOCK = method_21433("play_noteblock", class_4473.DEFAULT);
	public static final Identifier TUNE_NOTEBLOCK = method_21433("tune_noteblock", class_4473.DEFAULT);
	public static final Identifier POT_FLOWER = method_21433("pot_flower", class_4473.DEFAULT);
	public static final Identifier TRIGGER_TRAPPED_CHEST = method_21433("trigger_trapped_chest", class_4473.DEFAULT);
	public static final Identifier OPEN_ENDERCHEST = method_21433("open_enderchest", class_4473.DEFAULT);
	public static final Identifier ENCHANT_ITEM = method_21433("enchant_item", class_4473.DEFAULT);
	public static final Identifier PLAY_RECORD = method_21433("play_record", class_4473.DEFAULT);
	public static final Identifier INTERACT_WITH_FURNACE = method_21433("interact_with_furnace", class_4473.DEFAULT);
	public static final Identifier INTERACT_WITH_CRAFTING_TABLE = method_21433("interact_with_crafting_table", class_4473.DEFAULT);
	public static final Identifier OPEN_CHEST = method_21433("open_chest", class_4473.DEFAULT);
	public static final Identifier SLEEP_IN_BED = method_21433("sleep_in_bed", class_4473.DEFAULT);
	public static final Identifier OPEN_SHULKER_BOX = method_21433("open_shulker_box", class_4473.DEFAULT);

	public static void method_21431() {
	}

	private static Identifier method_21433(String string, class_4473 arg) {
		Identifier identifier = new Identifier(string);
		Registry.CUSTOM_STAT.add(identifier, identifier);
		CUSTOM.method_21426(identifier, arg);
		return identifier;
	}

	private static <T> StatType<T> method_21432(String string, Registry<T> registry) {
		StatType<T> statType = new StatType<>(registry);
		Registry.STATS.add(new Identifier(string), statType);
		return statType;
	}
}
