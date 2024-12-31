package net.minecraft.datafixer.fix;

import net.minecraft.datafixer.DataFix;
import net.minecraft.nbt.NbtCompound;

public class EntityHorseSplitFix implements DataFix {
	@Override
	public int getVersion() {
		return 703;
	}

	@Override
	public NbtCompound fixData(NbtCompound tag) {
		if ("EntityHorse".equals(tag.getString("id"))) {
			int i = tag.getInt("Type");
			switch (i) {
				case 0:
				default:
					tag.putString("id", "Horse");
					break;
				case 1:
					tag.putString("id", "Donkey");
					break;
				case 2:
					tag.putString("id", "Mule");
					break;
				case 3:
					tag.putString("id", "ZombieHorse");
					break;
				case 4:
					tag.putString("id", "SkeletonHorse");
			}

			tag.remove("Type");
		}

		return tag;
	}
}
