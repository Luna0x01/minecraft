package net.minecraft.datafixer.fix;

import net.minecraft.datafixer.DataFix;
import net.minecraft.nbt.NbtCompound;

public class EntityElderGuardianSplitFix implements DataFix {
	@Override
	public int getVersion() {
		return 700;
	}

	@Override
	public NbtCompound fixData(NbtCompound tag) {
		if ("Guardian".equals(tag.getString("id"))) {
			if (tag.getBoolean("Elder")) {
				tag.putString("id", "ElderGuardian");
			}

			tag.remove("Elder");
		}

		return tag;
	}
}
