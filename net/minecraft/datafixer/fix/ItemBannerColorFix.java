package net.minecraft.datafixer.fix;

import net.minecraft.datafixer.DataFix;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;

public class ItemBannerColorFix implements DataFix {
	@Override
	public int getVersion() {
		return 804;
	}

	@Override
	public NbtCompound fixData(NbtCompound tag) {
		if ("minecraft:banner".equals(tag.getString("id")) && tag.contains("tag", 10)) {
			NbtCompound nbtCompound = tag.getCompound("tag");
			if (nbtCompound.contains("BlockEntityTag", 10)) {
				NbtCompound nbtCompound2 = nbtCompound.getCompound("BlockEntityTag");
				if (nbtCompound2.contains("Base", 99)) {
					tag.putShort("Damage", (short)(nbtCompound2.getShort("Base") & 15));
					if (nbtCompound.contains("display", 10)) {
						NbtCompound nbtCompound3 = nbtCompound.getCompound("display");
						if (nbtCompound3.contains("Lore", 9)) {
							NbtList nbtList = nbtCompound3.getList("Lore", 8);
							if (nbtList.size() == 1 && "(+NBT)".equals(nbtList.getString(0))) {
								return tag;
							}
						}
					}

					nbtCompound2.remove("Base");
					if (nbtCompound2.isEmpty()) {
						nbtCompound.remove("BlockEntityTag");
					}

					if (nbtCompound.isEmpty()) {
						tag.remove("tag");
					}
				}
			}
		}

		return tag;
	}
}
