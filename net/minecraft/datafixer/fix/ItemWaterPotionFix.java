package net.minecraft.datafixer.fix;

import net.minecraft.datafixer.DataFix;
import net.minecraft.nbt.NbtCompound;

public class ItemWaterPotionFix implements DataFix {
	@Override
	public int getVersion() {
		return 806;
	}

	@Override
	public NbtCompound fixData(NbtCompound tag) {
		String string = tag.getString("id");
		if ("minecraft:potion".equals(string)
			|| "minecraft:splash_potion".equals(string)
			|| "minecraft:lingering_potion".equals(string)
			|| "minecraft:tipped_arrow".equals(string)) {
			NbtCompound nbtCompound = tag.getCompound("tag");
			if (!nbtCompound.contains("Potion", 8)) {
				nbtCompound.putString("Potion", "minecraft:water");
			}

			if (!tag.contains("tag", 10)) {
				tag.put("tag", nbtCompound);
			}
		}

		return tag;
	}
}
