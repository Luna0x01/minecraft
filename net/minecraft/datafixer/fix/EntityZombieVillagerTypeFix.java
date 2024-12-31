package net.minecraft.datafixer.fix;

import java.util.Random;
import net.minecraft.class_3040;
import net.minecraft.datafixer.DataFix;
import net.minecraft.nbt.NbtCompound;

public class EntityZombieVillagerTypeFix implements DataFix {
	private static final Random RANDOM = new Random();

	@Override
	public int getVersion() {
		return 502;
	}

	@Override
	public NbtCompound fixData(NbtCompound tag) {
		if ("Zombie".equals(tag.getString("id")) && tag.getBoolean("IsVillager")) {
			if (!tag.contains("ZombieType", 99)) {
				class_3040 lv = null;
				if (tag.contains("VillagerProfession", 99)) {
					try {
						lv = class_3040.method_13554(tag.getInt("VillagerProfession") + 1);
					} catch (RuntimeException var4) {
					}
				}

				if (lv == null) {
					lv = class_3040.method_13554(RANDOM.nextInt(5) + 1);
				}

				tag.putInt("ZombieType", lv.method_13553());
			}

			tag.remove("IsVillager");
		}

		return tag;
	}
}
