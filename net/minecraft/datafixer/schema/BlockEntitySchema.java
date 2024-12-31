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
	private static final Map<String, String> ID_MAP = Maps.newHashMap();

	@Nullable
	private static String method_12915(String string) {
		return (String)ID_MAP.get(new Identifier(string).toString());
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
				String string2 = method_12915(string);
				boolean bl;
				if (string2 == null) {
					LOGGER.warn("Unable to resolve BlockEntity for ItemInstance: {}", new Object[]{string});
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
		Map<String, String> map = ID_MAP;
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
	}
}
