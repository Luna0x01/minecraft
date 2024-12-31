package net.minecraft.datafixer.schema;

import net.minecraft.datafixer.DataFixer;
import net.minecraft.datafixer.Schema;
import net.minecraft.nbt.NbtCompound;

public abstract class EntityIdentifierSchema implements Schema {
	private final String field_14412;
	private final String field_14413;

	public EntityIdentifierSchema(String string, String string2) {
		this.field_14412 = string;
		this.field_14413 = string2;
	}

	@Override
	public NbtCompound fixData(DataFixer dataFixer, NbtCompound tag, int dataVersion) {
		if (tag.getString(this.field_14412).equals(this.field_14413)) {
			tag = this.updateDataForProperties(dataFixer, tag, dataVersion);
		}

		return tag;
	}

	abstract NbtCompound updateDataForProperties(DataFixer dataFixer, NbtCompound tag, int version);
}
