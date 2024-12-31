package net.minecraft.datafixer.fix;

import java.util.UUID;
import net.minecraft.datafixer.DataFix;
import net.minecraft.nbt.NbtCompound;

public class EntityStringUuidFix implements DataFix {
	@Override
	public int getVersion() {
		return 108;
	}

	@Override
	public NbtCompound fixData(NbtCompound tag) {
		if (tag.contains("UUID", 8)) {
			tag.putUuid("UUID", UUID.fromString(tag.getString("UUID")));
		}

		return tag;
	}
}
