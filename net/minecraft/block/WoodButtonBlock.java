package net.minecraft.block;

import net.minecraft.sound.Sound;
import net.minecraft.sound.Sounds;

public class WoodButtonBlock extends AbstractButtonBlock {
	protected WoodButtonBlock(Block.Builder builder) {
		super(true, builder);
	}

	@Override
	protected Sound getClickSound(boolean powered) {
		return powered ? Sounds.BLOCK_WOODEN_BUTTON_CLICK_ON : Sounds.BLOCK_WOODEN_BUTTON_CLICK_OFF;
	}
}
