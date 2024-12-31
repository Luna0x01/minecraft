package net.minecraft.datafixer.fix;

import net.minecraft.datafixer.DataFix;
import net.minecraft.nbt.NbtCompound;

public class OptionsForceVBOFix implements DataFix {
	@Override
	public int getVersion() {
		return 505;
	}

	@Override
	public NbtCompound fixData(NbtCompound tag) {
		tag.putString("useVbo", "true");
		return tag;
	}
}
