package net.minecraft.datafixer.fix;

import net.minecraft.datafixer.DataFix;
import net.minecraft.nbt.NbtCompound;

public class EntityZombieSplitFix implements DataFix {
	@Override
	public int getVersion() {
		return 702;
	}

	@Override
	public NbtCompound fixData(NbtCompound tag) {
		if ("Zombie".equals(tag.getString("id"))) {
			int i = tag.getInt("ZombieType");
			switch (i) {
				case 0:
				default:
					break;
				case 1:
				case 2:
				case 3:
				case 4:
				case 5:
					tag.putString("id", "ZombieVillager");
					tag.putInt("Profession", i - 1);
					break;
				case 6:
					tag.putString("id", "Husk");
			}

			tag.remove("ZombieType");
		}

		return tag;
	}
}
