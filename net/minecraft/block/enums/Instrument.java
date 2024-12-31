package net.minecraft.block.enums;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.material.Material;
import net.minecraft.sound.Sound;
import net.minecraft.sound.Sounds;
import net.minecraft.tag.BlockTags;
import net.minecraft.util.StringIdentifiable;

public enum Instrument implements StringIdentifiable {
	HARP("harp", Sounds.BLOCK_NOTE_BLOCK_HARP),
	BASEDRUM("basedrum", Sounds.BLOCK_NOTE_BLOCK_BASEDRUM),
	SNARE("snare", Sounds.BLOCK_NOTE_BLOCK_SNARE),
	HAT("hat", Sounds.BLOCK_NOTE_BLOCK_HAT),
	BASS("bass", Sounds.BLOCK_NOTE_BLOCK_BASS),
	FLUTE("flute", Sounds.BLOCK_NOTE_BLOCK_FLUTE),
	BELL("bell", Sounds.BLOCK_NOTE_BLOCK_BELL),
	GUITAR("guitar", Sounds.BLOCK_NOTE_BLOCK_GUITAR),
	CHIME("chime", Sounds.BLOCK_NOTE_BLOCK_CHIME),
	XYLOPHONE("xylophone", Sounds.BLOCK_NOTE_BLOCK_XYLOPHONE);

	private final String name;
	private final Sound sound;

	private Instrument(String string2, Sound sound) {
		this.name = string2;
		this.sound = sound;
	}

	@Override
	public String asString() {
		return this.name;
	}

	public Sound asSound() {
		return this.sound;
	}

	public static Instrument getByBlockState(BlockState state) {
		Block block = state.getBlock();
		if (block == Blocks.CLAY) {
			return FLUTE;
		} else if (block == Blocks.GOLD_BLOCK) {
			return BELL;
		} else if (block.isIn(BlockTags.WOOL)) {
			return GUITAR;
		} else if (block == Blocks.PACKED_ICE) {
			return CHIME;
		} else if (block == Blocks.BONE_BLOCK) {
			return XYLOPHONE;
		} else {
			Material material = state.getMaterial();
			if (material == Material.STONE) {
				return BASEDRUM;
			} else if (material == Material.SAND) {
				return SNARE;
			} else if (material == Material.GLASS) {
				return HAT;
			} else {
				return material == Material.WOOD ? BASS : HARP;
			}
		}
	}
}
