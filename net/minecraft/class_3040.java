package net.minecraft;

import net.minecraft.sound.Sound;
import net.minecraft.sound.Sounds;
import net.minecraft.text.TranslatableText;

public enum class_3040 {
	NORMAL("Zombie", false),
	VILLAGER_FARMER("Zombie", true),
	VILLAGER_LIBRARIAN("Zombie", true),
	VILLAGER_PRIEST("Zombie", true),
	VILLAGER_SMITH("Zombie", true),
	VILLAGER_BUTCHER("Zombie", true),
	HUSK("Husk", false);

	private boolean field_15055;
	private final TranslatableText field_15056;

	private class_3040(String string2, boolean bl) {
		this.field_15055 = bl;
		this.field_15056 = new TranslatableText("entity." + string2 + ".name");
	}

	public int method_13553() {
		return this.ordinal();
	}

	public boolean method_13555() {
		return this.field_15055;
	}

	public int method_13557() {
		return this.field_15055 ? this.method_13553() - 1 : 0;
	}

	public static class_3040 method_13554(int i) {
		return values()[i];
	}

	public static class_3040 method_13556(int i) {
		return i >= 0 && i < 5 ? method_13554(i + 1) : VILLAGER_FARMER;
	}

	public TranslatableText method_13558() {
		return this.field_15056;
	}

	public boolean method_13559() {
		return this != HUSK;
	}

	public Sound method_13560() {
		switch (this) {
			case HUSK:
				return Sounds.ENTITY_HUSK_AMBIENT;
			case VILLAGER_FARMER:
			case VILLAGER_LIBRARIAN:
			case VILLAGER_PRIEST:
			case VILLAGER_SMITH:
			case VILLAGER_BUTCHER:
				return Sounds.ENTITY_ZOMBIE_VILLAGER_AMBIENT;
			default:
				return Sounds.ENTITY_ZOMBIE_AMBIENT;
		}
	}

	public Sound method_13561() {
		switch (this) {
			case HUSK:
				return Sounds.ENTITY_HUSK_HURT;
			case VILLAGER_FARMER:
			case VILLAGER_LIBRARIAN:
			case VILLAGER_PRIEST:
			case VILLAGER_SMITH:
			case VILLAGER_BUTCHER:
				return Sounds.ENTITY_ZOMBIE_VILLAGER_HURT;
			default:
				return Sounds.ENTITY_ZOMBIE_HURT;
		}
	}

	public Sound method_13562() {
		switch (this) {
			case HUSK:
				return Sounds.ENTITY_HUSK_DEATH;
			case VILLAGER_FARMER:
			case VILLAGER_LIBRARIAN:
			case VILLAGER_PRIEST:
			case VILLAGER_SMITH:
			case VILLAGER_BUTCHER:
				return Sounds.ENTITY_ZOMBIE_VILLAGER_DEATH;
			default:
				return Sounds.ENTITY_ZOMBIE_DEATH;
		}
	}

	public Sound method_13563() {
		switch (this) {
			case HUSK:
				return Sounds.ENTITY_HUSK_STEP;
			case VILLAGER_FARMER:
			case VILLAGER_LIBRARIAN:
			case VILLAGER_PRIEST:
			case VILLAGER_SMITH:
			case VILLAGER_BUTCHER:
				return Sounds.ENTITY_ZOMBIE_VILLAGER_STEP;
			default:
				return Sounds.ENTITY_ZOMBIE_STEP;
		}
	}
}
