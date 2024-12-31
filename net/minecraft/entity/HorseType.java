package net.minecraft.entity;

import net.minecraft.loot.LootTables;
import net.minecraft.sound.Sound;
import net.minecraft.sound.Sounds;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Identifier;

public enum HorseType {
	HORSE("EntityHorse", "horse_white", Sounds.ENTITY_HORSE_AMBIENT, Sounds.ENTITY_HORSE_HURT, Sounds.ENTITY_HORSE_DEATH, LootTables.HORSE_ENTITIE),
	DONKEY("Donkey", "donkey", Sounds.ENTITY_DONKEY_AMBIENT, Sounds.ENTITY_DONKEY_HURT, Sounds.ENTITY_DONKEY_DEATH, LootTables.HORSE_ENTITIE),
	MULE("Mule", "mule", Sounds.ENTITY_MULE_AMBIENT, Sounds.ENTITY_MULE_HURT, Sounds.ENTITY_MULE_DEATH, LootTables.HORSE_ENTITIE),
	ZOMBIE(
		"ZombieHorse",
		"horse_zombie",
		Sounds.ENTITY_ZOMBIE_HORSE_AMBIENT,
		Sounds.ENTITY_ZOMBIE_HORSE_HURT,
		Sounds.ENTITY_ZOMBIE_HORSE_DEATH,
		LootTables.ZOMBIE_HORSE_ENTITIE
	),
	SKELETON(
		"SkeletonHorse",
		"horse_skeleton",
		Sounds.ENTITY_SKELETON_HORSE_AMBIENT,
		Sounds.ENTITY_SKELETON_HORSE_HURT,
		Sounds.ENTITY_SKELETON_HORSE_DEATH,
		LootTables.SKELETON_HORSE_ENTITIE
	);

	private final TranslatableText name;
	private final Identifier texturePath;
	private final Sound field_14653;
	private final Sound field_14654;
	private final Sound field_14655;
	private final Identifier lootTable;

	private HorseType(String string2, String string3, Sound sound, Sound sound2, Sound sound3, Identifier identifier) {
		this.name = new TranslatableText("entity." + string2 + ".name");
		this.texturePath = new Identifier("textures/entity/horse/" + string3 + ".png");
		this.field_14653 = sound2;
		this.field_14654 = sound;
		this.field_14655 = sound3;
		this.lootTable = identifier;
	}

	public Sound method_13142() {
		return this.field_14654;
	}

	public Sound method_13144() {
		return this.field_14653;
	}

	public Sound method_13145() {
		return this.field_14655;
	}

	public TranslatableText getName() {
		return this.name;
	}

	public Identifier getTexturePath() {
		return this.texturePath;
	}

	public boolean method_13148() {
		return this == DONKEY || this == MULE;
	}

	public boolean method_13149() {
		return this == DONKEY || this == MULE;
	}

	public boolean undead() {
		return this == ZOMBIE || this == SKELETON;
	}

	public boolean method_13151() {
		return !this.undead() && this != MULE;
	}

	public boolean method_13152() {
		return this == HORSE;
	}

	public int method_13153() {
		return this.ordinal();
	}

	public static HorseType method_13143(int i) {
		return values()[i];
	}

	public Identifier getLootTable() {
		return this.lootTable;
	}
}
