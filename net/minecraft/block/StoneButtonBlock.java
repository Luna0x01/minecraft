package net.minecraft.block;

import net.minecraft.sound.Sound;
import net.minecraft.sound.Sounds;

public class StoneButtonBlock extends AbstractButtonBlock {
	protected StoneButtonBlock(Block.Builder builder) {
		super(false, builder);
	}

	@Override
	protected Sound getClickSound(boolean powered) {
		return powered ? Sounds.BLOCK_STONE_BUTTON_CLICK_ON : Sounds.BLOCK_STONE_BUTTON_CLICK_OFF;
	}
}
