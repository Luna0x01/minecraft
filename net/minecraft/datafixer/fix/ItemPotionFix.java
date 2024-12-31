package net.minecraft.datafixer.fix;

import net.minecraft.datafixer.DataFix;
import net.minecraft.nbt.NbtCompound;

public class ItemPotionFix implements DataFix {
	private static final String[] INGREDIENT_IDS = new String[128];

	@Override
	public int getVersion() {
		return 102;
	}

	@Override
	public NbtCompound fixData(NbtCompound tag) {
		if ("minecraft:potion".equals(tag.getString("id"))) {
			NbtCompound nbtCompound = tag.getCompound("tag");
			short s = tag.getShort("Damage");
			if (!nbtCompound.contains("Potion", 8)) {
				String string = INGREDIENT_IDS[s & 127];
				nbtCompound.putString("Potion", string == null ? "minecraft:water" : string);
				tag.put("tag", nbtCompound);
				if ((s & 16384) == 16384) {
					tag.putString("id", "minecraft:splash_potion");
				}
			}

			if (s != 0) {
				tag.putShort("Damage", (short)0);
			}
		}

		return tag;
	}

	static {
		INGREDIENT_IDS[0] = "minecraft:water";
		INGREDIENT_IDS[1] = "minecraft:regeneration";
		INGREDIENT_IDS[2] = "minecraft:swiftness";
		INGREDIENT_IDS[3] = "minecraft:fire_resistance";
		INGREDIENT_IDS[4] = "minecraft:poison";
		INGREDIENT_IDS[5] = "minecraft:healing";
		INGREDIENT_IDS[6] = "minecraft:night_vision";
		INGREDIENT_IDS[7] = null;
		INGREDIENT_IDS[8] = "minecraft:weakness";
		INGREDIENT_IDS[9] = "minecraft:strength";
		INGREDIENT_IDS[10] = "minecraft:slowness";
		INGREDIENT_IDS[11] = "minecraft:leaping";
		INGREDIENT_IDS[12] = "minecraft:harming";
		INGREDIENT_IDS[13] = "minecraft:water_breathing";
		INGREDIENT_IDS[14] = "minecraft:invisibility";
		INGREDIENT_IDS[15] = null;
		INGREDIENT_IDS[16] = "minecraft:awkward";
		INGREDIENT_IDS[17] = "minecraft:regeneration";
		INGREDIENT_IDS[18] = "minecraft:swiftness";
		INGREDIENT_IDS[19] = "minecraft:fire_resistance";
		INGREDIENT_IDS[20] = "minecraft:poison";
		INGREDIENT_IDS[21] = "minecraft:healing";
		INGREDIENT_IDS[22] = "minecraft:night_vision";
		INGREDIENT_IDS[23] = null;
		INGREDIENT_IDS[24] = "minecraft:weakness";
		INGREDIENT_IDS[25] = "minecraft:strength";
		INGREDIENT_IDS[26] = "minecraft:slowness";
		INGREDIENT_IDS[27] = "minecraft:leaping";
		INGREDIENT_IDS[28] = "minecraft:harming";
		INGREDIENT_IDS[29] = "minecraft:water_breathing";
		INGREDIENT_IDS[30] = "minecraft:invisibility";
		INGREDIENT_IDS[31] = null;
		INGREDIENT_IDS[32] = "minecraft:thick";
		INGREDIENT_IDS[33] = "minecraft:strong_regeneration";
		INGREDIENT_IDS[34] = "minecraft:strong_swiftness";
		INGREDIENT_IDS[35] = "minecraft:fire_resistance";
		INGREDIENT_IDS[36] = "minecraft:strong_poison";
		INGREDIENT_IDS[37] = "minecraft:strong_healing";
		INGREDIENT_IDS[38] = "minecraft:night_vision";
		INGREDIENT_IDS[39] = null;
		INGREDIENT_IDS[40] = "minecraft:weakness";
		INGREDIENT_IDS[41] = "minecraft:strong_strength";
		INGREDIENT_IDS[42] = "minecraft:slowness";
		INGREDIENT_IDS[43] = "minecraft:strong_leaping";
		INGREDIENT_IDS[44] = "minecraft:strong_harming";
		INGREDIENT_IDS[45] = "minecraft:water_breathing";
		INGREDIENT_IDS[46] = "minecraft:invisibility";
		INGREDIENT_IDS[47] = null;
		INGREDIENT_IDS[48] = null;
		INGREDIENT_IDS[49] = "minecraft:strong_regeneration";
		INGREDIENT_IDS[50] = "minecraft:strong_swiftness";
		INGREDIENT_IDS[51] = "minecraft:fire_resistance";
		INGREDIENT_IDS[52] = "minecraft:strong_poison";
		INGREDIENT_IDS[53] = "minecraft:strong_healing";
		INGREDIENT_IDS[54] = "minecraft:night_vision";
		INGREDIENT_IDS[55] = null;
		INGREDIENT_IDS[56] = "minecraft:weakness";
		INGREDIENT_IDS[57] = "minecraft:strong_strength";
		INGREDIENT_IDS[58] = "minecraft:slowness";
		INGREDIENT_IDS[59] = "minecraft:strong_leaping";
		INGREDIENT_IDS[60] = "minecraft:strong_harming";
		INGREDIENT_IDS[61] = "minecraft:water_breathing";
		INGREDIENT_IDS[62] = "minecraft:invisibility";
		INGREDIENT_IDS[63] = null;
		INGREDIENT_IDS[64] = "minecraft:mundane";
		INGREDIENT_IDS[65] = "minecraft:long_regeneration";
		INGREDIENT_IDS[66] = "minecraft:long_swiftness";
		INGREDIENT_IDS[67] = "minecraft:long_fire_resistance";
		INGREDIENT_IDS[68] = "minecraft:long_poison";
		INGREDIENT_IDS[69] = "minecraft:healing";
		INGREDIENT_IDS[70] = "minecraft:long_night_vision";
		INGREDIENT_IDS[71] = null;
		INGREDIENT_IDS[72] = "minecraft:long_weakness";
		INGREDIENT_IDS[73] = "minecraft:long_strength";
		INGREDIENT_IDS[74] = "minecraft:long_slowness";
		INGREDIENT_IDS[75] = "minecraft:long_leaping";
		INGREDIENT_IDS[76] = "minecraft:harming";
		INGREDIENT_IDS[77] = "minecraft:long_water_breathing";
		INGREDIENT_IDS[78] = "minecraft:long_invisibility";
		INGREDIENT_IDS[79] = null;
		INGREDIENT_IDS[80] = "minecraft:awkward";
		INGREDIENT_IDS[81] = "minecraft:long_regeneration";
		INGREDIENT_IDS[82] = "minecraft:long_swiftness";
		INGREDIENT_IDS[83] = "minecraft:long_fire_resistance";
		INGREDIENT_IDS[84] = "minecraft:long_poison";
		INGREDIENT_IDS[85] = "minecraft:healing";
		INGREDIENT_IDS[86] = "minecraft:long_night_vision";
		INGREDIENT_IDS[87] = null;
		INGREDIENT_IDS[88] = "minecraft:long_weakness";
		INGREDIENT_IDS[89] = "minecraft:long_strength";
		INGREDIENT_IDS[90] = "minecraft:long_slowness";
		INGREDIENT_IDS[91] = "minecraft:long_leaping";
		INGREDIENT_IDS[92] = "minecraft:harming";
		INGREDIENT_IDS[93] = "minecraft:long_water_breathing";
		INGREDIENT_IDS[94] = "minecraft:long_invisibility";
		INGREDIENT_IDS[95] = null;
		INGREDIENT_IDS[96] = "minecraft:thick";
		INGREDIENT_IDS[97] = "minecraft:regeneration";
		INGREDIENT_IDS[98] = "minecraft:swiftness";
		INGREDIENT_IDS[99] = "minecraft:long_fire_resistance";
		INGREDIENT_IDS[100] = "minecraft:poison";
		INGREDIENT_IDS[101] = "minecraft:strong_healing";
		INGREDIENT_IDS[102] = "minecraft:long_night_vision";
		INGREDIENT_IDS[103] = null;
		INGREDIENT_IDS[104] = "minecraft:long_weakness";
		INGREDIENT_IDS[105] = "minecraft:strength";
		INGREDIENT_IDS[106] = "minecraft:long_slowness";
		INGREDIENT_IDS[107] = "minecraft:leaping";
		INGREDIENT_IDS[108] = "minecraft:strong_harming";
		INGREDIENT_IDS[109] = "minecraft:long_water_breathing";
		INGREDIENT_IDS[110] = "minecraft:long_invisibility";
		INGREDIENT_IDS[111] = null;
		INGREDIENT_IDS[112] = null;
		INGREDIENT_IDS[113] = "minecraft:regeneration";
		INGREDIENT_IDS[114] = "minecraft:swiftness";
		INGREDIENT_IDS[115] = "minecraft:long_fire_resistance";
		INGREDIENT_IDS[116] = "minecraft:poison";
		INGREDIENT_IDS[117] = "minecraft:strong_healing";
		INGREDIENT_IDS[118] = "minecraft:long_night_vision";
		INGREDIENT_IDS[119] = null;
		INGREDIENT_IDS[120] = "minecraft:long_weakness";
		INGREDIENT_IDS[121] = "minecraft:strength";
		INGREDIENT_IDS[122] = "minecraft:long_slowness";
		INGREDIENT_IDS[123] = "minecraft:leaping";
		INGREDIENT_IDS[124] = "minecraft:strong_harming";
		INGREDIENT_IDS[125] = "minecraft:long_water_breathing";
		INGREDIENT_IDS[126] = "minecraft:long_invisibility";
		INGREDIENT_IDS[127] = null;
	}
}
