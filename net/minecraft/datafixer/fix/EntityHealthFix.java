package net.minecraft.datafixer.fix;

import com.google.common.collect.Sets;
import java.util.Set;
import net.minecraft.datafixer.DataFix;
import net.minecraft.nbt.NbtCompound;

public class EntityHealthFix implements DataFix {
	private static final Set<String> ENTITIES = Sets.newHashSet(
		new String[]{
			"ArmorStand",
			"Bat",
			"Blaze",
			"CaveSpider",
			"Chicken",
			"Cow",
			"Creeper",
			"EnderDragon",
			"Enderman",
			"Endermite",
			"EntityHorse",
			"Ghast",
			"Giant",
			"Guardian",
			"LavaSlime",
			"MushroomCow",
			"Ozelot",
			"Pig",
			"PigZombie",
			"Rabbit",
			"Sheep",
			"Shulker",
			"Silverfish",
			"Skeleton",
			"Slime",
			"SnowMan",
			"Spider",
			"Squid",
			"Villager",
			"VillagerGolem",
			"Witch",
			"WitherBoss",
			"Wolf",
			"Zombie"
		}
	);

	@Override
	public int getVersion() {
		return 109;
	}

	@Override
	public NbtCompound fixData(NbtCompound tag) {
		if (ENTITIES.contains(tag.getString("id"))) {
			float f;
			if (tag.contains("HealF", 99)) {
				f = tag.getFloat("HealF");
				tag.remove("HealF");
			} else {
				if (!tag.contains("Health", 99)) {
					return tag;
				}

				f = tag.getFloat("Health");
			}

			tag.putFloat("Health", f);
		}

		return tag;
	}
}
