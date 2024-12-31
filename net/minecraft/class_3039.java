package net.minecraft;

import net.minecraft.loot.LootTables;
import net.minecraft.sound.Sound;
import net.minecraft.sound.Sounds;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Identifier;

public enum class_3039 {
	NORMAL("Skeleton", LootTables.SKELETON_ENTITIE),
	WITHER("WitherSkeleton", LootTables.WITHER_SKELETON_ENTITIE),
	STRAY("Stray", LootTables.STRAY_ENTITIE);

	private final TranslatableText field_15044;
	private final Identifier field_15045;

	private class_3039(String string2, Identifier identifier) {
		this.field_15044 = new TranslatableText("entity." + string2 + ".name");
		this.field_15045 = identifier;
	}

	public int method_13540() {
		return this.ordinal();
	}

	public static class_3039 method_13541(int i) {
		return values()[i];
	}

	public Identifier method_13542() {
		return this.field_15045;
	}

	public Sound method_13543() {
		switch (this) {
			case WITHER:
				return Sounds.ENTITY_WITHER_SKELETON_AMBIENT;
			case STRAY:
				return Sounds.ENTITY_STRAY_AMBIENT;
			default:
				return Sounds.ENTITY_SKELETON_AMBIENT;
		}
	}

	public Sound method_13544() {
		switch (this) {
			case WITHER:
				return Sounds.ENTITY_WITHER_SKELETON_HURT;
			case STRAY:
				return Sounds.ENTITY_STRAY_HURT;
			default:
				return Sounds.ENTITY_SKELETON_HURT;
		}
	}

	public Sound method_13545() {
		switch (this) {
			case WITHER:
				return Sounds.ENTITY_WITHER_SKELETON_DEATH;
			case STRAY:
				return Sounds.ENTITY_STRAY_DEATH;
			default:
				return Sounds.ENTITY_SKELETON_DEATH;
		}
	}

	public Sound method_13546() {
		switch (this) {
			case WITHER:
				return Sounds.ENTITY_WITHER_SKELETON_STEP;
			case STRAY:
				return Sounds.ENTITY_STRAY_STEP;
			default:
				return Sounds.ENTITY_SKELETON_STEP;
		}
	}
}
