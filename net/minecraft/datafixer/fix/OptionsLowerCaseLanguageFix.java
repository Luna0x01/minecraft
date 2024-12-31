package net.minecraft.datafixer.fix;

import java.util.Locale;
import net.minecraft.datafixer.DataFix;
import net.minecraft.nbt.NbtCompound;

public class OptionsLowerCaseLanguageFix implements DataFix {
	@Override
	public int getVersion() {
		return 816;
	}

	@Override
	public NbtCompound fixData(NbtCompound tag) {
		if (tag.contains("lang", 8)) {
			tag.putString("lang", tag.getString("lang").toLowerCase(Locale.ROOT));
		}

		return tag;
	}
}
