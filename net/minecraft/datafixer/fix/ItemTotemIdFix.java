package net.minecraft.datafixer.fix;

import net.minecraft.datafixer.DataFix;
import net.minecraft.nbt.NbtCompound;

public class ItemTotemIdFix implements DataFix {
	@Override
	public int getVersion() {
		return 820;
	}

	@Override
	public NbtCompound fixData(NbtCompound tag) {
		if ("minecraft:totem".equals(tag.getString("id"))) {
			tag.putString("id", "minecraft:totem_of_undying");
		}

		return tag;
	}
}
