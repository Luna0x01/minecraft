package net.minecraft.datafixer.fix;

import net.minecraft.datafixer.DataFix;
import net.minecraft.nbt.NbtCompound;

public class ItemSpawnEggFix implements DataFix {
	private static final String[] DAMAGE_TO_ENTITY_IDS = new String[256];

	@Override
	public int getVersion() {
		return 105;
	}

	@Override
	public NbtCompound fixData(NbtCompound tag) {
		if ("minecraft:spawn_egg".equals(tag.getString("id"))) {
			NbtCompound nbtCompound = tag.getCompound("tag");
			NbtCompound nbtCompound2 = nbtCompound.getCompound("EntityTag");
			short s = tag.getShort("Damage");
			if (!nbtCompound2.contains("id", 8)) {
				String string = DAMAGE_TO_ENTITY_IDS[s & 255];
				if (string != null) {
					nbtCompound2.putString("id", string);
					nbtCompound.put("EntityTag", nbtCompound2);
					tag.put("tag", nbtCompound);
				}
			}

			if (s != 0) {
				tag.putShort("Damage", (short)0);
			}
		}

		return tag;
	}

	static {
		String[] strings = DAMAGE_TO_ENTITY_IDS;
		strings[1] = "Item";
		strings[2] = "XPOrb";
		strings[7] = "ThrownEgg";
		strings[8] = "LeashKnot";
		strings[9] = "Painting";
		strings[10] = "Arrow";
		strings[11] = "Snowball";
		strings[12] = "Fireball";
		strings[13] = "SmallFireball";
		strings[14] = "ThrownEnderpearl";
		strings[15] = "EyeOfEnderSignal";
		strings[16] = "ThrownPotion";
		strings[17] = "ThrownExpBottle";
		strings[18] = "ItemFrame";
		strings[19] = "WitherSkull";
		strings[20] = "PrimedTnt";
		strings[21] = "FallingSand";
		strings[22] = "FireworksRocketEntity";
		strings[23] = "TippedArrow";
		strings[24] = "SpectralArrow";
		strings[25] = "ShulkerBullet";
		strings[26] = "DragonFireball";
		strings[30] = "ArmorStand";
		strings[41] = "Boat";
		strings[42] = "MinecartRideable";
		strings[43] = "MinecartChest";
		strings[44] = "MinecartFurnace";
		strings[45] = "MinecartTNT";
		strings[46] = "MinecartHopper";
		strings[47] = "MinecartSpawner";
		strings[40] = "MinecartCommandBlock";
		strings[48] = "Mob";
		strings[49] = "Monster";
		strings[50] = "Creeper";
		strings[51] = "Skeleton";
		strings[52] = "Spider";
		strings[53] = "Giant";
		strings[54] = "Zombie";
		strings[55] = "Slime";
		strings[56] = "Ghast";
		strings[57] = "PigZombie";
		strings[58] = "Enderman";
		strings[59] = "CaveSpider";
		strings[60] = "Silverfish";
		strings[61] = "Blaze";
		strings[62] = "LavaSlime";
		strings[63] = "EnderDragon";
		strings[64] = "WitherBoss";
		strings[65] = "Bat";
		strings[66] = "Witch";
		strings[67] = "Endermite";
		strings[68] = "Guardian";
		strings[69] = "Shulker";
		strings[90] = "Pig";
		strings[91] = "Sheep";
		strings[92] = "Cow";
		strings[93] = "Chicken";
		strings[94] = "Squid";
		strings[95] = "Wolf";
		strings[96] = "MushroomCow";
		strings[97] = "SnowMan";
		strings[98] = "Ozelot";
		strings[99] = "VillagerGolem";
		strings[100] = "EntityHorse";
		strings[101] = "Rabbit";
		strings[120] = "Villager";
		strings[200] = "EnderCrystal";
	}
}
