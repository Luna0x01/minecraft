package net.minecraft.datafixer.fix;

import net.minecraft.datafixer.DataFix;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;

public class MobSpawnerEntityIdentifiersFix implements DataFix {
	@Override
	public int getVersion() {
		return 107;
	}

	@Override
	public NbtCompound fixData(NbtCompound tag) {
		if (!"MobSpawner".equals(tag.getString("id"))) {
			return tag;
		} else {
			if (tag.contains("EntityId", 8)) {
				String string = tag.getString("EntityId");
				NbtCompound nbtCompound = tag.getCompound("SpawnData");
				nbtCompound.putString("id", string.isEmpty() ? "Pig" : string);
				tag.put("SpawnData", nbtCompound);
				tag.remove("EntityId");
			}

			if (tag.contains("SpawnPotentials", 9)) {
				NbtList nbtList = tag.getList("SpawnPotentials", 10);

				for (int i = 0; i < nbtList.size(); i++) {
					NbtCompound nbtCompound2 = nbtList.getCompound(i);
					if (nbtCompound2.contains("Type", 8)) {
						NbtCompound nbtCompound3 = nbtCompound2.getCompound("Properties");
						nbtCompound3.putString("id", nbtCompound2.getString("Type"));
						nbtCompound2.put("Entity", nbtCompound3);
						nbtCompound2.remove("Type");
						nbtCompound2.remove("Properties");
					}
				}
			}

			return tag;
		}
	}
}
