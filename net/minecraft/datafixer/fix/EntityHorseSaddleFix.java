package net.minecraft.datafixer.fix;

import net.minecraft.datafixer.DataFix;
import net.minecraft.nbt.NbtCompound;

public class EntityHorseSaddleFix implements DataFix {
	@Override
	public int getVersion() {
		return 110;
	}

	@Override
	public NbtCompound fixData(NbtCompound tag) {
		if ("EntityHorse".equals(tag.getString("id")) && !tag.contains("SaddleItem", 10) && tag.getBoolean("Saddle")) {
			NbtCompound nbtCompound = new NbtCompound();
			nbtCompound.putString("id", "minecraft:saddle");
			nbtCompound.putByte("Count", (byte)1);
			nbtCompound.putShort("Damage", (short)0);
			tag.put("SaddleItem", nbtCompound);
			tag.remove("Saddle");
		}

		return tag;
	}
}
