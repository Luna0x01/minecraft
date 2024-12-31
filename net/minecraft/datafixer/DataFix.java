package net.minecraft.datafixer;

import net.minecraft.nbt.NbtCompound;

public interface DataFix {
	int getVersion();

	NbtCompound fixData(NbtCompound tag);
}
