package net.minecraft.datafixer.fix;

import net.minecraft.datafixer.DataFix;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;

public class EntityRedundantChanceTagsFix implements DataFix {
	@Override
	public int getVersion() {
		return 113;
	}

	@Override
	public NbtCompound fixData(NbtCompound tag) {
		if (tag.contains("HandDropChances", 9)) {
			NbtList nbtList = tag.getList("HandDropChances", 5);
			if (nbtList.size() == 2 && nbtList.getFloat(0) == 0.0F && nbtList.getFloat(1) == 0.0F) {
				tag.remove("HandDropChances");
			}
		}

		if (tag.contains("ArmorDropChances", 9)) {
			NbtList nbtList2 = tag.getList("ArmorDropChances", 5);
			if (nbtList2.size() == 4 && nbtList2.getFloat(0) == 0.0F && nbtList2.getFloat(1) == 0.0F && nbtList2.getFloat(2) == 0.0F && nbtList2.getFloat(3) == 0.0F) {
				tag.remove("ArmorDropChances");
			}
		}

		return tag;
	}
}
