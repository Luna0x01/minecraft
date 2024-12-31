package net.minecraft.datafixer.fix;

import net.minecraft.datafixer.DataFix;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.DyeColor;

public class BedItemColorFix implements DataFix {
	@Override
	public int getVersion() {
		return 1125;
	}

	@Override
	public NbtCompound fixData(NbtCompound tag) {
		if ("minecraft:bed".equals(tag.getString("id")) && tag.getShort("Damage") == 0) {
			tag.putShort("Damage", (short)DyeColor.RED.getId());
		}

		return tag;
	}
}
