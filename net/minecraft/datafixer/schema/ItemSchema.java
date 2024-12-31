package net.minecraft.datafixer.schema;

import net.minecraft.datafixer.DataFixer;
import net.minecraft.datafixer.DataFixerFactory;
import net.minecraft.nbt.NbtCompound;

public class ItemSchema extends EntityIdentifierSchema {
	private final String[] properties;

	public ItemSchema(String string, String... strings) {
		super("id", string);
		this.properties = strings;
	}

	@Override
	NbtCompound updateDataForProperties(DataFixer dataFixer, NbtCompound tag, int version) {
		int i = 0;

		for (int j = this.properties.length; i < j; i++) {
			tag = DataFixerFactory.updateItem(dataFixer, tag, version, this.properties[i]);
		}

		return tag;
	}
}
