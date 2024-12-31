package net.minecraft.datafixer.schema;

import com.google.common.collect.Maps;
import java.util.Map;
import javax.annotation.Nullable;
import net.minecraft.datafixer.DataFixer;
import net.minecraft.datafixer.Schema;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.Identifier;
import net.minecraft.world.level.storage.LevelDataType;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class BlockEntitySchema implements Schema {
	private static final Logger LOGGER = LogManager.getLogger();
	private static final Map<String, String> ID_MAP_pre515 = Maps.newHashMap();
	private static final Map<String, String> ID_MAP = Maps.newHashMap();

	@Nullable
	private static String getBlockEntityId(int dataVersion, String id) {
		return dataVersion < 515 ? (String)ID_MAP_pre515.get(new Identifier(id).toString()) : (String)ID_MAP.get(new Identifier(id).toString());
	}

	@Override
	public NbtCompound fixData(DataFixer dataFixer, NbtCompound tag, int dataVersion) {
		if (!tag.contains("tag", 10)) {
			return tag;
		} else {
			NbtCompound nbtCompound = tag.getCompound("tag");
			if (nbtCompound.contains("BlockEntityTag", 10)) {
				NbtCompound nbtCompound2 = nbtCompound.getCompound("BlockEntityTag");
				String string = tag.getString("id");
				String string2 = getBlockEntityId(dataVersion, string);
				boolean bl;
				if (string2 == null) {
					LOGGER.warn("Unable to resolve BlockEntity for ItemInstance: {}", string);
					bl = false;
				} else {
					bl = !nbtCompound2.contains("id");
					nbtCompound2.putString("id", string2);
				}

				dataFixer.update(LevelDataType.BLOCK_ENTITY, nbtCompound2, dataVersion);
				if (bl) {
					nbtCompound2.remove("id");
				}
			}

			return tag;
		}
	}

	static {
		Map<String, String> map = ID_MAP_pre515;
		map.put("minecraft:furnace", "Furnace");
		map.put("minecraft:lit_furnace", "Furnace");
		map.put("minecraft:chest", "Chest");
		map.put("minecraft:trapped_chest", "Chest");
		map.put("minecraft:ender_chest", "EnderChest");
		map.put("minecraft:jukebox", "RecordPlayer");
		map.put("minecraft:dispenser", "Trap");
		map.put("minecraft:dropper", "Dropper");
		map.put("minecraft:sign", "Sign");
		map.put("minecraft:mob_spawner", "MobSpawner");
		map.put("minecraft:noteblock", "Music");
		map.put("minecraft:brewing_stand", "Cauldron");
		map.put("minecraft:enhanting_table", "EnchantTable");
		map.put("minecraft:command_block", "CommandBlock");
		map.put("minecraft:beacon", "Beacon");
		map.put("minecraft:skull", "Skull");
		map.put("minecraft:daylight_detector", "DLDetector");
		map.put("minecraft:hopper", "Hopper");
		map.put("minecraft:banner", "Banner");
		map.put("minecraft:flower_pot", "FlowerPot");
		map.put("minecraft:repeating_command_block", "CommandBlock");
		map.put("minecraft:chain_command_block", "CommandBlock");
		map.put("minecraft:standing_sign", "Sign");
		map.put("minecraft:wall_sign", "Sign");
		map.put("minecraft:piston_head", "Piston");
		map.put("minecraft:daylight_detector_inverted", "DLDetector");
		map.put("minecraft:unpowered_comparator", "Comparator");
		map.put("minecraft:powered_comparator", "Comparator");
		map.put("minecraft:wall_banner", "Banner");
		map.put("minecraft:standing_banner", "Banner");
		map.put("minecraft:structure_block", "Structure");
		map.put("minecraft:end_portal", "Airportal");
		map.put("minecraft:end_gateway", "EndGateway");
		map.put("minecraft:shield", "Shield");
		map = ID_MAP;
		map.put("minecraft:furnace", "minecraft:furnace");
		map.put("minecraft:lit_furnace", "minecraft:furnace");
		map.put("minecraft:chest", "minecraft:chest");
		map.put("minecraft:trapped_chest", "minecraft:chest");
		map.put("minecraft:ender_chest", "minecraft:enderchest");
		map.put("minecraft:jukebox", "minecraft:jukebox");
		map.put("minecraft:dispenser", "minecraft:dispenser");
		map.put("minecraft:dropper", "minecraft:dropper");
		map.put("minecraft:sign", "minecraft:sign");
		map.put("minecraft:mob_spawner", "minecraft:mob_spawner");
		map.put("minecraft:noteblock", "minecraft:noteblock");
		map.put("minecraft:brewing_stand", "minecraft:brewing_stand");
		map.put("minecraft:enhanting_table", "minecraft:enchanting_table");
		map.put("minecraft:command_block", "minecraft:command_block");
		map.put("minecraft:beacon", "minecraft:beacon");
		map.put("minecraft:skull", "minecraft:skull");
		map.put("minecraft:daylight_detector", "minecraft:daylight_detector");
		map.put("minecraft:hopper", "minecraft:hopper");
		map.put("minecraft:banner", "minecraft:banner");
		map.put("minecraft:flower_pot", "minecraft:flower_pot");
		map.put("minecraft:repeating_command_block", "minecraft:command_block");
		map.put("minecraft:chain_command_block", "minecraft:command_block");
		map.put("minecraft:shulker_box", "minecraft:shulker_box");
		map.put("minecraft:white_shulker_box", "minecraft:shulker_box");
		map.put("minecraft:orange_shulker_box", "minecraft:shulker_box");
		map.put("minecraft:magenta_shulker_box", "minecraft:shulker_box");
		map.put("minecraft:light_blue_shulker_box", "minecraft:shulker_box");
		map.put("minecraft:yellow_shulker_box", "minecraft:shulker_box");
		map.put("minecraft:lime_shulker_box", "minecraft:shulker_box");
		map.put("minecraft:pink_shulker_box", "minecraft:shulker_box");
		map.put("minecraft:gray_shulker_box", "minecraft:shulker_box");
		map.put("minecraft:silver_shulker_box", "minecraft:shulker_box");
		map.put("minecraft:cyan_shulker_box", "minecraft:shulker_box");
		map.put("minecraft:purple_shulker_box", "minecraft:shulker_box");
		map.put("minecraft:blue_shulker_box", "minecraft:shulker_box");
		map.put("minecraft:brown_shulker_box", "minecraft:shulker_box");
		map.put("minecraft:green_shulker_box", "minecraft:shulker_box");
		map.put("minecraft:red_shulker_box", "minecraft:shulker_box");
		map.put("minecraft:black_shulker_box", "minecraft:shulker_box");
		map.put("minecraft:bed", "minecraft:bed");
		map.put("minecraft:standing_sign", "minecraft:sign");
		map.put("minecraft:wall_sign", "minecraft:sign");
		map.put("minecraft:piston_head", "minecraft:piston");
		map.put("minecraft:daylight_detector_inverted", "minecraft:daylight_detector");
		map.put("minecraft:unpowered_comparator", "minecraft:comparator");
		map.put("minecraft:powered_comparator", "minecraft:comparator");
		map.put("minecraft:wall_banner", "minecraft:banner");
		map.put("minecraft:standing_banner", "minecraft:banner");
		map.put("minecraft:structure_block", "minecraft:structure_block");
		map.put("minecraft:end_portal", "minecraft:end_portal");
		map.put("minecraft:end_gateway", "minecraft:end_gateway");
		map.put("minecraft:shield", "minecraft:shield");
	}
}
