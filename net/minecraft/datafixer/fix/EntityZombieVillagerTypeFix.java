package net.minecraft.datafixer.fix;

import java.util.Random;
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
				int i = -1;
				if (tag.contains("VillagerProfession", 99)) {
					try {
						i = this.clampType(tag.getInt("VillagerProfession"));
					} catch (RuntimeException var4) {
					}
				}

				if (i == -1) {
					i = this.clampType(RANDOM.nextInt(6));
				}

				tag.putInt("ZombieType", i);
			}

			tag.remove("IsVillager");
		}

		return tag;
	}

	private int clampType(int type) {
		return type >= 0 && type < 6 ? type : -1;
	}
}
