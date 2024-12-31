package net.minecraft.world.level.storage;

import net.minecraft.datafixer.DataFixType;

public enum LevelDataType implements DataFixType {
	LEVEL,
	PLAYER,
	CHUNK,
	BLOCK_ENTITY,
	ENTITY,
	ITEM_INSTANCE,
	OPTIONS,
	STRUCTURE;
}
