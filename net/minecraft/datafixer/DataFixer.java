package net.minecraft.datafixer;

import net.minecraft.nbt.NbtCompound;

public interface DataFixer {
	NbtCompound update(DataFixType fixType, NbtCompound tag, int dataVersion);
}
