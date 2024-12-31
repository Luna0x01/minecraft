package net.minecraft.datafixer.fix;

import net.minecraft.datafixer.DataFix;
import net.minecraft.nbt.NbtCompound;

public class EntityShulkerColorFix implements DataFix {
	@Override
	public int getVersion() {
		return 808;
	}

	@Override
	public NbtCompound fixData(NbtCompound tag) {
		if ("minecraft:shulker".equals(tag.getString("id")) && !tag.contains("Color", 99)) {
			tag.putByte("Color", (byte)10);
		}

		return tag;
	}
}
