package net.minecraft.datafixer.fix;

import com.google.common.collect.Maps;
import java.util.Map;
import net.minecraft.datafixer.DataFix;
import net.minecraft.nbt.NbtCompound;

public class BlockEntityIdFix implements DataFix {
	private static final Map<String, String> RENAMED_BLOCK_ENTITIES = Maps.newHashMap();

	@Override
	public int getVersion() {
		return 704;
	}

	@Override
	public NbtCompound fixData(NbtCompound tag) {
		String string = (String)RENAMED_BLOCK_ENTITIES.get(tag.getString("id"));
		if (string != null) {
			tag.putString("id", string);
		}

		return tag;
	}

	static {
		RENAMED_BLOCK_ENTITIES.put("Airportal", "minecraft:end_portal");
		RENAMED_BLOCK_ENTITIES.put("Banner", "minecraft:banner");
		RENAMED_BLOCK_ENTITIES.put("Beacon", "minecraft:beacon");
		RENAMED_BLOCK_ENTITIES.put("Cauldron", "minecraft:brewing_stand");
		RENAMED_BLOCK_ENTITIES.put("Chest", "minecraft:chest");
		RENAMED_BLOCK_ENTITIES.put("Comparator", "minecraft:comparator");
		RENAMED_BLOCK_ENTITIES.put("Control", "minecraft:command_block");
		RENAMED_BLOCK_ENTITIES.put("DLDetector", "minecraft:daylight_detector");
		RENAMED_BLOCK_ENTITIES.put("Dropper", "minecraft:dropper");
		RENAMED_BLOCK_ENTITIES.put("EnchantTable", "minecraft:enchanting_table");
		RENAMED_BLOCK_ENTITIES.put("EndGateway", "minecraft:end_gateway");
		RENAMED_BLOCK_ENTITIES.put("EnderChest", "minecraft:ender_chest");
		RENAMED_BLOCK_ENTITIES.put("FlowerPot", "minecraft:flower_pot");
		RENAMED_BLOCK_ENTITIES.put("Furnace", "minecraft:furnace");
		RENAMED_BLOCK_ENTITIES.put("Hopper", "minecraft:hopper");
		RENAMED_BLOCK_ENTITIES.put("MobSpawner", "minecraft:mob_spawner");
		RENAMED_BLOCK_ENTITIES.put("Music", "minecraft:noteblock");
		RENAMED_BLOCK_ENTITIES.put("Piston", "minecraft:piston");
		RENAMED_BLOCK_ENTITIES.put("RecordPlayer", "minecraft:jukebox");
		RENAMED_BLOCK_ENTITIES.put("Sign", "minecraft:sign");
		RENAMED_BLOCK_ENTITIES.put("Skull", "minecraft:skull");
		RENAMED_BLOCK_ENTITIES.put("Structure", "minecraft:structure_block");
		RENAMED_BLOCK_ENTITIES.put("Trap", "minecraft:dispenser");
	}
}
