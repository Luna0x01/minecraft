package net.minecraft.sound;

public class BlockSoundGroup {
	public static final BlockSoundGroup field_12759 = new BlockSoundGroup(
		1.0F, 1.0F, Sounds.BLOCK_WOOD_BREAK, Sounds.BLOCK_WOOD_STEP, Sounds.BLOCK_WOOD_PLACE, Sounds.BLOCK_WOOD_HIT, Sounds.BLOCK_WOOD_FALL
	);
	public static final BlockSoundGroup field_12760 = new BlockSoundGroup(
		1.0F, 1.0F, Sounds.BLOCK_GRAVEL_BREAK, Sounds.BLOCK_GRAVEL_STEP, Sounds.BLOCK_GRAVEL_PLACE, Sounds.BLOCK_GRAVEL_HIT, Sounds.BLOCK_GRAVEL_FALL
	);
	public static final BlockSoundGroup field_12761 = new BlockSoundGroup(
		1.0F, 1.0F, Sounds.BLOCK_GRASS_BREAK, Sounds.BLOCK_GRASS_STEP, Sounds.BLOCK_GRASS_PLACE, Sounds.BLOCK_GRASS_HIT, Sounds.BLOCK_GRASS_FALL
	);
	public static final BlockSoundGroup STONE = new BlockSoundGroup(
		1.0F, 1.0F, Sounds.BLOCK_STONE_BREAK, Sounds.BLOCK_STONE_STEP, Sounds.BLOCK_STONE_PLACE, Sounds.BLOCK_STONE_HIT, Sounds.BLOCK_STONE_FALL
	);
	public static final BlockSoundGroup field_12763 = new BlockSoundGroup(
		1.0F, 1.5F, Sounds.BLOCK_METAL_BREAK, Sounds.BLOCK_METAL_STEP, Sounds.BLOCK_METAL_PLACE, Sounds.BLOCK_METAL_HIT, Sounds.BLOCK_METAL_FALL
	);
	public static final BlockSoundGroup field_12764 = new BlockSoundGroup(
		1.0F, 1.0F, Sounds.BLOCK_GLASS_BREAK, Sounds.BLOCK_GLASS_STEP, Sounds.BLOCK_GLASS_PLACE, Sounds.BLOCK_GLASS_HIT, Sounds.BLOCK_GLASS_FALL
	);
	public static final BlockSoundGroup field_12765 = new BlockSoundGroup(
		1.0F, 1.0F, Sounds.BLOCK_WOOL_BREAK, Sounds.BLOCK_WOOL_STEP, Sounds.BLOCK_WOOL_PLACE, Sounds.BLOCK_WOOL_HIT, Sounds.BLOCK_WOOL_FALL
	);
	public static final BlockSoundGroup field_12766 = new BlockSoundGroup(
		1.0F, 1.0F, Sounds.BLOCK_SAND_BREAK, Sounds.BLOCK_SAND_STEP, Sounds.BLOCK_SAND_PLACE, Sounds.BLOCK_SAND_HIT, Sounds.BLOCK_SAND_FALL
	);
	public static final BlockSoundGroup field_12767 = new BlockSoundGroup(
		1.0F, 1.0F, Sounds.BLOCK_SNOW_BREAK, Sounds.BLOCK_SNOW_STEP, Sounds.BLOCK_SNOW_PLACE, Sounds.BLOCK_SNOW_HIT, Sounds.BLOCK_SNOW_FALL
	);
	public static final BlockSoundGroup field_12768 = new BlockSoundGroup(
		1.0F, 1.0F, Sounds.BLOCK_LADDER_BREAK, Sounds.BLOCK_LADDER_STEP, Sounds.BLOCK_LADDER_PLACE, Sounds.BLOCK_LADDER_HIT, Sounds.BLOCK_LADDER_FALL
	);
	public static final BlockSoundGroup field_12769 = new BlockSoundGroup(
		0.3F, 1.0F, Sounds.BLOCK_ANVIL_BREAK, Sounds.BLOCK_ANVIL_STEP, Sounds.BLOCK_ANVIL_PLACE, Sounds.BLOCK_ANVIL_HIT, Sounds.BLOCK_ANVIL_FALL
	);
	public static final BlockSoundGroup field_12770 = new BlockSoundGroup(
		1.0F,
		1.0F,
		Sounds.BLOCK_SLIME_BLOCK_BREAK,
		Sounds.BLOCK_SLIME_BLOCK_STEP,
		Sounds.BLOCK_SLIME_BLOCK_PLACE,
		Sounds.BLOCK_SLIME_BLOCK_HIT,
		Sounds.BLOCK_SLIME_BLOCK_FALL
	);
	public static final BlockSoundGroup field_18497 = new BlockSoundGroup(
		1.0F, 1.0F, Sounds.BLOCK_WET_GRASS_BREAK, Sounds.BLOCK_WET_GRASS_STEP, Sounds.BLOCK_WET_GRASS_PLACE, Sounds.BLOCK_WET_GRASS_HIT, Sounds.BLOCK_WET_GRASS_FALL
	);
	public static final BlockSoundGroup field_18498 = new BlockSoundGroup(
		1.0F,
		1.0F,
		Sounds.BLOCK_CORAL_BLOCK_BREAK,
		Sounds.BLOCK_CORAL_BLOCK_STEP,
		Sounds.BLOCK_CORAL_BLOCK_PLACE,
		Sounds.BLOCK_CORAL_BLOCK_HIT,
		Sounds.BLOCK_CORAL_BLOCK_FALL
	);
	public final float volume;
	public final float pitch;
	private final Sound field_12771;
	private final Sound field_506;
	private final Sound field_12772;
	private final Sound field_12773;
	private final Sound field_12774;

	public BlockSoundGroup(float f, float g, Sound sound, Sound sound2, Sound sound3, Sound sound4, Sound sound5) {
		this.volume = f;
		this.pitch = g;
		this.field_12771 = sound;
		this.field_506 = sound2;
		this.field_12772 = sound3;
		this.field_12773 = sound4;
		this.field_12774 = sound5;
	}

	public float getVolume() {
		return this.volume;
	}

	public float getPitch() {
		return this.pitch;
	}

	public Sound method_11629() {
		return this.field_12771;
	}

	public Sound getStepSound() {
		return this.field_506;
	}

	public Sound method_4194() {
		return this.field_12772;
	}

	public Sound method_11630() {
		return this.field_12773;
	}

	public Sound method_487() {
		return this.field_12774;
	}
}
