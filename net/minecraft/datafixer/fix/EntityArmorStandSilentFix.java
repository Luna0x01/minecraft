package net.minecraft.datafixer.fix;

import net.minecraft.datafixer.DataFix;
import net.minecraft.nbt.NbtCompound;

public class EntityArmorStandSilentFix implements DataFix {
	@Override
	public int getVersion() {
		return 147;
	}

	@Override
	public NbtCompound fixData(NbtCompound tag) {
		if ("ArmorStand".equals(tag.getString("id")) && tag.getBoolean("Silent") && !tag.getBoolean("Marker")) {
			tag.remove("Silent");
		}

		return tag;
	}
}
