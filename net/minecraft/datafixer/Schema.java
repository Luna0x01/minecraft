package net.minecraft.datafixer;

import net.minecraft.nbt.NbtCompound;

public interface Schema {
	NbtCompound fixData(DataFixer dataFixer, NbtCompound tag, int dataVersion);
}
