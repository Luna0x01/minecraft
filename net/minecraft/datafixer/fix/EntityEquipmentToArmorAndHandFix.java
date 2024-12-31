package net.minecraft.datafixer.fix;

import net.minecraft.datafixer.DataFix;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtFloat;
import net.minecraft.nbt.NbtList;

public class EntityEquipmentToArmorAndHandFix implements DataFix {
	@Override
	public int getVersion() {
		return 100;
	}

	@Override
	public NbtCompound fixData(NbtCompound tag) {
		NbtList nbtList = tag.getList("Equipment", 10);
		if (!nbtList.isEmpty() && !tag.contains("HandItems", 10)) {
			NbtList nbtList2 = new NbtList();
			nbtList2.add(nbtList.get(0));
			nbtList2.add(new NbtCompound());
			tag.put("HandItems", nbtList2);
		}

		if (nbtList.size() > 1 && !tag.contains("ArmorItem", 10)) {
			NbtList nbtList3 = new NbtList();
			nbtList3.add(nbtList.getCompound(1));
			nbtList3.add(nbtList.getCompound(2));
			nbtList3.add(nbtList.getCompound(3));
			nbtList3.add(nbtList.getCompound(4));
			tag.put("ArmorItems", nbtList3);
		}

		tag.remove("Equipment");
		if (tag.contains("DropChances", 9)) {
			NbtList nbtList4 = tag.getList("DropChances", 5);
			if (!tag.contains("HandDropChances", 10)) {
				NbtList nbtList5 = new NbtList();
				nbtList5.add(new NbtFloat(nbtList4.getFloat(0)));
				nbtList5.add(new NbtFloat(0.0F));
				tag.put("HandDropChances", nbtList5);
			}

			if (!tag.contains("ArmorDropChances", 10)) {
				NbtList nbtList6 = new NbtList();
				nbtList6.add(new NbtFloat(nbtList4.getFloat(1)));
				nbtList6.add(new NbtFloat(nbtList4.getFloat(2)));
				nbtList6.add(new NbtFloat(nbtList4.getFloat(3)));
				nbtList6.add(new NbtFloat(nbtList4.getFloat(4)));
				tag.put("ArmorDropChances", nbtList6);
			}

			tag.remove("DropChances");
		}

		return tag;
	}
}
