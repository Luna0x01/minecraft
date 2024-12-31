package net.minecraft.block.entity;

import net.minecraft.datafixer.DataFixerUpper;
import net.minecraft.datafixer.schema.ItemListSchema;
import net.minecraft.world.level.storage.LevelDataType;

public class DropperBlockEntity extends DispenserBlockEntity {
	public static void registerDataFixes(DataFixerUpper dataFixer) {
		dataFixer.addSchema(LevelDataType.BLOCK_ENTITY, new ItemListSchema(DropperBlockEntity.class, "Items"));
	}

	@Override
	public String getTranslationKey() {
		return this.hasCustomName() ? this.name : "container.dropper";
	}

	@Override
	public String getId() {
		return "minecraft:dropper";
	}
}
