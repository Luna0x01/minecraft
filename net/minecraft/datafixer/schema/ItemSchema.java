package net.minecraft.datafixer.schema;

import net.minecraft.datafixer.DataFixer;
import net.minecraft.datafixer.DataFixerFactory;
import net.minecraft.nbt.NbtCompound;

public class ItemSchema extends EntityIdentifierSchema {
	private final String[] properties;

	public ItemSchema(Class<?> class_, String... strings) {
		super(class_);
		this.properties = strings;
	}

	@Override
	NbtCompound updateDataForProperties(DataFixer dataFixer, NbtCompound tag, int version) {
		for (String string : this.properties) {
			tag = DataFixerFactory.updateItem(dataFixer, tag, version, string);
		}

		return tag;
	}
}
