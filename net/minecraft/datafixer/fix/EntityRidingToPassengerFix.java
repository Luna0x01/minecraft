package net.minecraft.datafixer.fix;

import net.minecraft.datafixer.DataFix;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;

public class EntityRidingToPassengerFix implements DataFix {
	@Override
	public int getVersion() {
		return 135;
	}

	@Override
	public NbtCompound fixData(NbtCompound tag) {
		while (tag.contains("Riding", 10)) {
			NbtCompound nbtCompound = this.removeRiding(tag);
			this.putPassengers(tag, nbtCompound);
			tag = nbtCompound;
		}

		return tag;
	}

	protected void putPassengers(NbtCompound old, NbtCompound fixed) {
		NbtList nbtList = new NbtList();
		nbtList.add(old);
		fixed.put("Passengers", nbtList);
	}

	protected NbtCompound removeRiding(NbtCompound old) {
		NbtCompound nbtCompound = old.getCompound("Riding");
		old.remove("Riding");
		return nbtCompound;
	}
}
