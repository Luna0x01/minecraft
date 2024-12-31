package net.minecraft.datafixer.fix;

import net.minecraft.datafixer.DataFix;
import net.minecraft.nbt.NbtCompound;

public class ItemShulkerColorFix implements DataFix {
	@Override
	public int getVersion() {
		return 813;
	}

	@Override
	public NbtCompound fixData(NbtCompound tag) {
		if ("minecraft:shulker".equals(tag.getString("id"))) {
			tag.remove("Color");
		}

		return tag;
	}
}
