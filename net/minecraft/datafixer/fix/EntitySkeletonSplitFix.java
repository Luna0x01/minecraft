package net.minecraft.datafixer.fix;

import net.minecraft.datafixer.DataFix;
import net.minecraft.nbt.NbtCompound;

public class EntitySkeletonSplitFix implements DataFix {
	@Override
	public int getVersion() {
		return 701;
	}

	@Override
	public NbtCompound fixData(NbtCompound tag) {
		String string = tag.getString("id");
		if ("Skeleton".equals(string)) {
			int i = tag.getInt("SkeletonType");
			if (i == 1) {
				tag.putString("id", "WitherSkeleton");
			} else if (i == 2) {
				tag.putString("id", "Stray");
			}

			tag.remove("SkeletonType");
		}

		return tag;
	}
}
