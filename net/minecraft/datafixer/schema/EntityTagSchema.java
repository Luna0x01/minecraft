package net.minecraft.datafixer.schema;

import net.minecraft.datafixer.DataFixer;
import net.minecraft.datafixer.Schema;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.world.level.storage.LevelDataType;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class EntityTagSchema implements Schema {
	private static final Logger LOGGER = LogManager.getLogger();

	@Override
	public NbtCompound fixData(DataFixer dataFixer, NbtCompound tag, int dataVersion) {
		NbtCompound nbtCompound = tag.getCompound("tag");
		if (nbtCompound.contains("EntityTag", 10)) {
			NbtCompound nbtCompound2 = nbtCompound.getCompound("EntityTag");
			String string = tag.getString("id");
			String string2;
			if ("minecraft:armor_stand".equals(string)) {
				string2 = "ArmorStand";
			} else {
				if (!"minecraft:spawn_egg".equals(string)) {
					return tag;
				}

				string2 = nbtCompound2.getString("id");
			}

			boolean bl;
			if (string2 == null) {
				LOGGER.warn("Unable to resolve Entity for ItemInstance: {}", new Object[]{string});
				bl = false;
			} else {
				bl = !nbtCompound2.contains("id", 8);
				nbtCompound2.putString("id", string2);
			}

			dataFixer.update(LevelDataType.ENTITY, nbtCompound2, dataVersion);
			if (bl) {
				nbtCompound2.remove("id");
			}
		}

		return tag;
	}
}
